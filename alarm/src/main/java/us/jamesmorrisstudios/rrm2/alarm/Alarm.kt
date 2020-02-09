package us.jamesmorrisstudios.rrm2.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.room.Room
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import us.jamesmorrisstudios.rrm2.alarm.receiver.AlarmReceiver
import us.jamesmorrisstudios.rrm2.log.Log
import us.jamesmorrisstudios.rrm2.log.stringifyStacktrace
import us.jamesmorrisstudios.rrm2.util.*

/**
 * System Alarm Scheduler.
 *
 * This maintains scheduled alarms across reboots.
 */
interface Alarm {

    companion object {

        /**
         * The Alarm scheduler instance.
         */
        val instance: Alarm by lazy { AlarmImpl() }
    }

    /**
     * Initialize the alarm scheduler. Do this in the Application.onCreate method.
     */
    suspend fun initialize(context: Context)

    /**
     * Alarm subscription channel. Receive from this using the alarm handler.
     */
    fun subscription(): ReceiveChannel<Guid>

    /**
     * Schedule an alarm with the given unique guid at the given time.
     */
    suspend fun add(guid: Guid, time: Long)

    /**
     * Cancel a previously scheduled alarm.
     */
    suspend fun remove(guid: Guid)

    /**
     * Internal function called when an alarm is fired.
     */
    suspend fun onReceive(guid: Guid)

    /**
     * Opens the battery optimization settings screen so the user can whitelist the app.
     */
    fun openBatteryOptimizationSettings()

    /**
     * Opens the battery optimization settings screen so the user can whitelist the app.
     *
     * Note: This method may require additional explanation on Google Play when publishing. See https://developer.android.com/training/monitoring-device-state/doze-standby.html#support_for_other_use_cases
     */
    fun openBatteryOptimizationDialog()

    /**
     * Returns if the user has whitelisted this app from battery optimizations.
     */
    fun isBatteryOptimizationsWhitelisted(): Boolean

}

/**
 * Implementation of the Alarm scheduler.
 */
private class AlarmImpl : Alarm {
    private val log = Log.instance.buildLogClass(BuildConfig.MODULE_NAME, "AlarmImpl")
    private val deferredUntilInitialized = CompletableDeferred<Unit>()
    private val lock: Mutex = Mutex()
    private val subscription = Channel<Guid>(50)
    private lateinit var context: Context
    private lateinit var prefs: Prefs
    private lateinit var db: AlarmDb

    /**
     * {inherited}
     */
    override suspend fun initialize(context: Context) {
        this.context = context.applicationContext
        log.info("initialize")

        lock.withLock {
            // Initialize storage.
            prefs = Prefs.build(this.context, BuildConfig.PREFS_NAME)
            db = Room.databaseBuilder(this.context, AlarmDb::class.java, BuildConfig.DB_NAME).build()

            // Retrieve when we last launched.
            val keyLastLaunch = "time_last_launch"
            val timeLastLaunch = prefs.getLong(keyLastLaunch)

            // Notify for any alarms that are past due.
            notifyPastDue()

            // Check if the device has been rebooted since we last scheduled the wake timers.
            if (isRebootedSince(timeLastLaunch)) {
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
    override fun subscription(): ReceiveChannel<Guid> {
        return subscription
    }

    /**
     * {inherited}
     */
    override suspend fun add(guid: Guid, time: Long) {
        deferredUntilInitialized.await()
        lock.withLock {
            // Remove if this alarm already exists.
            db.alarmDao().findByGuid(guid)?.let {
                db.alarmDao().delete(it)
                cancelAlarmManager(it)
            }

            // Create and schedule new alarm
            var alarm = AlarmItem(guid = guid, time = time)
            val id = db.alarmDao().insert(alarm)
            alarm = alarm.copy(id = id)
            log.verbose { "add $alarm" }
            scheduleAlarmManager(alarm)
        }
    }

    /**
     * {inherited}
     */
    override suspend fun remove(guid: Guid) {
        deferredUntilInitialized.await()
        lock.withLock {
            val alarm = db.alarmDao().findByGuid(guid) ?: return
            log.verbose { "remove $alarm" }
            db.alarmDao().delete(alarm)
            cancelAlarmManager(alarm)
        }
    }

    /**
     * {inherited}
     */
    override suspend fun onReceive(guid: Guid) {
        deferredUntilInitialized.await()
        lock.withLock {
            val alarm = db.alarmDao().findByGuid(guid) ?: return
            log.verbose { "onReceive $alarm" }
            db.alarmDao().delete(alarm)
            notify(alarm.guid)
        }
    }

    /**
     * {inherited}
     */
    override fun openBatteryOptimizationSettings() {
        runCatching {
            val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            ContextCompat.startActivity(context, intent, null)
        }.exceptionOrNull()?.let {
            log.warn { it.stringifyStacktrace() }
        }
    }

    /**
     * {inherited}
     */
    @SuppressLint("BatteryLife")
    override fun openBatteryOptimizationDialog() {
        runCatching {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + context.packageName)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            ContextCompat.startActivity(context, intent, null)
        }.exceptionOrNull()?.let {
            log.warn { it.stringifyStacktrace() }
        }
    }

    /**
     * {inherited}
     */
    override fun isBatteryOptimizationsWhitelisted(): Boolean {
        val powerManager: PowerManager? = context.getSystemService()
        powerManager?.let {
            return it.isIgnoringBatteryOptimizations(context.packageName)
        }
        return false
    }

    /**
     * Retrieves all alarms that are due up to the current minute.
     *
     * On an app launch this may fire an alarm right before the onReceive method is called.
     */
    private suspend fun notifyPastDue() {
        val timeNow = currentTimeMillis()
        db.alarmDao().getWithTimeBefore(timeNow).forEach { alarm ->
            log.debug { "notifyPastDue $alarm" }
            db.alarmDao().delete(alarm)
            notify(alarm.guid)
        }
    }

    /**
     * Applies every scheduled alarm with the system alarm manager.
     */
    private suspend fun applyAll() {
        log.debug("applyAll")
        db.alarmDao().getAll().forEach { alarm ->
            scheduleAlarmManager(alarm)
        }
    }

    /**
     * Notifies the subscription channel that an alarm has fired.
     */
    private suspend fun notify(guid: Guid) {
        log.verbose("notify $guid")
        subscription.send(guid)
    }

    /**
     * Schedule the alarm at the given time.
     */
    private fun scheduleAlarmManager(alarm: AlarmItem) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildPendingIntent(alarm)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.time, pendingIntent)
    }

    /**
     * Cancel the alarm.
     */
    private fun cancelAlarmManager(alarm: AlarmItem) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildPendingIntent(alarm)
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Builds a pending intent for the given alarm.
     */
    private fun buildPendingIntent(alarm: AlarmItem): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            this.type = alarm.guid.toString()
            this.action = context.packageName + BuildConfig.RECEIVER_ACTION
        }
        return PendingIntent.getBroadcast(context, alarm.id, intent, 0)
    }

}