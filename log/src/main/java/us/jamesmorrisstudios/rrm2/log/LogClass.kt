package us.jamesmorrisstudios.rrm2.log

/**
 * Log class manager that allows for simple logging with a module and class tag.
 */
interface LogClass {

    /**
     * Log an assert.
     */
    fun assert(msg: String)

    /**
     * Log an assert.
     */
    fun assert(msg: () -> String)

    /**
     * Log an error.
     */
    fun error(msg: String)

    /**
     * Log an error.
     */
    fun error(msg: () -> String)

    /**
     * Log a warning.
     */
    fun warn(msg: String)

    /**
     * Log a warning.
     */
    fun warn(msg: () -> String)

    /**
     * Log information.
     */
    fun info(msg: String)

    /**
     * Log information.
     */
    fun info(msg: () -> String)

    /**
     * Log debugging information.
     */
    fun debug(msg: String)

    /**
     * Log debugging information.
     */
    fun debug(msg: () -> String)

    /**
     * Log verbose debugging information.
     */
    fun verbose(msg: String)

    /**
     * Log verbose debugging information.
     */
    fun verbose(msg: () -> String)

}

/**
 * Log class implementation.
 */
internal class LogClassImpl(private val log: Log, private val moduleTag: String, private val classTag: String) : LogClass {

    /**
     * {inherited}
     */
    override fun assert(msg: String) = log.log(LogLevel.Assert, moduleTag, classTag, msg)

    /**
     * {inherited}
     */
    override fun assert(msg: () -> String) = log.log(LogLevel.Assert, moduleTag, classTag, msg)

    /**
     * {inherited}
     */
    override fun error(msg: String) = log.log(LogLevel.Error, moduleTag, classTag, msg)

    /**
     * {inherited}
     */
    override fun error(msg: () -> String) = log.log(LogLevel.Error, moduleTag, classTag, msg)

    /**
     * {inherited}
     */
    override fun warn(msg: String) = log.log(LogLevel.Warn, moduleTag, classTag, msg)

    /**
     * {inherited}
     */
    override fun warn(msg: () -> String) = log.log(LogLevel.Warn, moduleTag, classTag, msg)

    /**
     * {inherited}
     */
    override fun info(msg: String) = log.log(LogLevel.Info, moduleTag, classTag, msg)

    /**
     * {inherited}
     */
    override fun info(msg: () -> String) = log.log(LogLevel.Info, moduleTag, classTag, msg)

    /**
     * {inherited}
     */
    override fun debug(msg: String) = log.log(LogLevel.Debug, moduleTag, classTag, msg)

    /**
     * {inherited}
     */
    override fun debug(msg: () -> String) = log.log(LogLevel.Debug, moduleTag, classTag, msg)

    /**
     * {inherited}
     */
    override fun verbose(msg: String) = log.log(LogLevel.Verbose, moduleTag, classTag, msg)

    /**
     * {inherited}
     */
    override fun verbose(msg: () -> String) = log.log(LogLevel.Verbose, moduleTag, classTag, msg)

}