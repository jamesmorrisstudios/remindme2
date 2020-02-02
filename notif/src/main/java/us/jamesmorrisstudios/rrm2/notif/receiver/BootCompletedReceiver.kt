package us.jamesmorrisstudios.rrm2.notif.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import us.jamesmorrisstudios.rrm2.log.Log
import us.jamesmorrisstudios.rrm2.notif.BuildConfig

/**
 * Boot completed receiver. This does not need to do anything directly as the Application class will start
 * and initialize the Alarm scheduler which will then reapply anything needed.
 */
class BootCompleteReceiver : BroadcastReceiver() {
    private val log = Log.instance.buildLogClass(BuildConfig.MODULE_NAME, "BootCompleteReceiver")

    /**
     * Do nothing here. The main app's Application class will handle things.
     */
    override fun onReceive(context: Context, intent: Intent?) {
        log.verbose("onReceive")

        if(intent == null) {
            log.warn("Invalid intent")
            return
        }

        if(intent.action != Intent.ACTION_BOOT_COMPLETED) {
            log.warn("Invalid action")
            return
        }
        log.debug("Boot Completed")
    }

}