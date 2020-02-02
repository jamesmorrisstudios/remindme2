package us.jamesmorrisstudios.rrm2.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import us.jamesmorrisstudios.rrm2.alarm.Alarm
import us.jamesmorrisstudios.rrm2.alarm.BuildConfig
import us.jamesmorrisstudios.rrm2.log.Log
import us.jamesmorrisstudios.rrm2.util.Guid
import us.jamesmorrisstudios.rrm2.util.toGuid

/**
 * Alarm Receiver that is called by the system any time a scheduled alarm fires.
 *
 * It validates the alarm and passes the type to the main Alarm instance.
 */
class AlarmReceiver : BroadcastReceiver() {
    private val log = Log.instance.buildLogClass(BuildConfig.MODULE_NAME, "AlarmReceiver")

    /**
     * System intent received for an alarm being fired.
     */
    override fun onReceive(context: Context, intent: Intent?) {
        log.verbose("onReceive")

        if (intent == null) {
            log.warn("Invalid intent")
            return
        }

        if (intent.action != context.packageName + BuildConfig.RECEIVER_ACTION) {
            log.warn("Invalid action")
            return
        }

        val guid = intent.type
        if (guid.isNullOrBlank()) {
            log.warn("Invalid guid")
            return
        }

        GlobalScope.launch {
            Alarm.instance.onReceive(guid.toGuid())
        }
    }

}