package us.jamesmorrisstudios.rrm2.rss

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import us.jamesmorrisstudios.rrm2.util.Guid
import us.jamesmorrisstudios.rrm2.util.JsonArray
import us.jamesmorrisstudios.rrm2.util.JsonObject

/**
 * RSS Feed that can be subscribed to.
 */
@Entity(tableName = "rss")
internal data class RssFeed(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val id: Int = 0,
    @ColumnInfo(name = "guid", index = true) val guid: Guid,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "created_time") val createdTime: Long,
    @ColumnInfo(name = "last_updated_time") val lastUpdatedTime: Long,
    @ColumnInfo(name = "channel") val channel: RssChannel
)

/**
 * RSS Channel as pulled from the RSS Feed URL.
 */
data class RssChannel(
    val title: String,
    val description: String,
    val items: List<RssItem>
) {

    /**
     * Serialize to json.
     */
    fun toJson(): JsonObject {
        return JsonObject.build().apply {
            setString("title", title)
            setString("description", description)
            setJsonArray("items", items.toJson())
        }
    }

    companion object {

        /**
         * Build an rss channel with default options.
         */
        fun build(): RssChannel {
            return fromJson(JsonObject.build())
        }

        /**
         * Parse from json.
         */
        fun fromJson(json: JsonObject): RssChannel {
            return RssChannel(
                title = json.getString("title"),
                description = json.getString("description"),
                items = json.getJsonArray("items").toRssItemList()
            )
        }

    }

}

/**
 * Individual RSS Item from an RSS Channel's list of items.
 */
data class RssItem(
    val title: String,
    val description: String,
    val link: String
) {

    /**
     * Serialize to json.
     */
    fun toJson(): JsonObject {
        return JsonObject.build().apply {
            setString("title", title)
            setString("description", description)
            setString("link", link)
        }
    }

    companion object {

        /**
         * Parse from json.
         */
        fun fromJson(json: JsonObject): RssItem {
            return RssItem(
                title = json.getString("title"),
                description = json.getString("description"),
                link = json.getString("link")
            )
        }

    }

}

/**
 * Helper to serialize a list of RssItems into a json array.
 */
internal fun List<RssItem>.toJson(): JsonArray {
    val json = JsonArray.build()
    this.forEach {
        json.addJsonObject(it.toJson())
    }
    return json
}

/**
 * Helper to parse a json array of RssItems into a list of RssItems.
 */
internal fun JsonArray.toRssItemList(): List<RssItem> {
    val list = mutableListOf<RssItem>()
    for(i in 0 until length()) {
        list.add(RssItem.fromJson(this.getJsonObject(i)))
    }
    return list
}