package com.lieruce.realestatemanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing a normalized Point of Interest / nearby location (e.g. Central Park, Subway).
 */
@Entity(tableName = "pois")
data class Poi(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)
