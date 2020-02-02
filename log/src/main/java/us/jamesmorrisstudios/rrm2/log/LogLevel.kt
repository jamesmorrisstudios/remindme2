package us.jamesmorrisstudios.rrm2.log

import android.util.Log

/**
 * Logging levels. All levels include the levels below them.
 */
enum class LogLevel(val priority: Int) {

    /**
     * Log verbose debugging information.
     */
    Verbose(Log.VERBOSE),

    /**
     * Log debugging information.
     */
    Debug(Log.DEBUG),

    /**
     * Log runtime information.
     */
    Info(Log.INFO),

    /**
     * Log warnings that occur.
     */
    Warn(Log.WARN),

    /**
     * Log errors that occur.
     */
    Error(Log.ERROR),

    /**
     * Log asserts (crashes) only.
     */
    Assert(Log.ASSERT),

    /**
     * Disable all logging.
     */
    None(8)

}