package us.jamesmorrisstudios.rrm2.log

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * Returns the stacktrace as a string.
 */
fun Throwable.stringifyStacktrace(): String {
    return android.util.Log.getStackTraceString(this)
}

/**
 * Log Message
 */
private data class LogMsg(
    val level: LogLevel,
    val moduleTag: String,
    val classTag: String,
    val msg: String
)

/**
 * Log manager.
 */
interface Log {

    companion object {

        /**
         * The logger instance.
         */
        val instance: Log by lazy { LogImpl() }
    }

    /**
     * Logging level. Defaults to Info.
     */
    var level: LogLevel

    /**
     * Creates a log class instance that holds the module and class tags for simpler logging.
     *
     * This should be setup at the top of every class.
     */
    fun buildLogClass(moduleTag: String, classTag: String): LogClass

    /**
     * Log a message.
     */
    fun log(level: LogLevel, moduleTag: String, classTag: String, msg: () -> String)

    /**
     * Log a message.
     */
    fun log(level: LogLevel, moduleTag: String, classTag: String, msg: String)

}

/**
 * Log implementation that checks a log level and prints the message if enabled.
 */
private class LogImpl : Log {
    private val queue = Channel<LogMsg>(50)

    /**
     * {inherited}
     */
    @get:Synchronized @set:Synchronized
    override var level: LogLevel = LogLevel.Info

    /**
     * Watch the channel queue and print messages as they get posted to it.
     */
    init {
        GlobalScope.launch {
            while(true) {
                val logMsg = queue.receive()
                android.util.Log.println(logMsg.level.priority, logMsg.moduleTag, "${logMsg.classTag}: ${logMsg.msg}")
            }
        }
    }

    /**
     * {inherited}
     */
    override fun buildLogClass(moduleTag: String, classTag: String): LogClass {
        return LogClassImpl(this, moduleTag, classTag)
    }

    /**
     * {inherited}
     */
    @Synchronized
    override fun log(level: LogLevel, moduleTag: String, classTag: String, msg: () -> String) {
        if(this.level.priority > level.priority) {
            return
        }
        queue.offer(LogMsg(level, moduleTag, classTag, msg.invoke()))
    }

    /**
     * {inherited}
     */
    @Synchronized
    override fun log(level: LogLevel, moduleTag: String, classTag: String, msg: String) {
        if(this.level.priority > level.priority) {
            return
        }
        queue.offer(LogMsg(level, moduleTag, classTag, msg))
    }

}