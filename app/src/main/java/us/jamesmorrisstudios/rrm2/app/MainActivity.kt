package us.jamesmorrisstudios.rrm2.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import us.jamesmorrisstudios.rrm2.alarm.Alarm
import us.jamesmorrisstudios.rrm2.log.Log
import us.jamesmorrisstudios.rrm2.notif.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val log = Log.instance.buildLogClass(BuildConfig.MODULE_NAME, "App")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        log.info("MainActivity onCreate")
    }

}
