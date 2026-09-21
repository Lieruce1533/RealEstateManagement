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

    // Live stream of all registered real estate agents
    val allAgents = propertyDao.getAllAgents()

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
        val picturesWithId = pictures.map { it.copy(propertyId = propertyId, id = 0L) }
        propertyDao.insertPictures(picturesWithId)
    }

    /**
     * Updates an existing property item, clears outdated pictures, and inserts the updated picture list.
     */
    suspend fun updateProperty(property: RealEstateItem, newPictures: List<PropertyPicture>) {
        propertyDao.updateProperty(property)
        // Clear old pictures associated with this property to prevent duplication before inserting updated ones
        propertyDao.deletePicturesForProperty(property.id)
        val picturesWithId = newPictures.map { it.copy(propertyId = property.id, id = 0L) }
        propertyDao.insertPictures(picturesWithId)
    }
}
