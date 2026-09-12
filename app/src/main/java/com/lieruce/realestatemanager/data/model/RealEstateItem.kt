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
    val amenities: String = "", // Comma-separated property amenities (e.g., Pool, Gym, Garage)
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

/**
 * Standardized constant values for property types, amenities, and points of interest.
 * Using predefined options prevents free-form user typos, ensuring reliable filtering
 * and search functionality across the app.
 */
object PropertyConstants {
    val PROPERTY_TYPES = listOf("House", "Apartment", "Penthouse", "Manor", "Duplex", "Flat", "Studio")
    val AVAILABLE_AMENITIES = listOf("Pool", "Balcony", "Garage", "Garden", "Air Conditioning", "Fireplace", "Terrace", "Security System")
    val AVAILABLE_POIS = listOf("School", "Park", "Shopping Mall", "Restaurant", "Hospital", "Library", "Public Transportation")
}
