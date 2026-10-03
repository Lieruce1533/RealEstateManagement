package com.lieruce.realestatemanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing a normalized property amenity (e.g. Pool, Gym, Garage).
 */
@Entity(tableName = "amenities")
data class Amenity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)
