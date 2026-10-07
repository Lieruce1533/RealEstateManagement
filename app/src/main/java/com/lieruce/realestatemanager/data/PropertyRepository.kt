package com.lieruce.realestatemanager.data

import com.lieruce.realestatemanager.data.dao.PropertyDao
import com.lieruce.realestatemanager.data.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository acting as a single source of truth for real estate data,
 * bridging the DAO and ViewModel using normalized relations (PropertyWithRelations).
 */
class PropertyRepository(private val propertyDao: PropertyDao) {

    // Live stream of all properties with their full relations (Agent, Pictures, Amenities, POIs)
    val allProperties: Flow<List<PropertyWithRelations>> = propertyDao.getAllProperties()
    val allAvailableProperties: Flow<List<PropertyWithRelations>> = propertyDao.getAvailableProperties()

    // Live stream of all registered real estate agents
    val allAgents = propertyDao.getAllAgents()

    /**
     * Queries Room database using multi-criteria SQL constraints.
     */
    fun filterProperties(
        type: String? = null,
        status: PropertyStatus? = null,
        agentId: Long? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        minSurface: Int? = null,
        maxSurface: Int? = null,
        areaQuery: String? = null,
        minEntryDate: java.time.Instant? = null,
        amenityNames: List<String> = emptyList(),
        poiNames: List<String> = emptyList()
    ): Flow<List<PropertyWithRelations>> {
        return propertyDao.filterProperties(
            type = type,
            status = status,
            agentId = agentId,
            minPrice = minPrice,
            maxPrice = maxPrice,
            minSurface = minSurface,
            maxSurface = maxSurface,
            areaQuery = areaQuery,
            minEntryDate = minEntryDate,
            amenityNames = amenityNames,
            poiNames = poiNames
        )
    }

    /**
     * Fetches a single property and its relations by ID.
     */
    fun getPropertyById(id: Long): Flow<PropertyWithRelations?> {
        return propertyDao.getPropertyById(id)
    }

    /**
     * Inserts a new property item and its associated pictures into the database (backward compatible).
     */
    suspend fun insertProperty(property: RealEstateItem, pictures: List<PropertyPicture>) {
        insertProperty(property, pictures, emptyList(), emptyList())
    }

    /**
     * Inserts a new property item, its associated pictures, amenities, and points of interest into the database.
     */
    suspend fun insertProperty(
        property: RealEstateItem,
        pictures: List<PropertyPicture>,
        amenityNames: List<String>,
        poiNames: List<String>
    ) {
        val propertyId = propertyDao.insertProperty(property)
        
        // Insert pictures
        val picturesWithId = pictures.map { it.copy(propertyId = propertyId, id = 0L) }
        propertyDao.insertPictures(picturesWithId)

        // Link amenities
        for (amenityName in amenityNames) {
            var amenityId = propertyDao.getAmenityIdByName(amenityName)
            if (amenityId == null) {
                amenityId = propertyDao.insertAmenity(Amenity(name = amenityName))
            }
            propertyDao.insertAmenityCrossRef(PropertyAmenityCrossRef(propertyId = propertyId, amenityId = amenityId))
        }

        // Link POIs
        for (poiName in poiNames) {
            var poiId = propertyDao.getPoiIdByName(poiName)
            if (poiId == null) {
                poiId = propertyDao.insertPoi(Poi(name = poiName))
            }
            propertyDao.insertPoiCrossRef(PropertyPoiCrossRef(propertyId = propertyId, poiId = poiId))
        }
    }

    /**
     * Updates an existing property item, clears outdated pictures, amenities, and POIs,
     * and inserts the updated relational lists.
     */
    suspend fun updateProperty(
        property: RealEstateItem,
        newPictures: List<PropertyPicture>,
        amenityNames: List<String>,
        poiNames: List<String>
    ) {
        propertyDao.updateProperty(property)
        
        // Clear old pictures and cross-references associated with this property
        propertyDao.deletePicturesForProperty(property.id)
        propertyDao.deleteAmenityCrossRefsForProperty(property.id)
        propertyDao.deletePoiCrossRefsForProperty(property.id)

        // Insert updated pictures
        val picturesWithId = newPictures.map { it.copy(propertyId = property.id, id = 0L) }
        propertyDao.insertPictures(picturesWithId)

        // Insert updated amenities cross-refs
        for (amenityName in amenityNames) {
            var amenityId = propertyDao.getAmenityIdByName(amenityName)
            if (amenityId == null) {
                amenityId = propertyDao.insertAmenity(Amenity(name = amenityName))
            }
            propertyDao.insertAmenityCrossRef(PropertyAmenityCrossRef(propertyId = property.id, amenityId = amenityId))
        }

        // Insert updated POIs cross-refs
        for (poiName in poiNames) {
            var poiId = propertyDao.getPoiIdByName(poiName)
            if (poiId == null) {
                poiId = propertyDao.insertPoi(Poi(name = poiName))
            }
            propertyDao.insertPoiCrossRef(PropertyPoiCrossRef(propertyId = property.id, poiId = poiId))
        }
    }
}
