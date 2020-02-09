package us.jamesmorrisstudios.rrm2.storage.history

import androidx.room.*
import us.jamesmorrisstudios.rrm2.util.GuidDbTypeConverter
import java.lang.IllegalArgumentException

/**
 * History Database
 */
@Database(entities = [HistoryItem::class], version = 1, exportSchema = false)
@TypeConverters(HistoryDbItemActionTypeConverter::class, GuidDbTypeConverter::class)
internal abstract class HistoryDb : RoomDatabase() {
    abstract fun historyDao(): HistoryDbDao
}

/**
 * History Database Queries.
 */
@Dao
internal interface HistoryDbDao {

    /**
     * Returns all history entries.
     */
    @Query("SELECT * FROM history")
    suspend fun getAll(): List<HistoryItem>

    /**
     * Returns all history entries for a specific guid
     */
    @Query("SELECT * FROM history WHERE guid = :guid")
    suspend fun getAllForGuid(guid: String): List<HistoryItem>

    /**
     * Returns all history entries for a specific guid up to the given limit. Ordered as most recent first.
     */
    @Query("SELECT * FROM history WHERE guid = :guid ORDER BY time DESC LIMIT :limit")
    suspend fun getLastForGuid(guid: String, limit: Int): List<HistoryItem>

    /**
     * Internal
     *
     * Inserts a new history entry.
     */
    @Insert
    suspend fun internalInsert(historyItem: HistoryItem): Long

    /**
     * Deletes an existing history entry.
     */
    @Delete
    suspend fun delete(historyItem: HistoryItem)

    /**
     * Deletes all entries for a given guid.
     */
    @Query("DELETE FROM history WHERE guid = :guid")
    suspend fun deleteForGuid(guid: String)

}

/**
 * Inserts a new history entry.
 */
internal suspend fun HistoryDbDao.insert(historyItem: HistoryItem): Int = internalInsert(historyItem).toInt()

/**
 * Type converter for storing history db action types.
 */
internal class HistoryDbItemActionTypeConverter {

    @TypeConverter
    fun toActionType(string: String): HistoryItemAction {
        return when(string) {
            "show" -> HistoryItemAction.Show
            "re_show" -> HistoryItemAction.ReShow
            "click" -> HistoryItemAction.Click
            "dismiss" -> HistoryItemAction.Dismiss
            "action1" -> HistoryItemAction.Action1
            "action2" -> HistoryItemAction.Action2
            "action3" -> HistoryItemAction.Action3
            "cancelled" -> HistoryItemAction.Cancelled
            "replaced" -> HistoryItemAction.Replaced
            else -> throw IllegalArgumentException("Could not recognize value")
        }
    }

    @TypeConverter
    fun toStringType(action: HistoryItemAction): String {
        return when(action) {
            HistoryItemAction.Show -> "show"
            HistoryItemAction.ReShow -> "re_show"
            HistoryItemAction.Click -> "click"
            HistoryItemAction.Dismiss -> "dismiss"
            HistoryItemAction.Action1 -> "action1"
            HistoryItemAction.Action2 -> "action2"
            HistoryItemAction.Action3 -> "action3"
            HistoryItemAction.Cancelled -> "cancelled"
            HistoryItemAction.Replaced -> "replaced"
        }
    }

}