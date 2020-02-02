package us.jamesmorrisstudios.rrm2.util

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Shared Preferences wrapper with suspending function support.
 */
interface Prefs {

    companion object {

        /**
         * Builds and returns an instance of the prefs.
         */
        fun build(context: Context, name: String): Prefs {
            return PrefsImpl(context, name)
        }

    }

    /**
     * Removes the value with the given key.
     */
    suspend fun remove(key: String)

    /**
     * Removes all values in the preferences file.
     */
    suspend fun removeAll()

    /**
     * Returns if the given key exists.
     */
    suspend fun has(key: String): Boolean

    /**
     * Sets a string value.
     */
    suspend fun setString(key: String, value: String)

    /**
     * Returns the string or the default value if it doesn't exist.
     */
    suspend fun getString(key: String): String

    /**
     * Returns the string or null if it doesn't exist.
     */
    suspend fun optString(key: String): String?

    /**
     * Sets a boolean value.
     */
    suspend fun setBoolean(key: String, value: Boolean)

    /**
     * Returns the boolean or the default value if it doesn't exist.
     */
    suspend fun getBoolean(key: String): Boolean

    /**
     * Returns the boolean or null if it doesn't exist.
     */
    suspend fun optBoolean(key: String): Boolean?

    /**
     * Sets an int value.
     */
    suspend fun setInt(key: String, value: Int)

    /**
     * Returns the int or the default value if it doesn't exist.
     */
    suspend fun getInt(key: String): Int

    /**
     * Returns the int or null if it doesn't exist.
     */
    suspend fun optInt(key: String): Int?

    /**
     * Sets a long value.
     */
    suspend fun setLong(key: String, value: Long)

    /**
     * Returns the long or the default value if it doesn't exist.
     */
    suspend fun getLong(key: String): Long

    /**
     * Returns the long or null if it doesn't exist.
     */
    suspend fun optLong(key: String): Long?

    /**
     * Sets a float value.
     */
    suspend fun setFloat(key: String, value: Float)

    /**
     * Returns the float or the default value if it doesn't exist.
     */
    suspend fun getFloat(key: String): Float

    /**
     * Returns the float or null if it doesn't exist.
     */
    suspend fun optFloat(key: String): Float?

    /**
     * Sets a double value.
     */
    suspend fun setDouble(key: String, value: Double)

    /**
     * Returns the double or the default value if it doesn't exist.
     */
    suspend fun getDouble(key: String): Double

    /**
     * Returns the double or null if it doesn't exist.
     */
    suspend fun optDouble(key: String): Double?

    /**
     * Sets a JsonObject value.
     */
    suspend fun setJsonObject(key: String, value: JsonObject)

    /**
     * Returns the JsonObject or the default value if it doesn't exist.
     */
    suspend fun getJsonObject(key: String): JsonObject

    /**
     * Returns the JsonObject or null if it doesn't exist.
     */
    suspend fun optJsonObject(key: String): JsonObject?

    /**
     * Sets a string value.
     */
    suspend fun setJsonArray(key: String, value: JsonArray)

    /**
     * Returns the JsonArray or the default value if it doesn't exist.
     */
    suspend fun getJsonArray(key: String): JsonArray

    /**
     * Returns the JsonArray or null if it doesn't exist.
     */
    suspend fun optJsonArray(key: String): JsonArray?

}

/**
 * Shared Preferences wrapper with suspending function support.
 */
internal class PrefsImpl internal constructor(private val context: Context, private val name: String) : Prefs {
    private val deferredUntilLoaded = GlobalScope.async {
        prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }
    private val lock: Mutex = Mutex()
    private lateinit var prefs: SharedPreferences

    /**
     * {inherited}
     */
    override suspend fun remove(key: String) {
        deferredUntilLoaded.await()
        lock.withLock {
            prefs.edit { remove(key) }
        }
    }

    /**
     * {inherited}
     */
    override suspend fun removeAll() {
        deferredUntilLoaded.await()
        lock.withLock {
            prefs.edit { clear() }
        }
    }

    /**
     * {inherited}
     */
    override suspend fun has(key: String): Boolean {
        deferredUntilLoaded.await()
        lock.withLock {
            return prefs.contains(key)
        }
    }

    /**
     * {inherited}
     */
    override suspend fun setString(key: String, value: String) {
        deferredUntilLoaded.await()
        lock.withLock {
            prefs.edit { putString(key, value) }
        }
    }

    /**
     * {inherited}
     */
    override suspend fun getString(key: String): String {
        return optString(key) ?: ""
    }

    /**
     * {inherited}
     */
    override suspend fun optString(key: String): String? {
        deferredUntilLoaded.await()
        lock.withLock {
            val value = prefs.all[key]
            if(value !is String) {
                return null
            }
            return value
        }
    }

    /**
     * {inherited}
     */
    override suspend fun setBoolean(key: String, value: Boolean) {
        deferredUntilLoaded.await()
        lock.withLock {
            prefs.edit { putBoolean(key, value) }
        }
    }

    /**
     * {inherited}
     */
    override suspend fun getBoolean(key: String): Boolean {
        return optBoolean(key) ?: false
    }

    /**
     * {inherited}
     */
    override suspend fun optBoolean(key: String): Boolean? {
        deferredUntilLoaded.await()
        lock.withLock {
            val value = prefs.all[key]
            if(value !is Boolean) {
                return null
            }
            return value
        }
    }

    /**
     * {inherited}
     */
    override suspend fun setInt(key: String, value: Int) {
        deferredUntilLoaded.await()
        lock.withLock {
            prefs.edit { putInt(key, value) }
        }
    }

    /**
     * {inherited}
     */
    override suspend fun getInt(key: String): Int {
        return optInt(key) ?: 0
    }

    /**
     * {inherited}
     */
    override suspend fun optInt(key: String): Int? {
        deferredUntilLoaded.await()
        lock.withLock {
            val value = prefs.all[key]
            if(value !is Number) {
                return null
            }
            return value.toInt()
        }
    }

    /**
     * {inherited}
     */
    override suspend fun setLong(key: String, value: Long) {
        deferredUntilLoaded.await()
        lock.withLock {
            prefs.edit { putLong(key, value) }
        }
    }

    /**
     * {inherited}
     */
    override suspend fun getLong(key: String): Long {
        return optLong(key) ?: 0L
    }

    /**
     * {inherited}
     */
    override suspend fun optLong(key: String): Long? {
        deferredUntilLoaded.await()
        lock.withLock {
            val value = prefs.all[key]
            if(value !is Number) {
                return null
            }
            return value.toLong()
        }
    }

    /**
     * {inherited}
     */
    override suspend fun setFloat(key: String, value: Float) {
        deferredUntilLoaded.await()
        lock.withLock {
            prefs.edit { putFloat(key, value) }
        }
    }

    /**
     * {inherited}
     */
    override suspend fun getFloat(key: String): Float {
        return optFloat(key) ?: 0.0f
    }

    /**
     * {inherited}
     */
    override suspend fun optFloat(key: String): Float? {
        deferredUntilLoaded.await()
        lock.withLock {
            val value = prefs.all[key]
            if(value !is Number) {
                return null
            }
            return value.toFloat()
        }
    }

    /**
     * {inherited}
     */
    override suspend fun setDouble(key: String, value: Double) {
        deferredUntilLoaded.await()
        lock.withLock {
            prefs.edit { putLong(key, value.toBits()) }
        }
    }

    /**
     * {inherited}
     */
    override suspend fun getDouble(key: String): Double {
        return optDouble(key) ?: 0.0
    }

    /**
     * {inherited}
     */
    override suspend fun optDouble(key: String): Double? {
        deferredUntilLoaded.await()
        lock.withLock {
            val value = prefs.all[key]
            if(value !is Number) {
                return null
            }
            return Double.fromBits(value.toLong())
        }
    }

    /**
     * {inherited}
     */
    override suspend fun setJsonObject(key: String, value: JsonObject) {
        deferredUntilLoaded.await()
        lock.withLock {
            prefs.edit { putString(key, value.toString()) }
        }
    }

    /**
     * {inherited}
     */
    override suspend fun getJsonObject(key: String): JsonObject {
        return optJsonObject(key) ?: JsonObject.build()
    }

    /**
     * {inherited}
     */
    override suspend fun optJsonObject(key: String): JsonObject? {
        deferredUntilLoaded.await()
        lock.withLock {
            val value = prefs.all[key]
            if(value !is String) {
                return null
            }
            return JsonObject.build(value)
        }
    }

    /**
     * {inherited}
     */
    override suspend fun setJsonArray(key: String, value: JsonArray) {
        deferredUntilLoaded.await()
        lock.withLock {
            prefs.edit { putString(key, value.toString()) }
        }
    }

    /**
     * {inherited}
     */
    override suspend fun getJsonArray(key: String): JsonArray {
        return optJsonArray(key) ?: JsonArray.build()
    }

    /**
     * {inherited}
     */
    override suspend fun optJsonArray(key: String): JsonArray? {
        deferredUntilLoaded.await()
        lock.withLock {
            val value = prefs.all[key]
            if(value !is String) {
                return null
            }
            return JsonArray.build(value)
        }
    }

}

/**
 * Sets a Uri value.
 */
suspend fun Prefs.setUri(key: String, value: Uri) {
    setString(key, value.toString())
}

/**
 * Returns the Uri or the default value if it doesn't exist.
 */
suspend fun Prefs.getUri(key: String): Uri {
    return optUri(key) ?: Uri.EMPTY
}

/**
 * Returns the Uri or null if it doesn't exist.
 */
suspend fun Prefs.optUri(key: String): Uri? {
    val value = optString(key) ?: return null
    return runCatching {
        Uri.parse(value)
    }.getOrNull()
}

/**
 * Removes the value with the given key.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.removeBlocking(key: String) = runBlocking { remove(key) }

/**
 * Removes all values in the preferences file.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.removeAllBlocking() = runBlocking { removeAll() }

/**
 * Returns if the given key exists.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.hasBlocking(key: String): Boolean = runBlocking { has(key) }

/**
 * Sets a string value.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.setStringBlocking(key: String, value: String) = runBlocking { setString(key, value) }

/**
 * Returns the string or the default value if it doesn't exist.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.getStringBlocking(key: String): String = runBlocking { getString(key) }

/**
 * Returns the string or null if it doesn't exist.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.optStringBlocking(key: String): String? = runBlocking { optString(key) }

/**
 * Sets a boolean value.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.setBooleanBlocking(key: String, value: Boolean) = runBlocking { setBoolean(key, value) }

/**
 * Returns the boolean or the default value if it doesn't exist.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.getBooleanBlocking(key: String): Boolean = runBlocking { getBoolean(key) }

/**
 * Returns the boolean or null if it doesn't exist.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.optBooleanBlocking(key: String): Boolean? = runBlocking { optBoolean(key) }

/**
 * Sets an int value.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.setIntBlocking(key: String, value: Int) = runBlocking { setInt(key, value) }

/**
 * Returns the int or the default value if it doesn't exist.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.getIntBlocking(key: String): Int = runBlocking { getInt(key) }

/**
 * Returns the int or null if it doesn't exist.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.optIntBlocking(key: String): Int? = runBlocking { optInt(key) }

/**
 * Sets a long value.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.setLongBlocking(key: String, value: Long) = runBlocking { setLong(key, value) }

/**
 * Returns the long or the default value if it doesn't exist.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.getLongBlocking(key: String): Long = runBlocking { getLong(key) }

/**
 * Returns the long or null if it doesn't exist.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.optLongBlocking(key: String): Long? = runBlocking { optLong(key) }

/**
 * Sets a float value.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.setFloatBlocking(key: String, value: Float) = runBlocking { setFloat(key, value) }

/**
 * Returns the float or the default value if it doesn't exist.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.getFloatBlocking(key: String): Float = runBlocking { getFloat(key) }

/**
 * Returns the float or nullif it doesn't exist.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.optFloatBlocking(key: String): Float? = runBlocking { optFloat(key) }

/**
 * Sets a double value.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.setDoubleBlocking(key: String, value: Double) = runBlocking { setDouble(key, value) }

/**
 * Returns the double or the default value if it doesn't exist.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.getDoubleBlocking(key: String): Double = runBlocking { getDouble(key) }

/**
 * Returns the double or null if it doesn't exist.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.optDoubleBlocking(key: String): Double? = runBlocking { optDouble(key) }

/**
 * Sets a JsonObject value.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.setJsonObjectBlocking(key: String, value: JsonObject) = runBlocking { setJsonObject(key, value) }

/**
 * Returns the JsonObject or the default value if it doesn't exist.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.getJsonObjectBlocking(key: String): JsonObject = runBlocking { getJsonObject(key) }

/**
 * Returns the JsonObject or null if it doesn't exist.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.optJsonObjectBlocking(key: String): JsonObject? = runBlocking { optJsonObject(key) }

/**
 * Sets a JsonArray value.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.setJsonArrayBlocking(key: String, value: JsonArray) = runBlocking { setJsonArray(key, value) }

/**
 * Returns the JsonArray or the default value if it doesn't exist.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.getJsonArrayBlocking(key: String): JsonArray = runBlocking { getJsonArray(key) }

/**
 * Returns the JsonArray or null if it doesn't exist.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.optJsonArrayBlocking(key: String): JsonArray? = runBlocking { optJsonArray(key) }

/**
 * Sets a Uri value.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.setUriBlocking(key: String, value: Uri) = runBlocking { setUri(key, value) }

/**
 * Returns the Uri or the default value if it doesn't exist.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.getUriBlocking(key: String): Uri = runBlocking { getUri(key) }

/**
 * Returns the Uri or null if it doesn't exist.
 *
 * Blocks until prefs fully load.
 */
fun Prefs.optUriBlocking(key: String): Uri? = runBlocking { optUri(key) }

/**
 * String property delegate backed by Prefs.
 *
 * Pass in an optional key or use the default property name.
 * Pass in an optional default value or use ""
 */
fun Prefs.string(key: String? = null, defaultValue: String = ""): ReadWriteProperty<Any, String> =
    PrefsProperty(prefs = this, key = key, defaultValue = defaultValue, getter = Prefs::getStringBlocking, setter = Prefs::setStringBlocking)

/**
 * Boolean property delegate backed by Prefs.
 *
 * Pass in an optional key or use the default property name.
 * Pass in an optional default value or use false
 */
fun Prefs.boolean(key: String? = null, defaultValue: Boolean = false): ReadWriteProperty<Any, Boolean> =
    PrefsProperty(prefs = this, key = key, defaultValue = defaultValue, getter = Prefs::getBooleanBlocking, setter = Prefs::setBooleanBlocking)

/**
 * Int property delegate backed by Prefs.
 *
 * Pass in an optional key or use the default property name.
 * Pass in an optional default value or use 0
 */
fun Prefs.int(key: String? = null, defaultValue: Int = 0): ReadWriteProperty<Any, Int> =
    PrefsProperty(prefs = this, key = key, defaultValue = defaultValue, getter = Prefs::getIntBlocking, setter = Prefs::setIntBlocking)

/**
 * Long property delegate backed by Prefs.
 *
 * Pass in an optional key or use the default property name.
 * Pass in an optional default value or use 0L
 */
fun Prefs.long(key: String? = null, defaultValue: Long = 0L): ReadWriteProperty<Any, Long> =
    PrefsProperty(prefs = this, key = key, defaultValue = defaultValue, getter = Prefs::getLongBlocking, setter = Prefs::setLongBlocking)

/**
 * Float property delegate backed by Prefs.
 *
 * Pass in an optional key or use the default property name.
 * Pass in an optional default value or use 0.0f
 */
fun Prefs.float(key: String? = null, defaultValue: Float = 0.0f): ReadWriteProperty<Any, Float> =
    PrefsProperty(prefs = this, key = key, defaultValue = defaultValue, getter = Prefs::getFloatBlocking, setter = Prefs::setFloatBlocking)

/**
 * Double property delegate backed by Prefs.
 *
 * Pass in an optional key or use the default property name.
 * Pass in an optional default value or use 0.0
 */
fun Prefs.double(key: String? = null, defaultValue: Double = 0.0): ReadWriteProperty<Any, Double> =
    PrefsProperty(prefs = this, key = key, defaultValue = defaultValue, getter = Prefs::getDoubleBlocking, setter = Prefs::setDoubleBlocking)

/**
 * JsonObject property delegate backed by Prefs.
 *
 * Pass in an optional key or use the default property name.
 * Pass in an optional default value or use ""
 */
fun Prefs.jsonObject(key: String? = null, defaultValue: JsonObject = JsonObject.build()): ReadWriteProperty<Any, JsonObject> =
    PrefsProperty(prefs = this, key = key, defaultValue = defaultValue, getter = Prefs::getJsonObjectBlocking, setter = Prefs::setJsonObjectBlocking)

/**
 * JsonArray property delegate backed by Prefs.
 *
 * Pass in an optional key or use the default property name.
 * Pass in an optional default value or use ""
 */
fun Prefs.jsonArray(key: String? = null, defaultValue: JsonArray = JsonArray.build()): ReadWriteProperty<Any, JsonArray> =
    PrefsProperty(prefs = this, key = key, defaultValue = defaultValue, getter = Prefs::getJsonArrayBlocking, setter = Prefs::setJsonArrayBlocking)

/**
 * Uri property delegate backed by Prefs.
 *
 * Pass in an optional key or use the default property name.
 * Pass in an optional default value or use ""
 */
fun Prefs.uri(key: String? = null, defaultValue: Uri = Uri.EMPTY): ReadWriteProperty<Any, Uri> =
    PrefsProperty(prefs = this, key = key, defaultValue = defaultValue, getter = Prefs::getUriBlocking, setter = Prefs::setUriBlocking)

/**
 * Generic property delegate backed by Prefs.
 */
private class PrefsProperty<T>(private val prefs: Prefs,
                               private val key: String? = null,
                               private val defaultValue: T,
                               private val getter: Prefs.(String) -> T,
                               private val setter: Prefs.(String, T) -> Unit) : ReadWriteProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return prefs.getter(key.orElse(property.name)).orElse(defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        prefs.setter(key.orElse(property.name), value)
    }

}

