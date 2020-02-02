package us.jamesmorrisstudios.rrm2.notif

import androidx.room.*
import us.jamesmorrisstudios.rrm2.util.Guid
import us.jamesmorrisstudios.rrm2.util.GuidDbTypeConverter

/**
 * Notif Database
 */
@Database(entities = [NotifItem::class], version = 1)
@TypeConverters(NotificationDbTypeConverter::class, GuidDbTypeConverter::class)
internal abstract class NotifDb : RoomDatabase() {
    abstract fun notifDao(): NotifDbDao
}

/**
 * Notif Database Queries.
 */
@Dao
internal interface NotifDbDao {

    /**
     * Returns all notifs.
     */
    @Query("SELECT * FROM notif")
    suspend fun getAll(): List<NotifItem>

    /**
     * Returns the notif with the given unique guid if it exists.
     */
    @Query("SELECT * FROM notif WHERE guid LIKE :guid")
    suspend fun findByGuid(guid: Guid): NotifItem?

    /**
     * Internal
     *
     * Inserts a new notif.
     */
    @Insert
    suspend fun internalInsert(notif: NotifItem): Long

    /**
     * Updates an existing notif.
     */
    @Update
    suspend fun update(notif: NotifItem)

    /**
     * Deletes an existing notif.
     */
    @Delete
    suspend fun delete(notif: NotifItem)

}

/**
 * Inserts a new notif.
 */
internal suspend fun NotifDbDao.insert(notif: NotifItem): Int = internalInsert(notif).toInt()

/**
 * Type converter for storing history db action types.
 */
internal class NotificationDbTypeConverter {

    @TypeConverter
    fun toNotificationType(string: String): Notification {
        return Notification.fromString(string)
    }

    @TypeConverter
    fun toStringType(notification: Notification): String {
        return notification.toString()
    }

}