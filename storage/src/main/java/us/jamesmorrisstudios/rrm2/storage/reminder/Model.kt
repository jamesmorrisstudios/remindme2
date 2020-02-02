package us.jamesmorrisstudios.rrm2.storage.reminder

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import us.jamesmorrisstudios.rrm2.util.Guid

// TODO modules to create
//   Location (geofence handler, map for picking a location)
//   RSS (RSS feed retrieval)

/**
 * Reminder Database Item
 *
 * Top level of a reminder that contains primary metadata.
 */
//@Fts4 // TODO figure out why this doesn't work
@Entity(tableName = "reminder")
data class ReminderItem(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val id: Int = 0,
    @ColumnInfo(name = "details_id") val detailsId: Int,
    @ColumnInfo(name = "guid", index = true) val guid: Guid,
    @ColumnInfo(name = "created_time") val createdTime: Long,
    @ColumnInfo(name = "last_modified_time") val lastModifiedTime: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "enabled") val enabled: Boolean
)

/**
 * Reminder Details Database Item
 *
 * Details for all the reminder options. Contains all the fixed options regarding a reminder.
 *
 * TODO finish building out the reminder details.
 */
@Entity(tableName = "reminder_detail")
data class ReminderDetailsItem(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val id: Int = 0

    // From Reminder Item
    // Title
    // Message
    // Enabled

    // From Reminder Details
    // Snooze Options
    // Auto Snooze Options
    //  Notification
    //    Icon (Uri)
    //    Sound (Nullable Uri)
    //    Image (Nullable String)
    //    Vibrate (Enabled)
    //    LED (Enabled)
    //    Priority (Min, Low, Default, High, Max)


    // TODO figure out below.

    //  Message (Single, List, RSS)
    //    Single (Message)
    //    List (Message List, Order (Ordered, Random))
    //    RSS (Url, Ordering (First, Last, Random))


    //  Criteria (Window)
    //    Location (In Area, Not In Area) ???

    //  Trigger
    //    Location (In Area, Not In Area) ???

)

data class SnoozeOptions(
    val enabled: Boolean,
    val duration: Long
)

data class AutoSnoozeOptions(
    val enabled: Boolean,
    val duration: Long
)

data class NotificationOptions(
    val priority: NotificationPriority,
    val icon: Uri,
    val color: Int,
    val vibrate: Boolean,
    val lights: Boolean,
    val sound: Uri?,
    val image: String? // Uri is resolved at display time for this.
)

enum class NotificationPriority {
    Min,
    Low,
    Default,
    High,
    Max
}

data class LocationArea(
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val dwell: Long
)