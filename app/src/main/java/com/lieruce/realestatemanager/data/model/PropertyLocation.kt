package com.lieruce.realestatemanager.data.model

/**
 * Embedded data class representing physical location details for a real estate property.
 * Room's @Embedded annotation flattens these fields into the parent table columns in SQLite,
 * keeping the database schema clean and backward-compatible while organizing our Kotlin code.
 */
data class PropertyLocation(
    val address: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)
