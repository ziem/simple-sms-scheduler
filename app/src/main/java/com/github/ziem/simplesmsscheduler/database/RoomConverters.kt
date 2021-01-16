package com.github.ziem.simplesmsscheduler.database

import androidx.room.TypeConverter
import com.github.ziem.simplesmsscheduler.model.State
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

object RoomConverters {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    @JvmStatic
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let {
            return formatter.parse(value, OffsetDateTime::from)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromOffsetDateTime(date: OffsetDateTime?): String? {
        return date?.format(formatter)
    }

    @TypeConverter
    @JvmStatic
    fun toState(value: Int?): State? {
        return value?.let {
            return State.fromInt(it)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromState(state: State?): Int? {
        return state?.let {
            return State.toInt(state)
        }
    }
}
