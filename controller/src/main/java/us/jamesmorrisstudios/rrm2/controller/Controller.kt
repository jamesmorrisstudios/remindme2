package us.jamesmorrisstudios.rrm2.controller

import android.content.Context
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import us.jamesmorrisstudios.rrm2.alarm.Alarm
import us.jamesmorrisstudios.rrm2.log.Log
import us.jamesmorrisstudios.rrm2.notif.*
import us.jamesmorrisstudios.rrm2.storage.Storage
import us.jamesmorrisstudios.rrm2.storage.reminder.ReminderItem
import us.jamesmorrisstudios.rrm2.util.Guid
import us.jamesmorrisstudios.rrm2.util.currentTimeMillis
import us.jamesmorrisstudios.rrm2.util.resourceIdToUri

/**
 * Manager that subscribes to events from the alarm, notif, and storage subsystems and ensures everything is properly scheduled all the time.
 *
 * See the various method descriptions below for specific tasks.
 */
interface Controller {
    companion object {

        /**
         * The Reminder Manager instance.
         */
        val instance: Controller by lazy { ControllerImpl() }
    }

    /**
     * Initialize the reminder controller. Do this in the Application.onCreate method.
     */
    suspend fun initialize(context: Context, alarm: Alarm, notif: Notif, storage: Storage)

}

/**
 *
 */
private class ControllerImpl : Controller {
    private val log = Log.instance.buildLogClass(BuildConfig.MODULE_NAME, "ControllerImpl")
    private lateinit var context: Context
    private lateinit var alarm: Alarm
    private lateinit var notif: Notif
    private lateinit var storage: Storage

    /**
     * {inherited}
     */
    override suspend fun initialize(context: Context, alarm: Alarm, notif: Notif, storage: Storage) {
        this.context = context
        this.alarm = alarm
        this.notif = notif
        this.storage = storage

        GlobalScope.launch {
            while (true) {
                onAlarm(alarm.subscription().receive())
            }
        }

        GlobalScope.launch {
            while (true) {
                onNotif(notif.subscription().receive())
            }
        }

        // TODO Subscribe to Storage Reminder, Storage Schedule
    }

    /**
     * Alarm was fired which means a notification should be displayed.
     *
     * Get the base guid
     *
     * Cancel snooze alarm
     *
     * Cancel auto snooze alarm
     *
     * Lookup the reminder from storage
     *
     * Regular Alarm
     *   Build the Notification and add it to the Notif handler.
     *
     *   Calculate the next regular wake time and schedule it.
     *
     *   If enabled schedule the auto snooze alarm
     *
     * Snooze Alarm
     *   Tell the Notif handler to show on the base guid.
     *
     *   If enabled schedule the auto snooze alarm
     *
     * Auto Snooze Alarm
     *   Tell the Notif handler to show on the base guid.
     *
     *   If enabled schedule the auto snooze alarm
     */
    private suspend fun onAlarm(guid: Guid) {
        log.debug("Alarm fired for guid $guid")

        // Get the various guids.
        val baseGuid = guid.getBase()
        val regularGuid = guid.withModifierAlarmRegular()
        val snoozeGuid = guid.withModifierAlarmSnooze()
        val autoSnoozeGuid = guid.withModifierAlarmAutoSnooze()

        // Cancel any snooze alarms that may exist.
        alarm.remove(snoozeGuid)
        alarm.remove(autoSnoozeGuid)

        // Lookup the reminder.
        val reminder = storage.reminder().get(baseGuid) ?: return

        // Perform the action for the specific alarm type.
        when(guid) {
            // TODO this code is experimental and not at al final.
            regularGuid -> {
                notif.add(baseGuid, reminder.buildNotification())
                notif.show(baseGuid)

                alarm.add(regularGuid, currentTimeMillis() + 3 * 60 * 60 * 1000)

                alarm.add(autoSnoozeGuid, currentTimeMillis() + 5 * 60 * 1000)
            }
            snoozeGuid -> {
                notif.show(baseGuid)
                alarm.add(snoozeGuid, currentTimeMillis() + 15 * 60 * 1000)
            }
            autoSnoozeGuid -> {
                notif.hide(baseGuid)
                notif.show(baseGuid)
                alarm.add(autoSnoozeGuid, currentTimeMillis() + 5 * 60 * 1000)
            }
        }
    }

    /**
     * Notif action fired which means a notification was interacted with.
     *
     * If the notification was clicked post event indicating the app should launch.
     *
     * ??? If supporting snooze and its snooze schedule the next wake for this reminder as being a snooze.
     *
     * Log the action and delay into the History storage.
     *
     */
    private suspend fun onNotif(notifResponse: NotifResponse) {
        log.debug("Notif fired for $notifResponse")

        // Get the base guid.
        val baseGuid = notifResponse.guid.getBase()

        // Lookup the reminder.
        val reminder = storage.reminder().get(baseGuid) ?: return

        // Perform the action for the specific notif action.
        when(notifResponse.action) {
            NotifActionType.Click -> {

            }
            NotifActionType.Dismiss -> {

            }
            NotifActionType.ActionComplete -> {

            }
            NotifActionType.ActionIncomplete -> {

            }
            NotifActionType.ActionSnooze -> {

            }
        }
    }

    /**
     *
     */
    private fun onStorageReminder() {

    }

    /**
     *
     */
    private fun onStorageSchedule() {

    }

    private fun ReminderItem.buildNotification(): Notification {
        return Notification(
            smallIcon = R.drawable.ic_alert.resourceIdToUri(context),
            title = title,
            message = description,
            channel = NotificationChannel(
                id = "aaa-bbb",
                name = "My Reminder",
                importance = NotificationImportance.Default,
                description = "",
                group = NotificationChannelGroup(
                    id = "aaa-bbb-ccc",
                    name = "Reminders"
                )
            )//,
            //action1 = NotificationAction(R.drawable.ic_baseline_block_24, "Complete"),
            //action2 = NotificationAction(R.drawable.ic_baseline_block_24, "Incomplete")
        )
    }

}