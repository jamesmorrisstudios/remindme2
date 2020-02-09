package us.jamesmorrisstudios.rrm2.rss

import androidx.room.*
import us.jamesmorrisstudios.rrm2.util.Guid
import us.jamesmorrisstudios.rrm2.util.GuidDbTypeConverter
import us.jamesmorrisstudios.rrm2.util.JsonObject

/**
 * RSS Database
 */
@Database(entities = [RssFeed::class], version = 1, exportSchema = false)
@TypeConverters(GuidDbTypeConverter::class, RssChannelDbTypeConverter::class)
internal abstract class RssDb : RoomDatabase() {
    abstract fun rssDao(): RssDbDao
}

/**
 * RSS Database Queries.
 */
@Dao
internal interface RssDbDao {

    /**
     * Returns a count of all the rss feeds.
     */
    @Query("SELECT COUNT(rowid) FROM rss")
    suspend fun getRowCount(): Int

    /**
     * Returns all rss feeds.
     */
    @Query("SELECT * FROM rss")
    suspend fun getAll(): List<RssFeed>

    /**
     * Returns the rss feed with the given unique guid if it exists.
     */
    @Query("SELECT * FROM rss WHERE guid LIKE :guid")
    suspend fun findByGuid(guid: Guid): RssFeed?

    /**
     * Internal
     *
     * Inserts a new rss feed.
     */
    @Insert
    suspend fun internalInsert(rssFeed: RssFeed): Long

    /**
     * Updates an existing rss feed.
     */
    @Update
    suspend fun update(rssFeed: RssFeed)

    /**
     * Deletes an existing alarm.
     */
    @Delete
    suspend fun delete(rssFeed: RssFeed)

}

/**
 * Inserts a new rss feed.
 */
internal suspend fun RssDbDao.insert(rssFeed: RssFeed): Int = internalInsert(rssFeed).toInt()

/**
 * Type converter for storing rss channel db action types.
 */
internal class RssChannelDbTypeConverter {

    @TypeConverter
    fun toRssChannelType(string: String): RssChannel {
        return RssChannel.fromJson(JsonObject.build(string))
    }

    @TypeConverter
    fun toStringType(rssChannel: RssChannel): String {
        return rssChannel.toJson().toString()
    }

}