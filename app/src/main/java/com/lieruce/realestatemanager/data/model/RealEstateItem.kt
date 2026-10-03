package com.lieruce.realestatemanager.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Room Entity representing a real estate property table in SQLite.
 * Uses java.time.Instant for modern timestamps, agentId as a foreign key,
 * and @Embedded PropertyLocation to group address and GPS coordinates cleanly.
 */
@Entity(tableName = "real_estate_items")
data class RealEstateItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val priceInDollars: Int,
    val surfaceInSqm: Int,
    val numberOfRooms: Int,
    val description: String,
    @Embedded val location: PropertyLocation, // Grouped address, latitude, and longitude
    val status: PropertyStatus = PropertyStatus.AVAILABLE,
    val entryDate: Instant,
    val saleDate: Instant? = null,
    val agentId: Long // Foreign Key referencing Agent.id
)

/**
 * Enum representing whether a real estate property is available for purchase or has been sold.
 */
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
