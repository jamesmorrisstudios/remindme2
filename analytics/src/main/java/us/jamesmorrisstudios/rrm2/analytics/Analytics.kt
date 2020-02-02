package us.jamesmorrisstudios.rrm2.analytics

import android.annotation.SuppressLint
import android.content.Context
import com.kochava.base.Tracker
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import us.jamesmorrisstudios.rrm2.log.Log
import us.jamesmorrisstudios.rrm2.util.Prefs
import us.jamesmorrisstudios.rrm2.util.currentTime
import us.jamesmorrisstudios.rrm2.util.currentTimeMillis

/**
 * Analytics event name types.
 */
enum class EventName(private val keyName: String) {
    // Reminder events
    ReminderCreated("ReminderCreated"),
    ReminderDeleted("ReminderDeleted"),
    ReminderModified("ReminderModified"),
    ReminderModifyAborted("ReminderModifyAborted"),

    // Page navigated events
    PageMain("PageMain"),
    PageSettings("PageSettings"),
    PageHelp("PageHelp");

    override fun toString(): String {
        return keyName
    }

}

/**
 * Analytics event.
 *
 * Note: When adding parameters the toKochavaEvent extension method must be updated to apply them.
 */
interface Event {
    val eventName: EventName
    val durationStartTime: Long

    suspend fun send()

}

/**
 * Analytics event implementation.
 */
internal class EventImpl(private val _eventName: EventName, private val _durationStartTime: Long = 0) : Event {
    override val eventName: EventName get() = _eventName
    override val durationStartTime: Long get() = _durationStartTime

    /**
     * Send this event.
     */
    override suspend fun send() {
        Analytics.instance.sendEvent(this)
    }

}

/**
 * Analytics manager.
 */
interface Analytics {

    companion object {

        /**
         * The Analytics instance.
         */
        val instance: Analytics by lazy { AnalyticsImpl() }
    }

    /**
     * Initialize the analytics manager. Do this in the Application.onCreate method.
     */
    suspend fun initialize(context: Context)

    /**
     * Enable or disable the analytics system.
     */
    suspend fun enable(enable: Boolean)

    /**
     * Builds an event given a name type.
     */
    fun buildEvent(eventName: EventName): Event

    /**
     * Builds a duration event given a name.
     */
    fun buildDurationEvent(eventName: EventName): Event

    /**
     * Sends the given event.
     */
    suspend fun sendEvent(event: Event)

}

/**
 * Analytics manager implementation.
 */
private class AnalyticsImpl : Analytics {
    private val log = Log.instance.buildLogClass(BuildConfig.MODULE_NAME, "AnalyticsImpl")
    private val deferredUntilInitialized = CompletableDeferred<Unit>()
    private val lock: Mutex = Mutex()
    private lateinit var context: Context
    private lateinit var prefs: Prefs
    private var startedThisLaunch = false

    /**
     * {inherited}
     */
    override suspend fun initialize(context: Context) {
        this.context = context.applicationContext
        log.info("initialize")

        lock.withLock {
            // Initialize storage.
            prefs = Prefs.build(this.context, BuildConfig.PREFS_NAME)

            if(prefs.getBoolean("enabled")) {
                start()
            }
        }
        deferredUntilInitialized.complete(Unit)
    }

    /**
     * {inherited}
     */
    override suspend fun enable(enable: Boolean) {
        deferredUntilInitialized.await()
        lock.withLock {
            prefs.setBoolean("enabled", enable)
            if(enable) {
                start()
            } else {
                stop()
            }
        }
    }

    /**
     * {inherited}
     */
    override fun buildEvent(eventName: EventName): Event {
        return EventImpl(_eventName = eventName)
    }

    /**
     * {inherited}
     */
    override fun buildDurationEvent(eventName: EventName): Event {
        return EventImpl(_eventName = eventName, _durationStartTime = currentTimeMillis())
    }

    /**
     * {inherited}
     */
    override suspend fun sendEvent(event: Event) {
        deferredUntilInitialized.await()
        lock.withLock {
            if(!Tracker.isConfigured()) {
                return
            }
            Tracker.sendEvent(event.toKochavaEvent())
        }
    }

    /**
     * Start the SDK.
     *
     * Only allow the SDK to be started once per app launch, if disabled and then enabled again don't actually start until the next launch.
     */
    private fun start() {
        // Ensure its only started once per launch.
        if(startedThisLaunch || Tracker.isConfigured()) {
            return
        }
        startedThisLaunch = true

        // Start
        GlobalScope.launch {
            Tracker.configure(Tracker.Configuration(context)
                .setAppGuid(BuildConfig.KOCHAVA_APP_GUID)
                .setLogLevel(if(BuildConfig.DEBUG) Tracker.LOG_LEVEL_INFO else Tracker.LOG_LEVEL_WARN)
            )
        }
    }

    /**
     * Stop the SDK.
     */
    private fun stop() {
        // Ensure its not stopped twice in a row.
        if(!Tracker.isConfigured()) {
            return
        }

        // Stop
        GlobalScope.launch {
            Tracker.unConfigure(true)
        }
    }

}

/**
 * Converts an Analytics event into a Kochava event.
 */
@SuppressLint("CheckResult")
private fun Event.toKochavaEvent(): Tracker.Event {
    val koEvent = Tracker.Event(this.eventName.toString())

    // Check if this is a duration event and if so calculate the duration.
    if(durationStartTime > 0) {
        val nowTime = currentTimeMillis()
        koEvent.setStartDate(durationStartTime.toString())
            .setEndDate(nowTime.toString())
            .setDuration((nowTime - durationStartTime).toDouble())
    }

    return koEvent
}