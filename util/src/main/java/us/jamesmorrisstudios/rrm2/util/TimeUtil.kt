package us.jamesmorrisstudios.rrm2.util

import android.os.SystemClock

/**
 * Returns the current system time as a unix timestamp in milliseconds.
 */
fun currentTimeMillis(): Long {
    return System.currentTimeMillis()
}

/**
 * Returns the current system time as a unix timestamp in seconds.
 */
fun currentTime(): Long {
    return currentTimeMillis() / 1000
}

/**
 * Returns the current device uptime including time spent in deep sleep in milliseconds.
 */
fun uptimeMillis(): Long {
    return SystemClock.elapsedRealtime()
}

/**
 * Returns the current device uptime including time spent in deep sleep in seconds.
 */
fun uptime(): Long {
    return uptimeMillis() / 1000
}

/**
 * Returns if the device has rebooted since the given time.
 */
fun isRebootedSince(time: Long): Boolean {
    val bootTime = currentTimeMillis() - uptimeMillis()
    return time < bootTime
}