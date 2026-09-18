package com.lieruce.realestatemanager.data

import com.lieruce.realestatemanager.data.dao.PropertyDao
import com.lieruce.realestatemanager.data.model.PropertyPicture
import com.lieruce.realestatemanager.data.model.PropertyWithRelations
import com.lieruce.realestatemanager.data.model.RealEstateItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository acting as a single source of truth for real estate data,
 * bridging the DAO and ViewModel using normalized relations (PropertyWithRelations).
 */
class PropertyRepository(private val propertyDao: PropertyDao) {

    // Live stream of all properties with their full relations (Agent, Pictures, Amenities, POIs)
    val allProperties: Flow<List<PropertyWithRelations>> = propertyDao.getAllProperties()

    /**
     * Fetches a single property and its relations by ID.
     */
    fun getPropertyById(id: Long): Flow<PropertyWithRelations?> {
        return propertyDao.getPropertyById(id)
    }

    /**
     * Inserts a new property item and its associated pictures into the database.
     */
    suspend fun insertProperty(property: RealEstateItem, pictures: List<PropertyPicture>) {
        val propertyId = propertyDao.insertProperty(property)
        val picturesWithId = pictures.map { it.copy(propertyId = propertyId) }
        propertyDao.insertPictures(picturesWithId)
    }

    /**
     * Updates an existing property item and inserts any new pictures.
     */
    suspend fun updateProperty(property: RealEstateItem, newPictures: List<PropertyPicture>) {
        propertyDao.updateProperty(property)
        propertyDao.insertPictures(newPictures)
    }
}
