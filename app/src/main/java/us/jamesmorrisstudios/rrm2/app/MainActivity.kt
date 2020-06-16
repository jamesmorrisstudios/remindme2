package us.jamesmorrisstudios.rrm2.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.core.setContent
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.padding
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
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
        setContent {
            MyApp()
        }
        //setContentView(R.layout.activity_main)
        log.info("MainActivity onCreate")
    }

}

@Composable
fun MyApp() {
    MaterialTheme {
        Surface(color = Color.Yellow) {
            Greeting(name = "Android")
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!", modifier = Modifier.padding(24.dp))
}

@Preview
@Composable
fun DefaultPreview() {
    MyApp()
}