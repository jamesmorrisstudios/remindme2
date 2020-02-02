package us.jamesmorrisstudios.rrm2.storage

import android.content.Context
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import us.jamesmorrisstudios.rrm2.log.Log
import us.jamesmorrisstudios.rrm2.storage.history.History
import us.jamesmorrisstudios.rrm2.storage.history.HistoryImpl
import us.jamesmorrisstudios.rrm2.storage.image.Image
import us.jamesmorrisstudios.rrm2.storage.image.ImageImpl
import us.jamesmorrisstudios.rrm2.storage.reminder.Reminder
import us.jamesmorrisstudios.rrm2.storage.reminder.ReminderImpl
import us.jamesmorrisstudios.rrm2.storage.schedule.Schedule
import us.jamesmorrisstudios.rrm2.storage.schedule.ScheduleImpl

/**
 * Storage Manager.
 */
interface Storage {

    companion object {

        /**
         * The Storage instance.
         */
        val instance: Storage by lazy { StorageImpl() }
    }

    /**
     * Initialize the storage manager. Do this in the Application.onCreate method.
     */
    suspend fun initialize(context: Context)

    /**
     * Image handler.
     */
    suspend fun image(): Image

    /**
     * Reminder handler.
     */
    suspend fun reminder(): Reminder

    /**
     * Schedule handler.
     */
    suspend fun schedule(): Schedule

    /**
     * History log handler.
     */
    suspend fun history(): History

}

/**
 * Implementation of the Storage Manager.
 */
private class StorageImpl: Storage {
    private val log = Log.instance.buildLogClass(BuildConfig.MODULE_NAME, "StorageImpl")
    private val deferredUntilInitialized = CompletableDeferred<Unit>()
    private val lock: Mutex = Mutex()
    private lateinit var context: Context
    private lateinit var image: Image
    private lateinit var reminder: Reminder
    private lateinit var schedule: Schedule
    private lateinit var history: History

    /**
     * {inherited}
     */
    override suspend fun initialize(context: Context) {
        this.context = context.applicationContext
        log.info("initialize")

        lock.withLock {
            // Initialize storage.
            image = ImageImpl(this.context)
            reminder = ReminderImpl(this.context)
            schedule = ScheduleImpl(this.context)
            history = HistoryImpl(this.context)
        }
        deferredUntilInitialized.complete(Unit)
    }

    /**
     * {inherited}
     */
    override suspend fun image(): Image {
        deferredUntilInitialized.await()
        return image
    }

    /**
     * {inherited}
     */
    override suspend fun reminder(): Reminder {
        deferredUntilInitialized.await()
        return reminder
    }

    /**
     * {inherited}
     */
    override suspend fun schedule(): Schedule {
        deferredUntilInitialized.await()
        return schedule
    }

    /**
     * {inherited}
     */
    override suspend fun history(): History {
        deferredUntilInitialized.await()
        return history
    }

}