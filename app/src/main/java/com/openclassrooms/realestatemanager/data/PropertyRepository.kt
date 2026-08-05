package com.openclassrooms.realestatemanager.data

import com.openclassrooms.realestatemanager.data.dao.PropertyDao
import com.openclassrooms.realestatemanager.data.model.PropertyPicture
import com.openclassrooms.realestatemanager.data.model.PropertyWithPictures
import com.openclassrooms.realestatemanager.data.model.RealEstateItem
import kotlinx.coroutines.flow.Flow

class PropertyRepository(private val propertyDao: PropertyDao) {

    val allProperties: Flow<List<PropertyWithPictures>> = propertyDao.getAllProperties()

    fun getPropertyById(id: Long): Flow<PropertyWithPictures?> {
        return propertyDao.getPropertyById(id)
    }

    suspend fun insertProperty(property: RealEstateItem, pictures: List<PropertyPicture>) {
        val propertyId = propertyDao.insertProperty(property)
        val picturesWithId = pictures.map { it.copy(propertyId = propertyId) }
        propertyDao.insertPictures(picturesWithId)
    }

    suspend fun updateProperty(property: RealEstateItem, newPictures: List<PropertyPicture>) {
        propertyDao.updateProperty(property)
        // For simplicity, we just insert new ones. 
        // A more complex logic would diff and delete removed ones.
        propertyDao.insertPictures(newPictures)
    }
}
