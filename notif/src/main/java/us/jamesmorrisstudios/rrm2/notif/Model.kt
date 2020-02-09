package us.jamesmorrisstudios.rrm2.notif

import android.app.NotificationManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import us.jamesmorrisstudios.rrm2.util.*

/**
 * Notif Database Item
 */
@Entity(tableName = "notif")
internal data class NotifItem(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val id: Int = 0,
    @ColumnInfo(name = "guid", index = true) val guid: Guid,
    @ColumnInfo(name = "show_time") val showTime: Long = 0,
    @ColumnInfo(name = "visible") val visible: Boolean = false,
    @ColumnInfo(name = "notification") val notification: Notification
)

/**
 * Notification object.
 */
data class Notification(
    val smallIcon: Uri,
    val title: String,
    val message: String,
    val channel: NotificationChannel,
    val image: Uri? = null,
    val actionComplete: NotificationAction? = null,
    val actionIncomplete: NotificationAction? = null,
    val actionSnooze: NotificationAction? = null) {

    /**
     * Serialize to json.
     */
    fun toJson(): JsonObject {
        return JsonObject.build().apply {
            setUri("smallIcon", smallIcon)
            setString("title", title)
            setString("message", message)
            setNotificationChannel("channel", channel)
            image?.let { setUri("image", it) }
            actionComplete?.let { setNotificationAction("action1", it) }
            actionIncomplete?.let { setNotificationAction("action2", it) }
            actionSnooze?.let { setNotificationAction("action3", it) }
        }
    }

    companion object {

        /**
         * Parse from json.
         */
        internal fun fromJson(json: JsonObject): Notification {
            return Notification(
                smallIcon = json.getUri("smallIcon"),
                title = json.getString("title"),
                message = json.getString("message"),
                channel = json.getNotificationChannel("channel"),
                image = json.optUri("image"),
                actionComplete = json.optNotificationAction("action1"),
                actionIncomplete = json.optNotificationAction("action2"),
                actionSnooze = json.optNotificationAction("action3")
            )
        }

    }

}

/**
 * Notification action
 */
data class NotificationAction(
    val iconId: Int,
    val text: String = "") {

    /**
     * Serialize to json.
     */
    fun toJson(): JsonObject {
        return JsonObject.build().apply {
            setInt("iconId", iconId)
            setString("text", text)
        }
    }

    companion object {

        /**
         * Builds a notification action with default options.
         */
        internal fun build(): NotificationAction  {
            return fromJson(JsonObject.build())
        }

        /**
         * Parse from json.
         */
        internal fun fromJson(json: JsonObject): NotificationAction {
            return NotificationAction(
                iconId = json.getInt("iconId"),
                text = json.getString("text")
            )
        }

    }

}

/**
 * Notification channel object.
 */
data class NotificationChannel(
    val id: String,
    val name: String = "",
    val importance: NotificationImportance = NotificationImportance.Default,
    val description: String = "",
    val badge: Boolean = false,
    val vibration: LongArray? = null,
    val lights: Int? = null,
    val sound: Uri? = null,
    val group: NotificationChannelGroup? = null) {

    /**
     * Serialize to json.
     */
    fun toJson(): JsonObject {
        return JsonObject.build().apply {
            setString("id", id)
            setString("name", name)
            setNotificationImportance("importance", importance)
            setString("description", description)
            setBoolean("badge", badge)
            vibration?.let { setLongArray("vibration", it) }
            lights?.let { setInt("lights", it) }
            sound?.let { setUri("sound", it) }
            group?.let { setNotificationChannelGroup("group", it) }
        }
    }

    companion object {

        /**
         * Builds a notification channel with default options.
         */
        internal fun build(): NotificationChannel {
            return fromJson(JsonObject.build())
        }

        /**
         * Parse from serialized json.
         */
        internal fun fromJson(json: JsonObject): NotificationChannel {
            return NotificationChannel(
                id = json.getString("id"),
                name = json.getString("name"),
                importance = json.getNotificationImportance("importance"),
                description = json.getString("description"),
                badge = json.getBoolean("badge"),
                vibration = json.optLongArray("vibration"),
                lights = json.optInt("lights"),
                sound = json.optUri("sound"),
                group = json.optNotificationChannelGroup("group")
            )
        }

    }

}

/**
 * Notification channel group object.
 */
data class NotificationChannelGroup(
    val id: String,
    val name: String = "") {

    /**
     * Serialize to json.
     */
    fun toJson(): JsonObject {
        return JsonObject.build().apply {
            setString("id", id)
            setString("name", name)
        }
    }

    companion object {

        /**
         * Builds a notification channel group with default options.
         */
        internal fun build(): NotificationChannelGroup {
            return fromJson(JsonObject.build())
        }

        /**
         * Parse from serialized json.
         */
        internal fun fromJson(json: JsonObject): NotificationChannelGroup {
            return NotificationChannelGroup(
                id = json.getString("id"),
                name = json.getString("name")
            )
        }

    }

}

/**
 * Notification importance or priority.
 */
enum class NotificationImportance {
    Min,
    Low,
    Default,
    High,
    Max;

    /**
     * Serialize to string.
     */
    override fun toString(): String {
        return when(this) {
            Min -> "min"
            Low -> "low"
            Default -> "default"
            High -> "high"
            Max -> "max"
        }
    }

    companion object {

        /**
         * Parse from string.
         */
        internal fun fromString(string: String): NotificationImportance {
            return when(string) {
                "min" -> Min
                "low" -> Low
                "default" -> Default
                "high" -> High
                "max" -> Max
                else -> Default
            }
        }

    }

}

/**
 * Extension to set the notification importance into a json object.
 */
internal fun JsonObject.setNotificationImportance(key: String, notificationImportance: NotificationImportance) {
    setString(key, notificationImportance.toString())
}

/**
 * Extension to retrieve the notification importance from a json object.
 */
internal fun JsonObject.getNotificationImportance(key: String): NotificationImportance {
    return optNotificationImportance(key) ?: NotificationImportance.Default
}

/**
 * Extension to optionally retrieve the notification importance from a json object.
 */
internal fun JsonObject.optNotificationImportance(key: String): NotificationImportance? {
    if(!has(key)) {
        return null
    }
    return NotificationImportance.fromString(getString(key))
}

/**
 * Extension to set the notification channel into a json object.
 */
internal fun JsonObject.setNotificationChannel(key: String, notificationChannel: NotificationChannel) {
    setJsonObject(key, notificationChannel.toJson())
}

/**
 * Extension to retrieve the notification channel from a json object.
 */
internal fun JsonObject.getNotificationChannel(key: String): NotificationChannel {
    return optNotificationChannel(key) ?: NotificationChannel.build()
}

/**
 * Extension to optionally retrieve the notification channel from a json object.
 */
internal fun JsonObject.optNotificationChannel(key: String): NotificationChannel? {
    if(!has(key)) {
        return null
    }
    return NotificationChannel.fromJson(getJsonObject(key))
}

/**
 * Extension to set the notification channel group into a json object.
 */
internal fun JsonObject.setNotificationChannelGroup(key: String, notificationChannelGroup: NotificationChannelGroup) {
    setJsonObject(key, notificationChannelGroup.toJson())
}

/**
 * Extension to retrieve the notification channel group from a json object.
 */
internal fun JsonObject.getNotificationChannelGroup(key: String): NotificationChannelGroup {
    return optNotificationChannelGroup(key) ?: NotificationChannelGroup.build()
}

/**
 * Extension to optionally retrieve the notification channel group from a json object.
 */
internal fun JsonObject.optNotificationChannelGroup(key: String): NotificationChannelGroup? {
    if(!has(key)) {
        return null
    }
    return NotificationChannelGroup.fromJson(getJsonObject(key))
}

/**
 * Extension to set the notification action into a json object.
 */
internal fun JsonObject.setNotificationAction(key: String, notificationAction: NotificationAction) {
    setJsonObject(key, notificationAction.toJson())
}

/**
 * Extension to retrieve the notification action from a json object.
 */
internal fun JsonObject.getNotificationAction(key: String): NotificationAction {
    return optNotificationAction(key) ?: NotificationAction.build()
}

/**
 * Extension to optionally retrieve the notification action from a json object.
 */
internal fun JsonObject.optNotificationAction(key: String): NotificationAction? {
    if(!has(key)) {
        return null
    }
    return NotificationAction.fromJson(getJsonObject(key))
}

/**
 * Returns the notification channel importance int as a notification importance enum.
 *
 * Returns default unless on Android API 26+
 */
internal fun Int.toNotificationImportance(): NotificationImportance {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        return when(this) {
            NotificationManager.IMPORTANCE_MIN -> NotificationImportance.Min
            NotificationManager.IMPORTANCE_LOW -> NotificationImportance.Low
            NotificationManager.IMPORTANCE_DEFAULT -> NotificationImportance.Default
            NotificationManager.IMPORTANCE_HIGH -> NotificationImportance.High
            NotificationManager.IMPORTANCE_MAX -> NotificationImportance.Max
            else -> NotificationImportance.Default
        }
    }
    return NotificationImportance.Default
}

/**
 * Returns the notification importance as a notification priority int.
 */
internal fun NotificationImportance.toPriority(): Int {
    return when(this) {
        NotificationImportance.Min -> NotificationCompat.PRIORITY_MIN
        NotificationImportance.Low -> NotificationCompat.PRIORITY_LOW
        NotificationImportance.Default -> NotificationCompat.PRIORITY_DEFAULT
        NotificationImportance.High -> NotificationCompat.PRIORITY_HIGH
        NotificationImportance.Max -> NotificationCompat.PRIORITY_MAX
    }
}

/**
 * Returns the notification importance as a notification channel importance int.
 *
 * Returns default unless on Android API 26+
 */
internal fun NotificationImportance.toImportance(): Int {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        return when(this) {
            NotificationImportance.Min -> NotificationManager.IMPORTANCE_MIN
            NotificationImportance.Low -> NotificationManager.IMPORTANCE_LOW
            NotificationImportance.Default -> NotificationManager.IMPORTANCE_DEFAULT
            NotificationImportance.High-> NotificationManager.IMPORTANCE_HIGH
            NotificationImportance.Max -> NotificationManager.IMPORTANCE_MAX
        }
    }
    return 3
}