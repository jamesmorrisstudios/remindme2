package us.jamesmorrisstudios.rrm2.storage.history

import android.content.Context
import androidx.room.Room
import us.jamesmorrisstudios.rrm2.storage.BuildConfig

/**
 * History Manager.
 */
interface History {

    /**
     * Add a new history entry.
     */
    suspend fun add(entry: HistoryItem)

    /**
     * Return all the history entries for the given guid.
     */
    suspend fun getAll(guid: String): List<HistoryItem>

    /**
     * Return the last limit history entries for the given guid.
     */
    suspend fun getLast(guid: String, limit: Int): List<HistoryItem>

    /**
     * Delete all history entries for the given guid.
     */
    suspend fun deleteAll(guid: String)

    /**
     * Delete all history entries for the given guid older then the given timestamp.
     */
    suspend fun deleteOldest(guid: String, time: Long)

}

/**
 * Implementation of the History Manager.
 */
internal class HistoryImpl(private val context: Context) : History {
    private val db  = Room.databaseBuilder(this.context, HistoryDb::class.java, BuildConfig.HISTORY_DB_NAME).build()

    /**
     * {inherited}
     */
    override suspend fun add(entry: HistoryItem) {
        db.historyDao().insert(entry)
    }

    /**
     * {inherited}
     */
    override suspend fun getAll(guid: String): List<HistoryItem> {
        return db.historyDao().getAllForGuid(guid)
    }

    /**
     * {inherited}
     */
    override suspend fun getLast(guid: String, limit: Int): List<HistoryItem> {
        return db.historyDao().getLastForGuid(guid, limit).sortedBy { it.time }
    }

    /**
     * {inherited}
     */
    override suspend fun deleteAll(guid: String) {
        db.historyDao().deleteForGuid(guid)
    }

    /**
     * {inherited}
     */
    override suspend fun deleteOldest(guid: String, time: Long) {
        db.historyDao().deleteOldestForGuid(guid, time)
    }

}
