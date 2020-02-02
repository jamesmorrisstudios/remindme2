package us.jamesmorrisstudios.rrm2.util

/**
 * Returns the given value if not null and the default if it is null.
 */
inline fun <T> T?.orElse(default: T): T {
    if(this == null) {
        return default
    }
    return this
}

/**
 * Returns the given value if not null and the default if it is null.
 */
inline fun <T> T?.orElseGet(default: () -> T): T {
    if(this == null) {
        return default.invoke()
    }
    return this
}