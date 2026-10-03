package com.lieruce.realestatemanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing a real estate agent managing properties.
 */
@Entity(tableName = "agents")
data class Agent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val phone: String,
    val photoUri: String? = null
)
