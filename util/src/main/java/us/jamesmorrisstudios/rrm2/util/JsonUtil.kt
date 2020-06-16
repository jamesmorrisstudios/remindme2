package us.jamesmorrisstudios.rrm2.util

import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject

/**
 * Custom Json Object that builds on top of the platform JSONObject and provides easier to use functionality.
 */
interface JsonObject {

    companion object {

        /**
         * Builds and returns an empty Json Object.
         */
        fun build(): JsonObject {
            return JsonObjectImpl(JSONObject())
        }

        /**
         * Builds and returns a Json Object parsed from the given string.
         *
         * On error this returns an empty Json Object.
         */
        fun build(string: String): JsonObject {
            return runCatching {
                JsonObjectImpl(JSONObject(string))
            }.getOrElse {
                JsonObjectImpl(JSONObject())
            }
        }

        /**
         * Builds and returns a Json Object from a platform native JSONObject.
         */
        fun build(jsonObject: JSONObject): JsonObject {
            return JsonObjectImpl(jsonObject)
        }

    }

    /**
     * Returns the platform native JSONObject.
     */
    fun toJSONObject(): JSONObject

    /**
     * Serializes the object with pretty print formatting.
     */
    fun prettyPrint(): String

    /**
     * Serializes the object in minimized format.
     */
    override fun toString(): String

    /**
     * Returns a deep copy of the object.
     */
    fun copy(): JsonObject

    /**
     * Returns the number of elements in the object.
     */
    fun length(): Int

    /**
     * Returns if the object has the given key.
     */
    fun has(key: String): Boolean

    /**
     * Removes the key/value with the given key if it exists.
     */
    fun remove(key: String)

    /**
     * Set a string value with the given key.
     */
    fun setString(key: String, value: String)

    /**
     * Returns the string value for the given key with a default value of "".
     */
    fun getString(key: String): String

    /**
     * Returns the string value for the given key with a default value of null.
     */
    fun optString(key: String): String?

    /**
     * Set a boolean value with the given key.
     */
    fun setBoolean(key: String, value: Boolean)

    /**
     * Returns the boolean value for the given key with a default value of false.
     */
    fun getBoolean(key: String): Boolean

    /**
     * Returns the boolean value for the given key with a default value of null.
     */
    fun optBoolean(key: String): Boolean?

    /**
     * Set an int value with the given key.
     */
    fun setInt(key: String, value: Int)

    /**
     * Returns the int value for the given key with a default value of 0.
     */
    fun getInt(key: String): Int

    /**
     * Returns the int value for the given key with a default value of null.
     */
    fun optInt(key: String): Int?

    /**
     * Set a long value with the given key.
     */
    fun setLong(key: String, value: Long)

    /**
     * Returns the long value for the given key with a default value of 0.
     */
    fun getLong(key: String): Long

    /**
     * Returns the long value for the given key with a default value of null.
     */
    fun optLong(key: String): Long?

    /**
     * Set a float value with the given key.
     */
    fun setFloat(key: String, value: Float)

    /**
     * Returns the float value for the given key with a default value of 0.0.
     */
    fun getFloat(key: String): Float

    /**
     * Returns the float value for the given key with a default value of null.
     */
    fun optFloat(key: String): Float?

    /**
     * Set a double value with the given key.
     */
    fun setDouble(key: String, value: Double)

    /**
     * Returns the double value for the given key with a default value of 0.0.
     */
    fun getDouble(key: String): Double

    /**
     * Returns the double value for the given key with a default value of null.
     */
    fun optDouble(key: String): Double?

    /**
     * Set a JsonObject value with the given key.
     */
    fun setJsonObject(key: String, value: JsonObject)

    /**
     * Returns the JsonObject value for the given key with a default value of an empty object.
     */
    fun getJsonObject(key: String): JsonObject

    /**
     * Returns the JsonObject value for the given key with a default value of null.
     */
    fun optJsonObject(key: String): JsonObject?

    /**
     * Set a JsonArray value with the given key.
     */
    fun setJsonArray(key: String, value: JsonArray)

    /**
     * Returns the JsonArray value for the given key with a default value of an empty array.
     */
    fun getJsonArray(key: String): JsonArray

    /**
     * Returns the JsonArray value for the given key with a default value of null.
     */
    fun optJsonArray(key: String): JsonArray?

}

/**
 * Custom Json Object Implementation.
 */
private class JsonObjectImpl(private val obj: JSONObject) : JsonObject {

    /**
     * {inherited}
     */
    override fun toJSONObject(): JSONObject {
        return obj
    }

    /**
     * {inherited}
     */
    override fun prettyPrint(): String {
        return obj.toString(2)
    }

    /**
     * {inherited}
     */
    override fun toString(): String {
        return obj.toString()
    }

    /**
     * {inherited}
     */
    override fun copy(): JsonObject {
        return JsonObject.build(this.toString())
    }

    /**
     * {inherited}
     */
    override fun length(): Int {
        return obj.length()
    }

    /**
     * {inherited}
     */
    override fun has(key: String): Boolean {
        return obj.has(key)
    }

    /**
     * {inherited}
     */
    override fun remove(key: String) {
        obj.remove(key)
    }

    /**
     * {inherited}
     */
    override fun setString(key: String, value: String) {
        obj.putOpt(key, value)
    }

    /**
     * {inherited}
     */
    override fun getString(key: String): String {
        return optString(key) ?: ""
    }

    /**
     * {inherited}
     */
    override fun optString(key: String): String? {
        val value = obj.opt(key)
        if (value !is String) {
            return null
        }
        return value
    }

    /**
     * {inherited}
     */
    override fun setBoolean(key: String, value: Boolean) {
        obj.putOpt(key, value)
    }

    /**
     * {inherited}
     */
    override fun getBoolean(key: String): Boolean {
        return optBoolean(key) ?: false
    }

    /**
     * {inherited}
     */
    override fun optBoolean(key: String): Boolean? {
        val value = obj.opt(key)
        if (value !is Boolean) {
            return null
        }
        return value
    }

    /**
     * {inherited}
     */
    override fun setInt(key: String, value: Int) {
        obj.putOpt(key, value)
    }

    /**
     * {inherited}
     */
    override fun getInt(key: String): Int {
        return optInt(key) ?: 0
    }

    /**
     * {inherited}
     */
    override fun optInt(key: String): Int? {
        val value = obj.opt(key)
        if (value !is Number) {
            return null
        }
        return value.toInt()
    }

    /**
     * {inherited}
     */
    override fun setLong(key: String, value: Long) {
        obj.putOpt(key, value)
    }

    /**
     * {inherited}
     */
    override fun getLong(key: String): Long {
        return optLong(key) ?: 0L
    }

    /**
     * {inherited}
     */
    override fun optLong(key: String): Long? {
        val value = obj.opt(key)
        if (value !is Number) {
            return null
        }
        return value.toLong()
    }

    /**
     * {inherited}
     */
    override fun setFloat(key: String, value: Float) {
        obj.putOpt(key, value)
    }

    /**
     * {inherited}
     */
    override fun getFloat(key: String): Float {
        return optFloat(key) ?: 0.0f
    }

    /**
     * {inherited}
     */
    override fun optFloat(key: String): Float? {
        val value = obj.opt(key)
        if (value !is Number) {
            return null
        }
        return value.toFloat()
    }

    /**
     * {inherited}
     */
    override fun setDouble(key: String, value: Double) {
        obj.putOpt(key, value)
    }

    /**
     * {inherited}
     */
    override fun getDouble(key: String): Double {
        return optDouble(key) ?: 0.0
    }

    /**
     * {inherited}
     */
    override fun optDouble(key: String): Double? {
        val value = obj.opt(key)
        if (value !is Number) {
            return null
        }
        return value.toDouble()
    }

    /**
     * {inherited}
     */
    override fun setJsonObject(key: String, value: JsonObject) {
        obj.putOpt(key, value.toJSONObject())
    }

    /**
     * {inherited}
     */
    override fun getJsonObject(key: String): JsonObject {
        return optJsonObject(key) ?: JsonObject.build()
    }

    /**
     * {inherited}
     */
    override fun optJsonObject(key: String): JsonObject? {
        val value = obj.opt(key)
        if (value !is JSONObject) {
            return null
        }
        return value.toJsonObject()
    }

    /**
     * {inherited}
     */
    override fun setJsonArray(key: String, value: JsonArray) {
        obj.putOpt(key, value.toJSONArray())
    }

    /**
     * {inherited}
     */
    override fun getJsonArray(key: String): JsonArray {
        return optJsonArray(key) ?: JsonArray.build()
    }

    /**
     * {inherited}
     */
    override fun optJsonArray(key: String): JsonArray? {
        val value = obj.opt(key)
        if (value !is JSONArray) {
            return null
        }
        return value.toJsonArray()
    }

}

/**
 * Custom Json Array that builds on top of the platform JSONArray and provides easier to use functionality.
 */
interface JsonArray {

    companion object {

        /**
         * Builds and returns an empty Json Array.
         */
        fun build(): JsonArray {
            return JsonArrayImpl(JSONArray())
        }

        /**
         * Builds and returns a Json Array parsed from the given string.
         *
         * On error this returns an empty Json Array.
         */
        fun build(string: String): JsonArray {
            return runCatching {
                JsonArrayImpl(JSONArray(string))
            }.getOrElse {
                JsonArrayImpl(JSONArray())
            }
        }

        /**
         * Builds and returns a Json Array from a platform native JSONArray.
         */
        fun build(jsonArray: JSONArray): JsonArray {
            return JsonArrayImpl(jsonArray)
        }

    }

    /**
     * Returns the platform native JSONArray.
     */
    fun toJSONArray(): JSONArray

    /**
     * Serializes the array with pretty print formatting.
     */
    fun prettyPrint(): String

    /**
     * Serializes the array in minimized format.
     */
    override fun toString(): String

    /**
     * Returns a deepcopy of the array.
     */
    fun copy(): JsonArray

    /**
     * Returns the number of elements in the array.
     */
    fun length(): Int

    /**
     * Removes the element at the given index and shifts additional items down.
     */
    fun remove(index: Int)

    /**
     * Add a string value to the end of the array.
     */
    fun addString(value: String)

    /**
     * Return the string value at the given index with a default value of "".
     */
    fun getString(index: Int): String

    /**
     * Return the string value at the given index with a default value of null.
     */
    fun optString(index: Int): String?

    /**
     * Add a boolean value to the end of the array.
     */
    fun addBoolean(value: Boolean)

    /**
     * Return the boolean value at the given index with a default value of false.
     */
    fun getBoolean(index: Int): Boolean

    /**
     * Return the boolean value at the given index with a default value of null.
     */
    fun optBoolean(index: Int): Boolean?

    /**
     * Add an int value to the end of the array.
     */
    fun addInt(value: Int)

    /**
     * Return the int value at the given index with a default value of 0.
     */
    fun getInt(index: Int): Int

    /**
     * Return the int value at the given index with a default value of null.
     */
    fun optInt(index: Int): Int?

    /**
     * Add a long value to the end of the array.
     */
    fun addLong(value: Long)

    /**
     * Return the long value at the given index with a default value of 0.
     */
    fun getLong(index: Int): Long

    /**
     * Return the long value at the given index with a default value of null.
     */
    fun optLong(index: Int): Long?

    /**
     * Add a float value to the end of the array.
     */
    fun addFloat(value: Float)

    /**
     * Return the float value at the given index with a default value of 0.0.
     */
    fun getFloat(index: Int): Float

    /**
     * Return the float value at the given index with a default value of null.
     */
    fun optFloat(index: Int): Float?

    /**
     * Add a double value to the end of the array.
     */
    fun addDouble(value: Double)

    /**
     * Return the double value at the given index with a default value of 0.0.
     */
    fun getDouble(index: Int): Double

    /**
     * Return the double value at the given index with a default value of null.
     */
    fun optDouble(index: Int): Double?

    /**
     * Add a JsonObject value to the end of the array.
     */
    fun addJsonObject(value: JsonObject)

    /**
     * Return the JsonObject value at the given index with a default value of an empty object.
     */
    fun getJsonObject(index: Int): JsonObject

    /**
     * Return the JsonObject value at the given index with a default value of null.
     */
    fun optJsonObject(index: Int): JsonObject?

    /**
     * Add a JsonArray value to the end of the array.
     */
    fun addJsonArray(value: JsonArray)

    /**
     * Return the JsonArray value at the given index with a default value of an empty array.
     */
    fun getJsonArray(index: Int): JsonArray

    /**
     * Return the JsonArray value at the given index with a default value of null.
     */
    fun optJsonArray(index: Int): JsonArray?

}

/**
 * Custom Json Array Implementation.
 */
private class JsonArrayImpl(private val arr: JSONArray) : JsonArray {

    /**
     * {inherited}
     */
    override fun toJSONArray(): JSONArray {
        return arr
    }

    /**
     * {inherited}
     */
    override fun prettyPrint(): String {
        return arr.toString(2)
    }

    /**
     * {inherited}
     */
    override fun toString(): String {
        return arr.toString()
    }

    /**
     * {inherited}
     */
    override fun copy(): JsonArray {
        return JsonArray.build(this.toString())
    }

    /**
     * {inherited}
     */
    override fun length(): Int {
        return arr.length()
    }

    /**
     * {inherited}
     */
    override fun remove(index: Int) {
        arr.remove(index)
    }

    /**
     * {inherited}
     */
    override fun addString(value: String) {
        arr.put(value)
    }

    /**
     * {inherited}
     */
    override fun getString(index: Int): String {
        return optString(index) ?: ""
    }

    /**
     * {inherited}
     */
    override fun optString(index: Int): String? {
        val value = arr.opt(index)
        if (value !is String) {
            return null
        }
        return value
    }

    /**
     * {inherited}
     */
    override fun addBoolean(value: Boolean) {
        arr.put(value)
    }

    /**
     * {inherited}
     */
    override fun getBoolean(index: Int): Boolean {
        return optBoolean(index) ?: false
    }

    /**
     * {inherited}
     */
    override fun optBoolean(index: Int): Boolean? {
        val value = arr.opt(index)
        if (value !is Boolean) {
            return null
        }
        return value
    }

    /**
     * {inherited}
     */
    override fun addInt(value: Int) {
        arr.put(value)
    }

    /**
     * {inherited}
     */
    override fun getInt(index: Int): Int {
        return optInt(index) ?: 0
    }

    /**
     * {inherited}
     */
    override fun optInt(index: Int): Int? {
        val value = arr.opt(index)
        if (value !is Number) {
            return null
        }
        return value.toInt()
    }

    /**
     * {inherited}
     */
    override fun addLong(value: Long) {
        arr.put(value)
    }

    /**
     * {inherited}
     */
    override fun getLong(index: Int): Long {
        return optLong(index) ?: 0L
    }

    /**
     * {inherited}
     */
    override fun optLong(index: Int): Long? {
        val value = arr.opt(index)
        if (value !is Number) {
            return null
        }
        return value.toLong()
    }

    /**
     * {inherited}
     */
    override fun addFloat(value: Float) {
        arr.put(value)
    }

    /**
     * {inherited}
     */
    override fun getFloat(index: Int): Float {
        return optFloat(index) ?: 0.0f
    }

    /**
     * {inherited}
     */
    override fun optFloat(index: Int): Float? {
        val value = arr.opt(index)
        if (value !is Number) {
            return null
        }
        return value.toFloat()
    }

    /**
     * {inherited}
     */
    override fun addDouble(value: Double) {
        arr.put(value)
    }

    /**
     * {inherited}
     */
    override fun getDouble(index: Int): Double {
        return optDouble(index) ?: 0.0
    }

    /**
     * {inherited}
     */
    override fun optDouble(index: Int): Double? {
        val value = arr.opt(index)
        if (value !is Number) {
            return null
        }
        return value.toDouble()
    }

    /**
     * {inherited}
     */
    override fun addJsonObject(value: JsonObject) {
        arr.put(value.toJSONObject())
    }

    /**
     * {inherited}
     */
    override fun getJsonObject(index: Int): JsonObject {
        return optJsonObject(index) ?: JsonObject.build()
    }

    /**
     * {inherited}
     */
    override fun optJsonObject(index: Int): JsonObject? {
        val value = arr.opt(index)
        if (value !is JSONObject) {
            return null
        }
        return value.toJsonObject()
    }

    /**
     * {inherited}
     */
    override fun addJsonArray(value: JsonArray) {
        arr.put(value.toJSONArray())
    }

    /**
     * {inherited}
     */
    override fun getJsonArray(index: Int): JsonArray {
        return optJsonArray(index) ?: JsonArray.build()
    }

    /**
     * {inherited}
     */
    override fun optJsonArray(index: Int): JsonArray? {
        val value = arr.opt(index)
        if (value !is JSONArray) {
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
