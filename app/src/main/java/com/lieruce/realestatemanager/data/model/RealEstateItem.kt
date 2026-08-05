package com.lieruce.realestatemanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "real_estate_items")
data class RealEstateItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val priceInDollars: Int,
    val surfaceInSqm: Int,
    val numberOfRooms: Int,
    val description: String,
    val address: String,
    val pointsOfInterest: String, // Comma separated interests
    val status: PropertyStatus = PropertyStatus.AVAILABLE,
    val entryDate: Long, // Using Long for timestamp for simplicity with Room
    val saleDate: Long? = null,
    val agentName: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)

enum class PropertyStatus {
    AVAILABLE, SOLD
}
