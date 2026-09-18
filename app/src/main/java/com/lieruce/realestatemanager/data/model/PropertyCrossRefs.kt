package com.lieruce.realestatemanager.data.model

import androidx.room.Entity

/**
 * Junction table for Many-to-Many relationship between RealEstateItem and Amenity.
 */
@Entity(tableName = "property_amenities", primaryKeys = ["propertyId", "amenityId"])
data class PropertyAmenityCrossRef(
    val propertyId: Long,
    val amenityId: Long
)

/**
 * Junction table for Many-to-Many relationship between RealEstateItem and Poi.
 */
@Entity(tableName = "property_pois", primaryKeys = ["propertyId", "poiId"])
data class PropertyPoiCrossRef(
    val propertyId: Long,
    val poiId: Long
)
