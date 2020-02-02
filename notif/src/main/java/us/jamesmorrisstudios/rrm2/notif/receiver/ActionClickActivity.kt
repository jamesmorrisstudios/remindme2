package us.jamesmorrisstudios.rrm2.notif.receiver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import us.jamesmorrisstudios.rrm2.log.Log
import us.jamesmorrisstudios.rrm2.notif.BuildConfig

/**
 * Notification click handler.
 *
 * This must route through an activity as starting with Android 10 you cannot launch an activity from a broadcast receiver.
 */
class ActionClickActivity : AppCompatActivity() {
    private val log = Log.instance.buildLogClass(BuildConfig.MODULE_NAME, "ActionClickActivity")

    /**
     * Activity created with intent click action.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log.verbose("onCreate")

        // Forward the launching intent to the receiver.
        val actionReceiver = ActionReceiver()
        actionReceiver.onReceive(this, intent)
    }

}