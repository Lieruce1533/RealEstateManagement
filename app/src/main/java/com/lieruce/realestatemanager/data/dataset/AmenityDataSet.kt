package com.lieruce.realestatemanager.data.dataset

import com.lieruce.realestatemanager.data.model.Amenity
import com.lieruce.realestatemanager.data.model.PropertyConstants

/**
 * DataSet providing standardized amenities for database seeding matching PropertyConstants.AVAILABLE_AMENITIES.
 */
object AmenityDataSet {
    fun getInitialAmenities(): List<Amenity> {
        return PropertyConstants.AVAILABLE_AMENITIES.mapIndexed { index, name ->
            Amenity(id = (index + 1).toLong(), name = name)
        }
    }
}
