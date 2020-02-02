package us.jamesmorrisstudios.rrm2.util

import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject

interface JsonObject {

    companion object {

        fun build(): JsonObject {
            return JsonObjectImpl(JSONObject())
        }

        fun build(string: String): JsonObject {
            return runCatching {
                JsonObjectImpl(JSONObject(string))
            }.getOrElse {
                JsonObjectImpl(JSONObject())
            }
        }

        fun build(jsonObject: JSONObject): JsonObject {
            return JsonObjectImpl(jsonObject)
        }

    }

    fun toJSONObject(): JSONObject

    fun prettyPrint(): String

    override fun toString(): String

    fun copy(): JsonObject

    fun length(): Int

    fun has(key: String): Boolean

    fun remove(key: String)

    fun setString(key: String, value: String)

    fun getString(key: String): String

    fun optString(key: String): String?

    fun setBoolean(key: String, value: Boolean)

    fun getBoolean(key: String): Boolean

    fun optBoolean(key: String): Boolean?

    fun setInt(key: String, value: Int)

    fun getInt(key: String): Int

    fun optInt(key: String): Int?

    fun setLong(key: String, value: Long)

    fun getLong(key: String): Long

    fun optLong(key: String): Long?

    fun setFloat(key: String, value: Float)

    fun getFloat(key: String): Float

    fun optFloat(key: String): Float?

    fun setDouble(key: String, value: Double)

    fun getDouble(key: String): Double

    fun optDouble(key: String): Double?

    fun setJsonObject(key: String, value: JsonObject)

    fun getJsonObject(key: String): JsonObject

    fun optJsonObject(key: String): JsonObject?

    fun setJsonArray(key: String, value: JsonArray)

    fun getJsonArray(key: String): JsonArray

    fun optJsonArray(key: String): JsonArray?

}

private class JsonObjectImpl(private val obj: JSONObject) : JsonObject {

    override fun toJSONObject(): JSONObject {
        return obj
    }

    override fun prettyPrint(): String {
        return obj.toString(2)
    }

    override fun toString(): String {
        return obj.toString() ?: "{}"
    }

    override fun copy(): JsonObject {
        return JsonObject.build(this.toString())
    }

    override fun length(): Int {
        return obj.length()
    }

    override fun has(key: String): Boolean {
        return obj.has(key)
    }

    override fun remove(key: String) {
        obj.remove(key)
    }

    override fun setString(key: String, value: String) {
        obj.putOpt(key, value)
    }

    override fun getString(key: String): String {
        return optString(key) ?: ""
    }

    override fun optString(key: String): String? {
        val value = obj.opt(key)
        if(value !is String) {
            return null
        }
        return value
    }

    override fun setBoolean(key: String, value: Boolean) {
        obj.putOpt(key, value)
    }

    override fun getBoolean(key: String): Boolean {
        return optBoolean(key) ?: false
    }

    override fun optBoolean(key: String): Boolean? {
        val value = obj.opt(key)
        if(value !is Boolean) {
            return null
        }
        return value
    }

    override fun setInt(key: String, value: Int) {
        obj.putOpt(key, value)
    }

    override fun getInt(key: String): Int {
        return optInt(key) ?: 0
    }

    override fun optInt(key: String): Int? {
        val value = obj.opt(key)
        if(value !is Number) {
            return null
        }
        return value.toInt()
    }

    override fun setLong(key: String, value: Long) {
        obj.putOpt(key, value)
    }

    override fun getLong(key: String): Long {
        return optLong(key) ?: 0L
    }

    override fun optLong(key: String): Long? {
        val value = obj.opt(key)
        if(value !is Number) {
            return null
        }
        return value.toLong()
    }

    override fun setFloat(key: String, value: Float) {
        obj.putOpt(key, value)
    }

    override fun getFloat(key: String): Float {
        return optFloat(key) ?: 0.0f
    }

    override fun optFloat(key: String): Float? {
        val value = obj.opt(key)
        if(value !is Number) {
            return null
        }
        return value.toFloat()
    }

    override fun setDouble(key: String, value: Double) {
        obj.putOpt(key, value)
    }

    override fun getDouble(key: String): Double {
        return optDouble(key) ?: 0.0
    }

    override fun optDouble(key: String): Double? {
        val value = obj.opt(key)
        if(value !is Number) {
            return null
        }
        return value.toDouble()
    }

    override fun setJsonObject(key: String, value: JsonObject) {
        obj.putOpt(key, value.toJSONObject())
    }

    override fun getJsonObject(key: String): JsonObject {
        return optJsonObject(key) ?: JsonObject.build()
    }

    override fun optJsonObject(key: String): JsonObject? {
        val value = obj.opt(key)
        if(value !is JSONObject) {
            return null
        }
        return value.toJsonObject()
    }

    override fun setJsonArray(key: String, value: JsonArray) {
        obj.putOpt(key, value.toJSONArray())
    }

    override fun getJsonArray(key: String): JsonArray {
        return optJsonArray(key) ?: JsonArray.build()
    }

    override fun optJsonArray(key: String): JsonArray? {
        val value = obj.opt(key)
        if(value !is JSONArray) {
            return null
        }
        return value.toJsonArray()
    }

}

interface JsonArray {

    companion object {

        fun build(): JsonArray {
            return JsonArrayImpl(JSONArray())
        }

        fun build(string: String): JsonArray {
            return runCatching {
                JsonArrayImpl(JSONArray(string))
            }.getOrElse {
                JsonArrayImpl(JSONArray())
            }
        }

        fun build(jsonArray: JSONArray): JsonArray {
            return JsonArrayImpl(jsonArray)
        }

    }

    fun toJSONArray(): JSONArray

    fun prettyPrint(): String

    override fun toString(): String

    fun copy(): JsonArray

    fun length(): Int

    fun remove(index: Int)

    fun addString(value: String)

    fun getString(index: Int): String

    fun optString(index: Int): String?

    fun addBoolean(value: Boolean)

    fun getBoolean(index: Int): Boolean

    fun optBoolean(index: Int): Boolean?

    fun addInt(value: Int)

    fun getInt(index: Int): Int

    fun optInt(index: Int): Int?

    fun addLong(value: Long)

    fun getLong(index: Int): Long

    fun optLong(index: Int): Long?

    fun addFloat(value: Float)

    fun getFloat(index: Int): Float

    fun optFloat(index: Int): Float?

    fun addDouble(value: Double)

    fun getDouble(index: Int): Double

    fun optDouble(index: Int): Double?

    fun addJsonObject(value: JsonObject)

    fun getJsonObject(index: Int): JsonObject

    fun optJsonObject(index: Int): JsonObject?

    fun addJsonArray(value: JsonArray)

    fun getJsonArray(index: Int): JsonArray

    fun optJsonArray(index: Int): JsonArray?

}

private class JsonArrayImpl(private val arr: JSONArray) : JsonArray {

    override fun toJSONArray(): JSONArray {
        return arr
    }

    override fun prettyPrint(): String {
        return arr.toString(2)
    }

    override fun toString(): String {
        return arr.toString() ?: "[]"
    }

    override fun copy(): JsonArray {
        return JsonArray.build(this.toString())
    }

    override fun length(): Int {
        return arr.length()
    }

    override fun remove(index: Int) {
        arr.remove(index)
    }

    override fun addString(value: String) {
        arr.put(value)
    }

    override fun getString(index: Int): String {
        return optString(index) ?: ""
    }

    override fun optString(index: Int): String? {
        val value = arr.opt(index)
        if(value !is String) {
            return null
        }
        return value
    }

    override fun addBoolean(value: Boolean) {
        arr.put(value)
    }

    override fun getBoolean(index: Int): Boolean {
        return optBoolean(index) ?: false
    }

    override fun optBoolean(index: Int): Boolean? {
        val value = arr.opt(index)
        if(value !is Boolean) {
            return null
        }
        return value
    }

    override fun addInt(value: Int) {
        arr.put(value)
    }

    override fun getInt(index: Int): Int {
        return optInt(index) ?: 0
    }

    override fun optInt(index: Int): Int? {
        val value = arr.opt(index)
        if(value !is Number) {
            return null
        }
        return value.toInt()
    }

    override fun addLong(value: Long) {
        arr.put(value)
    }

    override fun getLong(index: Int): Long {
        return optLong(index) ?: 0L
    }

    override fun optLong(index: Int): Long? {
        val value = arr.opt(index)
        if(value !is Number) {
            return null
        }
        return value.toLong()
    }

    override fun addFloat(value: Float) {
        arr.put(value)
    }

    override fun getFloat(index: Int): Float {
        return optFloat(index) ?: 0.0f
    }

    override fun optFloat(index: Int): Float? {
        val value = arr.opt(index)
        if(value !is Number) {
            return null
        }
        return value.toFloat()
    }

    override fun addDouble(value: Double) {
        arr.put(value)
    }

    override fun getDouble(index: Int): Double {
        return optDouble(index) ?: 0.0
    }

    override fun optDouble(index: Int): Double? {
        val value = arr.opt(index)
        if(value !is Number) {
            return null
        }
        return value.toDouble()
    }

    override fun addJsonObject(value: JsonObject) {
        arr.put(value.toJSONObject())
    }

    override fun getJsonObject(index: Int): JsonObject {
        return optJsonObject(index) ?: JsonObject.build()
    }

    override fun optJsonObject(index: Int): JsonObject? {
        val value = arr.opt(index)
        if(value !is JSONObject) {
            return null
        }
        return value.toJsonObject()
    }

    override fun addJsonArray(value: JsonArray) {
        arr.put(value.toJSONArray())
    }

    override fun getJsonArray(index: Int): JsonArray {
        return optJsonArray(index) ?: JsonArray.build()
    }

    override fun optJsonArray(index: Int): JsonArray? {
        val value = arr.opt(index)
        if(value !is JSONArray) {
            return null
        }
        return value.toJsonArray()
    }

}

/**
 * Converts a platform JSONObject to a JsonObject.
 */
fun JSONObject.toJsonObject(): JsonObject {
    return JsonObject.build(this)
}

/**
 * Converts a platform JSONArray to a JsonArray
 */
fun JSONArray.toJsonArray(): JsonArray {
    return JsonArray.build(this)
}

/**
 * Parses the string into a json object. If not valid json it returns an empty object.
 */
fun String.parseJsonObject(): JsonObject {
    return JsonObject.build(this)
}

/**
 * Parses the string into a json array. If not valid json it returns an empty array.
 */
fun String.parseJsonArray(): JsonArray {
    return JsonArray.build(this)
}

/**
 * Sets the long array as a JsonArray.
 */
fun JsonObject.setLongArray(key: String, value: LongArray) {
    setJsonArray(key, runCatching {
        JsonArray.build(JSONArray(value))
    }.getOrElse { JsonArray.build() })
}

/**
 * Returns the long array or an empty array on error.
 */
fun JsonObject.getLongArray(key: String): LongArray {
    return optLongArray(key) ?: longArrayOf()
}

/**
 * Returns the value for the key as a long array or null if unable.
 */
fun JsonObject.optLongArray(key: String): LongArray? {
    val value = optJsonArray(key) ?: return null
    return LongArray(value.length()) {
        value.getLong(it)
    }
}

/**
 * Sets the uri as a string.
 */
fun JsonObject.setUri(key: String, value: Uri) {
    setString(key, value.toString())
}

/**
 * Returns the uri or Uri.EMPTY on error.
 */
fun JsonObject.getUri(key: String): Uri {
    return optUri(key) ?: Uri.EMPTY
}

/**
 * Returns the value for the key parsed as a uri or null if unable.
 */
fun JsonObject.optUri(key: String): Uri? {
    val value = optString(key) ?: return null
    return runCatching {
        Uri.parse(value)
    }.getOrNull()
}
