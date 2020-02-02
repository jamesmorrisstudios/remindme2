package us.jamesmorrisstudios.rrm2.storage.reminder

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import us.jamesmorrisstudios.rrm2.log.Log
import us.jamesmorrisstudios.rrm2.storage.BuildConfig
import us.jamesmorrisstudios.rrm2.util.Guid

/**
 * Reminder interface
 *
 * TODO this is just some simplistic code to get some basic stuff running but its far from complete.
 */
interface Reminder {

    suspend fun add(reminderItem: ReminderItem)

    suspend fun get(guid: Guid): ReminderItem?

    suspend fun delete(guid: Guid)

}

internal class ReminderImpl(private val context: Context) : Reminder {
    private val log = Log.instance.buildLogClass(BuildConfig.MODULE_NAME, "ReminderImpl")
    private val deferredUntilInitialized = CompletableDeferred<Unit>()
    private val lock: Mutex = Mutex()
    private lateinit var db: ReminderDb

    init {
        GlobalScope.launch {
            lock.withLock {
                db = Room.databaseBuilder(context, ReminderDb::class.java, BuildConfig.REMINDER_DB_NAME).build()
                deferredUntilInitialized.complete(Unit)
            }
        }
    }

    override suspend fun add(reminderItem: ReminderItem) {
        deferredUntilInitialized.await()
        lock.withLock {
            // If it already exists delete it first.
            db.reminderDao().findByGuid(reminderItem.guid)?.let {
                db.reminderDao().delete(it)
            }

            // Add it
            db.reminderDao().insert(reminderItem)
        }
    }

    override suspend fun get(guid: Guid): ReminderItem? {
        deferredUntilInitialized.await()
        lock.withLock {
            return db.reminderDao().findByGuid(guid)
        }
    }

    override suspend fun delete(guid: Guid) {
        deferredUntilInitialized.await()
        lock.withLock {
            val reminder = db.reminderDao().findByGuid(guid) ?: return
            db.reminderDao().delete(reminder)
        }
    }

}
