package com.example.sumit.utils

import androidx.room.TypeConverter
import java.util.Date

/**
 * Contains conversion method used by Room.
 */
class Converters {
    /**
     * Converts the number of millisecond since the Epoch to a [Date] object.
     */
    @TypeConverter
    fun fromTimestamp(value: Long): Date = Date(value)

    /**
     * Converts a [Date] object to the number of milliseconds since the Epoch.
     */
    @TypeConverter
    fun dateToTimestamp(date: Date): Long = date.time
}