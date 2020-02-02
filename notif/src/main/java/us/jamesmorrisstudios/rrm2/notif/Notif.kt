package us.jamesmorrisstudios.rrm2.notif

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.media.AudioAttributes
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.room.Room
import coil.Coil
import coil.api.get
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import us.jamesmorrisstudios.rrm2.log.Log
import us.jamesmorrisstudios.rrm2.log.stringifyStacktrace
import us.jamesmorrisstudios.rrm2.notif.receiver.ActionClickActivity
import us.jamesmorrisstudios.rrm2.notif.receiver.ActionReceiver
import us.jamesmorrisstudios.rrm2.util.*

/**
 * Notification Action Types
 */
enum class NotifActionType {

    /**
     * Notification clicked on.
     *
     * The notification will no longer be visible and the record will have been automatically deleted.
     */
    Click,

    /**
     * Notification Dismissed
     *
     * The notification will no longer be visible and the record will have been automatically deleted.
     */
    Dismiss,

    /**
     * Notification action complete
     *
     * The notification will still be visible and the record persisted.
     */
    ActionComplete,

    /**
     * Notification action incomplete
     *
     * The notification will still be visible and the record persisted.
     */
    ActionIncomplete,

    /**
     * Notification action snooze
     *
     * The notification will still be visible and the record persisted.
     */
    ActionSnooze

}

/**
 * Notification action response
 */
data class NotifResponse(val guid: Guid, val delay: Long, val action: NotifActionType)

/**
 * Notification manager.
 */
interface Notif {

    companion object {

        /**
         * The Alarm scheduler instance.
         */
        val instance: Notif by lazy { NotifImpl() }
    }

    /**
     * Initialize the alarm scheduler. Do this in the Application.onCreate method.
     */
    suspend fun initialize(context: Context)

    /**
     * Notifications subscription channel. Receive from this using the notifications handler.
     *
     * TODO remove in favor of getFlow
     */
    fun subscription(): ReceiveChannel<NotifResponse>

    /**
     * Returns the flow of events.
     *
     * Typically used as getFlow().collect { response ->  }
     *
     * TODO not working yet with the unstable kotlin compiler version used by jetpack compose
     *     https://github.com/Kotlin/kotlinx.coroutines/issues/1637
     */
    //fun getFlow(): Flow<NotifResponse>

    /**
     * Creates a new notification but does not show it.
     */
    suspend fun add(guid: Guid, notification: Notification)

    /**
     * Removes a notification. If it is visible it will be removed from the notification shade.
     */
    suspend fun remove(guid: Guid)

    /**
     * Returns if a notification exists.
     *
     * @return if the notification exists.
     */
    suspend fun has(guid: Guid): Boolean

    /**
     * Shows an existing notification. If already visible this will show it again.
     *
     * @return if the notification exists.
     */
    suspend fun show(guid: Guid): Boolean

    /**
     * Hides an existing notification.
     *
     * @return if the notification exists.
     */
    suspend fun hide(guid: Guid): Boolean

    /**
     * Returns if an existing notification is currently visible.
     *
     * @return if the notification exists and is visible.
     */
    suspend fun isVisible(guid: Guid): Boolean

    /**
     * Internal function called when an alarm is fired.
     */
    suspend fun onReceive(guid: Guid, action: NotifActionType)

    /**
     * Retrieve the notification channel with the given id.
     *
     * Will be null on Android versions prior to api 26.
     */
    fun getNotificationChannel(id: String): NotificationChannel?

    /**
     * Deletes the notification channel with the given id.
     *
     * Does nothing on Android versions prior to api 26.
     */
    fun deleteNotificationChannel(id: String)

    /**
     * Opens the notification channel settings page for the channel with the given id.
     *
     * Does nothing on Android versions prior to api 26.
     */
    fun openNotificationChannelSettings(id: String)

}

/**
 * Implementation of the notification manager.
 */
private class NotifImpl : Notif {
    private val log = Log.instance.buildLogClass(BuildConfig.MODULE_NAME, "NotifImpl")
    private val deferredUntilInitialized = CompletableDeferred<Unit>()
    private val lock: Mutex = Mutex()
    private val subscription = Channel<NotifResponse>(50)
    private lateinit var context: Context
    private lateinit var prefs: Prefs
    private lateinit var db: NotifDb

    /**
     * {inherited}
     */
    override suspend fun initialize(context: Context) {
        this.context = context.applicationContext
        log.info("initialize")

        lock.withLock {
            // Initialize storage.
            prefs = Prefs.build(this.context, BuildConfig.PREFS_NAME)
            db = Room.databaseBuilder(this.context, NotifDb::class.java, BuildConfig.DB_NAME).build()

            // Retrieve when we last launched.
            val keyLastLaunch = "time_last_launch"
            val timeLastLaunch = prefs.getLong(keyLastLaunch)

            // Check if the device has been rebooted since we last set notifications.
            if(isRebootedSince(timeLastLaunch)) {
                applyAll()
            }

            // Update last launch time.
            prefs.setLong(keyLastLaunch, currentTimeMillis())
        }
        deferredUntilInitialized.complete(Unit)
    }

    /**
     * {inherited}
     */
    override fun subscription(): ReceiveChannel<NotifResponse> {
        return subscription
    }

    /**
     * {inherited}
     */
    //override fun getFlow(): Flow<NotifResponse> = subscription.consumeAsFlow()

    /**
     * {inherited}
     */
    override suspend fun add(guid: Guid, notification: Notification) {
        deferredUntilInitialized.await()
        lock.withLock {
            // Remove if this notification already exists.
            db.notifDao().findByGuid(guid)?.let {
                db.notifDao().delete(it)
                cancelNotification(it)
            }

            // Add but do not show the new notification
            var notif = NotifItem(guid = guid, notification = notification)
            val id = db.notifDao().insert(notif)
            notif = notif.copy(id = id)
            log.verbose { "add $notif" }
        }
    }

    /**
     * {inherited}
     */
    override suspend fun remove(guid: Guid) {
        deferredUntilInitialized.await()
        lock.withLock {
            val notif = db.notifDao().findByGuid(guid) ?: return
            log.verbose { "remove $notif" }
            db.notifDao().delete(notif)
            cancelNotification(notif)
        }
    }

    /**
     * {inherited}
     */
    override suspend fun has(guid: Guid): Boolean {
        deferredUntilInitialized.await()
        lock.withLock {
            return db.notifDao().findByGuid(guid) != null
        }
    }

    /**
     * {inherited}
     */
    override suspend fun show(guid: Guid): Boolean {
        deferredUntilInitialized.await()
        lock.withLock {
            // Get the notif entry
            val originalNotif = db.notifDao().findByGuid(guid) ?: return false
            // Update the notif with visibility set.
            val notif = originalNotif.copy(showTime = currentTimeMillis(), visible = true)
            db.notifDao().update(notif)
            // Show the notification
            showNotification(notif)
            log.verbose { "show $notif" }
            return true
        }
    }

    /**
     * {inherited}
     */
    override suspend fun hide(guid: Guid): Boolean {
        deferredUntilInitialized.await()
        lock.withLock {
            // Get the notif entry
            val originalNotif = db.notifDao().findByGuid(guid) ?: return false
            // Update the notif with visibility set.
            val notif = originalNotif.copy(showTime = 0, visible = false)
            db.notifDao().update(notif)
            // Hide the notification
            cancelNotification(notif)
            log.verbose { "hide $notif" }
            return true
        }
    }

    /**
     * {inherited}
     */
    override suspend fun isVisible(guid: Guid): Boolean {
        deferredUntilInitialized.await()
        lock.withLock {
            val notif = db.notifDao().findByGuid(guid) ?: return false
            return notif.visible
        }
    }

    /**
     * {inherited}
     */
    override suspend fun onReceive(guid: Guid, action: NotifActionType) {
        deferredUntilInitialized.await()
        lock.withLock {
            val notif = db.notifDao().findByGuid(guid) ?: return
            log.verbose { "onReceive $notif" }
            // If the notification was clicked or dismissed its gone and we can remove it here. Otherwise keep it around until we are told to do something with it.
            if(action == NotifActionType.Click || action == NotifActionType.Dismiss) {
                db.notifDao().delete(notif)
            }
            notify(notif, action)
        }
    }

    /**
     * {inherited}
     */
    override fun getNotificationChannel(id: String): NotificationChannel? {
        val notificationManager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // App versions
            val appChannel = notificationManager.getNotificationChannel(id) ?: return null
            val appGroup = notificationManager.getNotificationChannelGroup(appChannel.group)

            // Build the channel.
            val group = appGroup?.let { NotificationChannelGroup(it.id, it.name.toString()) }
            return NotificationChannel(
                id = appChannel.id,
                name = appChannel.name.toString(),
                importance = appChannel.importance.toNotificationImportance(),
                description = appChannel.description,
                badge = appChannel.canShowBadge(),
                vibration = appChannel.vibrationPattern.takeIf { appChannel.shouldVibrate() },
                lights = appChannel.lightColor.takeIf { appChannel.shouldShowLights() },
                sound = appChannel.sound,
                group = group
            )
        }
        return null
    }

    /**
     * {inherited}
     */
    override fun deleteNotificationChannel(id: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.deleteNotificationChannel(id)
        }
    }

    /**
     * {inherited}
     */
    override fun openNotificationChannelSettings(id: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            runCatching {
                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    putExtra(Settings.EXTRA_CHANNEL_ID, id)
                }
                startActivity(context, intent, null)
            }.exceptionOrNull()?.let {
                log.warn { it.stringifyStacktrace() }
            }
        }
    }

    /**
     * Applies every visible notification.
     */
    private suspend fun applyAll() {
        log.debug("applyAll")
        db.notifDao().getAll().forEach { notif ->
            if(notif.visible) {
                showNotification(notif)
            }
        }
    }

    /**
     * Notifies the subscription channel that a notification action has occurred.
     */
    private suspend fun notify(notif: NotifItem, action: NotifActionType) {
        val delay = currentTimeMillis() - notif.showTime
        val notifResponse = NotifResponse(guid = notif.guid, delay = delay, action = action)
        log.verbose { "notify $notifResponse" }
        subscription.send(notifResponse)
    }

    /**
     * Shows a given notification.
     */
    private suspend fun showNotification(notif: NotifItem) {
        val notification = notif.notification

        // Register the notification channel.
        registerAppNotificationChannel(notification.channel)

        // Build and display the notification.
        val appNotification = buildAppNotification(notif.guid, notification)
        with(NotificationManagerCompat.from(context)) {
            notify(notif.id, appNotification)
        }
    }

    /**
     * Cancels the notification with the system.
     */
    private fun cancelNotification(notif: NotifItem) {
        with(NotificationManagerCompat.from(context)) {
            cancel(notif.id)
        }
    }

    /**
     * Builds the notification into the platform notification object.
     */
    private suspend fun buildAppNotification(guid: Guid, notification: Notification): android.app.Notification {
        // Builder
        val builder = NotificationCompat.Builder(context, notification.channel.id)
            .setAutoCancel(true)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setPriority(notification.channel.importance.toPriority())

        // If an image exists download and use it.
        val image = notification.image?.let {
            runCatching {
                Coil.get(it).toBitmap()
            }.getOrNull()
        }

        if(image != null) {
            // Image
            builder.setLargeIcon(image)
            builder.setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(image)
                .bigLargeIcon(null)
            )
        } else {
            // Long text
            builder.setStyle(NotificationCompat.BigTextStyle()
                .bigText(notification.message)
            )
        }

        // Vibration
        notification.channel.vibration?.let {
            builder.setVibrate(it)
        }

        // LED Lights
        notification.channel.lights?.let {
            builder.setLights(it, 500, 500)
        }

        // Sound
        notification.channel.sound?.let {
            builder.setSound(it)
        }

        // Badge
        if(notification.channel.badge) {
            builder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
        }

        // Click and Dismiss intents.
        builder.setContentIntent(PendingIntent.getActivity(context, 0, Intent(context, ActionClickActivity::class.java).apply { action = context.packageName + BuildConfig.ACTION_TYPE_CLICK; type = guid.toString() }, 0))
        builder.setDeleteIntent(PendingIntent.getBroadcast(context, 1, Intent(context, ActionReceiver::class.java).apply { action = context.packageName + BuildConfig.ACTION_TYPE_DISMISS; type = guid.toString() }, 0))

        // Action intents
        notification.actionComplete?.let {
            val intent = PendingIntent.getBroadcast(context, 2, Intent(context, ActionReceiver::class.java).apply { action = context.packageName + BuildConfig.ACTION_TYPE_ACTION_COMPLETE; type = guid.toString() }, 0)
            builder.addAction(NotificationCompat.Action(it.iconId, it.text, intent))
        }
        notification.actionIncomplete?.let {
            val intent = PendingIntent.getBroadcast(context, 3, Intent(context, ActionReceiver::class.java).apply { action = context.packageName + BuildConfig.ACTION_TYPE_ACTION_INCOMPLETE; type = guid.toString() }, 0)
            builder.addAction(NotificationCompat.Action(it.iconId, it.text, intent))
        }
        notification.actionSnooze?.let {
            val intent = PendingIntent.getBroadcast(context, 4, Intent(context, ActionReceiver::class.java).apply { action = context.packageName + BuildConfig.ACTION_TYPE_ACTION_SNOOZE; type = guid.toString() }, 0)
            builder.addAction(NotificationCompat.Action(it.iconId, it.text, intent))
        }

        // Recover builder to set some newer feature items.
        val builder2 = android.app.Notification.Builder.recoverBuilder(context, builder.build()).apply {

            // Small icon
            val smallIconImage = runCatching {
                Coil.get(notification.smallIcon).toBitmap(width = 24.dpToPx(context), height = 24.dpToPx(context))
            }.getOrNull()

            if(smallIconImage != null) {
                setSmallIcon(Icon.createWithBitmap(smallIconImage))
            } else {
                setSmallIcon(R.drawable.ic_notif_fallback)
            }
        }

        return builder2.build()
    }

    /**
     * Builds and registers the notification channel and group with the system if on Android 8+
     */
    private fun registerAppNotificationChannel(notificationChannel: NotificationChannel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = NotificationManagerCompat.from(context)

            // Build the group if one exists.
            notificationChannel.group?.let {
                val appGroup = android.app.NotificationChannelGroup(it.id, it.name)
                notificationManager.createNotificationChannelGroup(appGroup)
            }

            // Build the channel
            val appChannel = with(notificationChannel) {
                android.app.NotificationChannel(id, name, notificationChannel.importance.toImportance()).apply {
                    // Description
                    this.description = description

                    // Vibration
                    notificationChannel.vibration?.let {
                        this.vibrationPattern = it
                        this.enableVibration(true)
                    }

                    // Lights (not supported on many devices these days).
                    notificationChannel.lights?.let {
                        this.lightColor = it
                        this.enableLights(true)
                    }

                    // Sound
                    notificationChannel.sound?.let {
                        this.setSound(it, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT).build())
                    }

                    // Icon badge
                    this.setShowBadge(notificationChannel.badge)

                    // Group
                    notificationChannel.group?.let {
                        this.group = it.id
                    }
                }
            }
            notificationManager.createNotificationChannel(appChannel)
        }
    }

}
