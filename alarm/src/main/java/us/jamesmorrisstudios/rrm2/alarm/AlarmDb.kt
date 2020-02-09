package us.jamesmorrisstudios.rrm2.alarm

import androidx.room.*
import us.jamesmorrisstudios.rrm2.util.Guid
import us.jamesmorrisstudios.rrm2.util.GuidDbTypeConverter

/**
 * Alarm Database
 */
@Database(entities = [AlarmItem::class], version = 1, exportSchema = false)
@TypeConverters(GuidDbTypeConverter::class)
internal abstract class AlarmDb : RoomDatabase() {
    abstract fun alarmDao(): AlarmDbDao
}

/**
 * Alarm Database Queries.
 */
@Dao
internal interface AlarmDbDao {

    /**
     * Returns all alarms.
     */
    @Query("SELECT * FROM alarm")
    suspend fun getAll(): List<AlarmItem>

    /**
     * Returns all the alarms with a time before the given time (inclusive).
     */
    @Query("SELECT * FROM alarm WHERE time <= :time")
    suspend fun getWithTimeBefore(time: Long): List<AlarmItem>

    /**
     * Returns the alarm with the given unique guid if it exists.
     */
    @Query("SELECT * FROM alarm WHERE guid LIKE :guid")
    suspend fun findByGuid(guid: Guid): AlarmItem?

    /**
     * Internal
     *
     * Inserts a new alarm.
     */
    @Insert
    suspend fun internalInsert(alarm: AlarmItem): Long

    /**
     * Deletes an existing alarm.
     */
    @Delete
    suspend fun delete(alarm: AlarmItem)

}

/**
 * Inserts a new alarm.
 */
internal suspend fun AlarmDbDao.insert(alarm: AlarmItem): Int = internalInsert(alarm).toInt()
