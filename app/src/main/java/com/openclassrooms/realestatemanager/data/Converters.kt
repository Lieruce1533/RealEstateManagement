package com.openclassrooms.realestatemanager.data

import androidx.room.TypeConverter
import com.openclassrooms.realestatemanager.data.model.PropertyStatus

class Converters {
    @TypeConverter
    fun fromStatus(status: PropertyStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(value: String): PropertyStatus {
        return PropertyStatus.valueOf(value)
    }
}
