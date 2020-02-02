package us.jamesmorrisstudios.rrm2.alarm

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import us.jamesmorrisstudios.rrm2.util.Guid

/**
 * Alarm Item
 */
@Entity(tableName = "alarm")
internal data class AlarmItem(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val id: Int = 0,
    @ColumnInfo(name = "guid", index = true) val guid: Guid,
    @ColumnInfo(name = "time") val time: Long
)