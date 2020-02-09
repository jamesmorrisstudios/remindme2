package us.jamesmorrisstudios.rrm2.storage.reminder

import androidx.room.*
import us.jamesmorrisstudios.rrm2.util.Guid
import us.jamesmorrisstudios.rrm2.util.GuidDbTypeConverter

// https://stackoverflow.com/questions/5481386/date-and-time-change-listener-in-android
// https://developer.android.com/training/data-storage/room/relationships

/**
 * Reminder Database
 */
@Database(entities = [ReminderItem::class], version = 1, exportSchema = false)
@TypeConverters(GuidDbTypeConverter::class)
internal abstract class ReminderDb : RoomDatabase() {
    abstract fun reminderDao(): ReminderDbDao
}

/**
 * Reminder Database Queries.
 */
@Dao
internal interface ReminderDbDao {

    /**
     * Returns the reminder with the given unique guid if it exists.
     */
    @Query("SELECT * FROM reminder WHERE guid LIKE :guid")
    suspend fun findByGuid(guid: Guid): ReminderItem?

    /**
     * Internal
     *
     * Inserts a new reminder.
     */
    @Insert
    suspend fun internalInsert(reminder: ReminderItem): Long

    /**
     * Deletes an existing reminder.
     */
    @Delete
    suspend fun delete(reminder: ReminderItem)

}

/**
 * Inserts a new reminder.
 */
internal suspend fun ReminderDbDao.insert(reminder: ReminderItem): Int = internalInsert(reminder).toInt()