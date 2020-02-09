package us.jamesmorrisstudios.rrm2.app

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import us.jamesmorrisstudios.rrm2.alarm.Alarm
import us.jamesmorrisstudios.rrm2.analytics.Analytics
import us.jamesmorrisstudios.rrm2.controller.Controller
import us.jamesmorrisstudios.rrm2.controller.withModifierAlarmRegular
import us.jamesmorrisstudios.rrm2.log.Log
import us.jamesmorrisstudios.rrm2.log.LogLevel
import us.jamesmorrisstudios.rrm2.notif.*
import us.jamesmorrisstudios.rrm2.rss.Rss
import us.jamesmorrisstudios.rrm2.storage.Storage
import us.jamesmorrisstudios.rrm2.storage.reminder.ReminderItem
import us.jamesmorrisstudios.rrm2.util.Guid
import us.jamesmorrisstudios.rrm2.util.currentTime
import us.jamesmorrisstudios.rrm2.util.currentTimeMillis
import us.jamesmorrisstudios.rrms2.location.Location

/**
 * Main Application.
 *
 * Starts all the application systems used by the app.
 *
 * All systems are started asynchronously via coroutines and may suspend on usage if their startup takes a while.
 */
class App : Application() {
    private val log = Log.instance.buildLogClass(BuildConfig.MODULE_NAME, "App")

    // Build instances.
    private val analytics = Analytics.instance
    private val storage = Storage.instance
    private val alarm = Alarm.instance
    private val notif = Notif.instance
    private val rss = Rss.instance
    private val location = Location.instance
    private val controller = Controller.instance

    /**
     * Application launch. Spin up all the main app services.
     */
    override fun onCreate() {
        super.onCreate()

        // Setup Logger
        Log.instance.level = if (BuildConfig.DEBUG) {
            LogLevel.Verbose
        } else {
            LogLevel.Warn
        }
        log.info("App onCreate")

        // Initialize all services.
        GlobalScope.launch {
            // Initialize analytics.
            launch {
                analytics.initialize(applicationContext)
            }

            // Initialize rss.
            launch {
                rss.initialize(applicationContext)
            }

            // Initialize location.
            launch {
                location.initialize(applicationContext)
            }

            // Initialize the storage system.
            launch {
                storage.initialize(applicationContext)
            }

            // Initialize the scheduled alarm system.
            launch {
                alarm.initialize(applicationContext)
            }

            // Initialize the notification system.
            launch {
                notif.initialize(applicationContext)
            }

            // Initialize the controller system.
            launch {
                controller.initialize(applicationContext, alarm, notif, storage)
            }
        }
        log.info("App onCreate Complete")

        primeReminderDb()

        test()
    }

    // Add reminders if they do not yet exist.
    private fun primeReminderDb() {
        GlobalScope.launch {
            // If our reminder is not present create it.

            val guid1 = Guid.fromString("1894d6cc-5128-4f59-9d5d-5f1781c1b118")
            val guid2 = Guid.fromString("292c4c9c-7a6a-48f2-a275-af7e80958f59")
            val guid3 = Guid.fromString("3bdc2409-d7a3-4fab-b3e5-ddf798d4ef0d")
            val guid4 = Guid.fromString("4e5c6f8d-6965-42b0-840c-ffaeacc69329")

            if(storage.reminder().get(guid1) == null) {
                log.debug("Creating new reminder and scheduling it.")
                val reminder = ReminderItem(
                    detailsId = 0,
                    guid = guid1,
                    createdTime = currentTimeMillis(),
                    lastModifiedTime = currentTimeMillis(),
                    title = "Pizza",
                    description = "Pepperoni Pizza",
                    enabled = true
                )
                storage.reminder().add(reminder)

                alarm.add(reminder.guid.withModifierAlarmRegular(), currentTimeMillis() + 30 * 1000)
            }

            // TODO create other reminders as well.
        }
    }

    private fun test() {
        GlobalScope.launch {
            val guid = Guid.fromString("1894d6cc-5128-4f59-9d5d-5f1781c1b119")
            rss.add(guid, "https://wordsmith.org/awad/rss1.xml")

            delay(1000)

            log.error(rss.get(guid)?.toJson().toString())
        }
    }

//    private fun testCode() {
//        GlobalScope.launch {
//            test()
//        }
//    }

//    // Test Code
//    private fun CoroutineScope.test() {
//        launch {
//            var count = 0
//            Alarm.instance.add(Guid.generate(), System.currentTimeMillis() + 3 * 60 * 1000)
//            log.info("Alarm Scheduled")
//
//            Alarm.instance.getFlow().collect { guid ->
//                log.info("Alarm Fired $guid")
//
//                delay(20000)
//                count++
//
//                Alarm.instance.add(Guid.generate(), System.currentTimeMillis() + 3 * 60 * 1000)
//                log.info("Alarm Scheduled")
//            }
//
////            while (true) {
////                Alarm.instance.schedule("PizzaPizza $count", System.currentTimeMillis() + 3 * 60 * 1000)
////                log.info("Alarm Scheduled")
////
////                val item = Alarm.instance.subscription().receive()
////                log.info(item)
////
////                delay(20000)
////                count++
////            }
//        }
//
//        launch {
//            while(true) {
//                val item = Notif.instance.subscription().receive()
//                log.info(item.toString())
//            }
//        }
//
//        launch {
//            val guid = Guid.generate()
//            var count = 0
//            while (true) {
//                val notification = Notification(
//                    smallIcon = R.drawable.ic_alert.toUri(applicationContext),
//                    title = "Pizza",
//                    message = "Pepperoni $count",
//                    image = Storage.instance.image().getUri("test.jpg"),
//                    channel = NotificationChannel(
//                        id = "aaa-bbb",
//                        name = "My Reminder",
//                        importance = NotificationImportance.Default,
//                        description = "",
//                        group = NotificationChannelGroup(
//                            id = "aaa-bbb-ccc",
//                            name = "Reminders"
//                        )
//                    ),
//                    action1 = NotificationAction(R.drawable.ic_baseline_block_24, "Complete"),
//                    action2 = NotificationAction(R.drawable.ic_baseline_block_24, "Incomplete")
//                )
//
//                Notif.instance.add(guid, notification)
//                Notif.instance.show(guid)
//
//                log.info("Notification Shown")
//
//                delay(20000)
//                count++
//            }
//        }
//    }
//

}