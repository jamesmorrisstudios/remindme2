package us.jamesmorrisstudios.rrm2.rss

import android.content.Context
import androidx.room.Room
import com.ouattararomuald.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.ouattararomuald.syndication.Syndication
import com.ouattararomuald.syndication.rss.Item
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import us.jamesmorrisstudios.rrm2.log.Log
import us.jamesmorrisstudios.rrm2.util.*

/**
 * RSS handler.
 *
 * This manages downloading and maintaining RSS feed subscriptions.
 */
interface Rss {

    companion object {

        /**
         * The RSS handler instance.
         */
        val instance: Rss by lazy { RssImpl() }
    }

    /**
     * Initialize the Rss handler. Do this in the Application.onCreate method.
     */
    suspend fun initialize(context: Context)

    /**
     * Subscribes to the given RSS Feed.
     *
     * Subscribed RSS Feeds are automatically updated periodically and are refreshed immediately upon adding.
     */
    suspend fun add(guid: Guid, url: String)

    /**
     * Removes an RSS Feed subscription.
     */
    suspend fun remove(guid: Guid)

    /**
     * Retrieves the given RSS Feed details.
     */
    suspend fun get(guid: Guid): RssChannel?

    /**
     * Refreshes the given RSS Feed.
     */
    suspend fun refresh(guid: Guid)

    /**
     * Refreshes all subscribed RSS Feeds.
     */
    suspend fun refresh()

}

/**
 * Implementation of the RSS Handler.
 */
private class RssImpl : Rss {
    private val log = Log.instance.buildLogClass(BuildConfig.MODULE_NAME, "RssImpl")
    private val deferredUntilInitialized = CompletableDeferred<Unit>()
    private val lock: Mutex = Mutex()
    private lateinit var context: Context
    private lateinit var prefs: Prefs
    private lateinit var db: RssDb

    /**
     * {inherited}
     */
    override suspend fun initialize(context: Context) {
        this.context = context.applicationContext
        log.info("initialize")

        lock.withLock {
            // Initialize storage.
            prefs = Prefs.build(this.context, BuildConfig.PREFS_NAME)
            db = Room.databaseBuilder(this.context, RssDb::class.java, BuildConfig.DB_NAME).build()

            // Start the periodic refresh worker if needed.
            updateRefreshWorker()
        }
        deferredUntilInitialized.complete(Unit)
    }

    /**
     * {inherited}
     */
    override suspend fun add(guid: Guid, url: String) {
        deferredUntilInitialized.await()
        lock.withLock {
            log.debug("add: guid:$guid, url:$url")
            // Remove any existing subscription.
            db.rssDao().findByGuid(guid)?.let {
                db.rssDao().delete(it)
            }

            // Create the new rss feed.
            db.rssDao().insert(
                RssFeed(
                    guid = guid,
                    url = url,
                    createdTime = currentTimeMillis(),
                    lastUpdatedTime = 0,
                    channel = RssChannel.build()
            ))

            // Refresh it.
            db.rssDao().findByGuid(guid)?.let {
                refresh(it)
            }

            // Start the periodic refresh worker if needed.
            updateRefreshWorker()
        }
    }

    /**
     * {inherited}
     */
    override suspend fun remove(guid: Guid) {
        deferredUntilInitialized.await()
        lock.withLock {
            log.debug("remove: guid:$guid")
            db.rssDao().findByGuid(guid)?.let {
                db.rssDao().delete(it)
            }

            // Stop the periodic refresh worker if needed.
            updateRefreshWorker()
        }
    }

    /**
     * {inherited}
     */
    override suspend fun get(guid: Guid): RssChannel? {
        deferredUntilInitialized.await()
        lock.withLock {
            log.debug("get: guid:$guid")
            return db.rssDao().findByGuid(guid)?.channel
        }
    }

    /**
     * {inherited}
     */
    override suspend fun refresh(guid: Guid) {
        deferredUntilInitialized.await()
        lock.withLock {
            log.debug("refresh: guid:$guid")
            db.rssDao().findByGuid(guid)?.let {
                refresh(it)
            }
        }
    }

    /**
     * {inherited}
     */
    override suspend fun refresh() {
        deferredUntilInitialized.await()
        lock.withLock {
            log.debug("refresh: all")
            db.rssDao().getAll().forEach {
                refresh(it)
            }
        }
    }

    /**
     * Refresh an RssFeed.
     */
    private suspend fun refresh(rssFeed: RssFeed): Boolean {
        // Attempt to read the rss feed.
        val refreshedChannel = readRssFeed(rssFeed) ?: return false

        // Copy the update into the feed.
        val refreshedRssFeed = rssFeed.copy(
            lastUpdatedTime = currentTimeMillis(),
            channel = refreshedChannel
        )

        // Update the db entry.
        db.rssDao().update(refreshedRssFeed)
        return true
    }

    /**
     * Read the Rss Feed into a resulting Rss Channel.
     *
     * On failure (such as offline) this returns null.
     */
    private suspend fun readRssFeed(rssFeed: RssFeed): RssChannel? {
        return runCatching {
            val syndication = Syndication(url = rssFeed.url, callFactory = CoroutineCallAdapterFactory())
            val reader = syndication.create(RssReader::class.java)
            val rss = reader.readRssFeed().await()
            rss.channel.toRssChannel()
        }.getOrNull()
    }

    /**
     * If any subscriptions exist in the db ensure the refresh worker is started.
     * If none exist ensure the refresh worker is stopped.
     */
    private suspend fun updateRefreshWorker() {
        if(db.rssDao().getRowCount() > 0) {
            log.debug("updateRefreshWorker: Start")
            startRefreshWorker(context)
        } else {
            log.debug("updateRefreshWorker: Stop")
            stopRefreshWorker(context)
        }
    }

}

/**
 * Helper to convert a library Channel into an Rss Channel.
 */
internal fun com.ouattararomuald.syndication.rss.Channel.toRssChannel(): RssChannel {
    return RssChannel(
        title = this.title,
        description = this.description,
        items = this.items.toRssItems()
    )
}

/**
 * Helper to convert a list of library Items into a list of RssItems.
 */
internal fun List<Item>?.toRssItems(): List<RssItem> {
    return this?.map { it.toRssItem() } ?: listOf()
}

/**
 * Helper to convert a library Item to an RssItem
 */
internal fun Item.toRssItem(): RssItem {
    return RssItem(
        title = this.title ?: "",
        description = this.description ?: "",
        link = this.link ?: ""
    )
}

/**
 * Helper used to read data from an Rss Feed.
 */
internal interface RssReader {

    /**
     * Read RSS deferred via a coroutine.
     */
    fun readRssFeed(): Deferred<com.ouattararomuald.syndication.rss.RssFeed>

}