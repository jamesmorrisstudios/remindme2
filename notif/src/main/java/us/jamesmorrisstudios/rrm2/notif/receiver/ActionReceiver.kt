package us.jamesmorrisstudios.rrm2.notif.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import us.jamesmorrisstudios.rrm2.log.Log
import us.jamesmorrisstudios.rrm2.notif.BuildConfig
import us.jamesmorrisstudios.rrm2.notif.Notif
import us.jamesmorrisstudios.rrm2.notif.NotifActionType
import us.jamesmorrisstudios.rrm2.util.toGuid

/**
 * Notification Action Receiver that is called by the system any time a notification is interacted with.
 */
class ActionReceiver : BroadcastReceiver() {
    private val log = Log.instance.buildLogClass(BuildConfig.MODULE_NAME, "ActionReceiver")

    /**
     * System intent received for a notification action.
     */
    override fun onReceive(context: Context, intent: Intent?) {
        log.verbose("onReceive")

        if(intent == null) {
            log.warn("Invalid intent")
            return
        }

        val action = when(intent.action) {
            context.packageName + BuildConfig.ACTION_TYPE_CLICK -> NotifActionType.Click
            context.packageName + BuildConfig.ACTION_TYPE_DISMISS -> NotifActionType.Dismiss
            context.packageName + BuildConfig.ACTION_TYPE_COMPLETE -> NotifActionType.ActionComplete
            context.packageName + BuildConfig.ACTION_TYPE_INCOMPLETE -> NotifActionType.ActionIncomplete
            context.packageName +  BuildConfig.ACTION_TYPE_SNOOZE -> NotifActionType.ActionSnooze
            else -> null
        }

        if(action == null) {
            log.warn("Invalid action")
            return
        }

        val guid = intent.type
        if(guid.isNullOrBlank()) {
            log.warn("Invalid guid")
            return
        }

        GlobalScope.launch {
            Notif.instance.onReceive(guid.toGuid(), action)
        }
    }

}