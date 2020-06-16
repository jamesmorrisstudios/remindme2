package us.jamesmorrisstudios.rrm2.storage.history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * History Item
 */
@Entity(tableName = "history")
data class HistoryItem(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val id: Int = 0,
    @ColumnInfo(name = "guid", index = true) val guid: String,
    @ColumnInfo(name = "time") val time: Long,
    @ColumnInfo(name = "delay") val delay: Long,
    @ColumnInfo(name = "action") val action: HistoryItemAction
)

/**
 * History Item Action
 */
enum class HistoryItemAction {
    /**
     * Item shown.
     */
    Show,

    /**
     * Item Reshown.
     */
    ReShow,

    /**
     * Item clicked.
     */
    Click,

    /**
     * Item dismissed.
     */
    Dismiss,

    /**
     * Item complete.
     */
    Complete,

    /**
     * Item incomplete.
     */
    Incomplete,

    /**
     * Item snooze.
     */
    Snooze,

    /**
     * Item cancelled.
     */
    Cancelled,

    /**
     * Item replaced.
     */
    Replaced
}