package com.lieruce.realestatemanager.data

import androidx.room.TypeConverter
import com.lieruce.realestatemanager.data.model.PropertyStatus
import java.time.Instant

/**
 * Room TypeConverters to convert complex types (like PropertyStatus and java.time.Instant)
 * into primitive SQLite types (String and Long) so Room can store them in the database.
 */
class Converters {
    
    /**
     * Converts PropertyStatus enum to String for database storage.
     */
    @TypeConverter
    fun fromStatus(status: PropertyStatus): String {
        return status.name
    }

    /**
     * Converts String from database back to PropertyStatus enum.
     */
    @TypeConverter
    fun toStatus(value: String): PropertyStatus {
        return PropertyStatus.valueOf(value)
    }

    /**
     * Converts epoch milliseconds (Long) from database into a java.time.Instant object.
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    /**
     * Converts a java.time.Instant object into epoch milliseconds (Long) for database storage.
     */
    @TypeConverter
    fun dateToTimestamp(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }
}
