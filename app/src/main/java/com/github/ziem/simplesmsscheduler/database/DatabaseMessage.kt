package com.github.ziem.simplesmsscheduler.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.ziem.simplesmsscheduler.model.State
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "scheduled_message")
data class DatabaseMessage(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    @ColumnInfo(name = "date_time")
    val dateTime: OffsetDateTime,
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,
    @ColumnInfo(name = "contact_name")
    val contactName: String,
    @ColumnInfo(name = "contact_thumbnail")
    val contactThumbnail: String?,
    val message: String,
    val state: State = State.Scheduled,
    @ColumnInfo(name = "send_automatically")
    val sendAutomatically: Boolean
)

