package us.jamesmorrisstudios.rrm2.rss

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Unique tag to retrieve info on the worker.
 */
private const val refreshWorkerTag = "rssRefreshWorkerTag"

/**
 * Unique name to ensure the worker is not started multiple times.
 */
private const val refreshWorkerName = "rssRefreshWorkerName"

/**
 * Refresh worker
 */
internal class RssRefreshWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    /**
     * Perform a refresh and return success.
     */
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Rss.instance.refresh()
        Result.success()
    }

}

/**
 * Starts the refresh worker. If already started this does nothing.
 */
internal fun startRefreshWorker(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val workRequest = PeriodicWorkRequestBuilder<RssRefreshWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .addTag(refreshWorkerTag)
            .build()

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(refreshWorkerName, ExistingPeriodicWorkPolicy.KEEP, workRequest)
}

/**
 * Stops the refresh worker.
 */
internal fun stopRefreshWorker(context: Context) {
    WorkManager.getInstance(context).cancelAllWorkByTag(refreshWorkerTag)
}

/**
 * Returns if the refresh worker is started.
 */
internal suspend fun isRefreshWorkerStarted(context: Context): Boolean {
    val result = WorkManager.getInstance(context)
        .getWorkInfosByTag(refreshWorkerTag)
        .await()

    return result.isNotEmpty()
}