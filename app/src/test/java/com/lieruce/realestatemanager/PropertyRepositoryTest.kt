package com.lieruce.realestatemanager

import android.database.Cursor
import com.lieruce.realestatemanager.data.PropertyRepository
import com.lieruce.realestatemanager.data.dao.PropertyDao
import com.lieruce.realestatemanager.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

/**
 * Fake PropertyDao implementing PropertyDao for isolated repository testing without Android SQLite dependencies.
 */
class FakePropertyDao : PropertyDao {
    private val properties = mutableListOf<PropertyWithRelations>()
    private val agents = mutableListOf<Agent>()

    override fun getAllProperties(): Flow<List<PropertyWithRelations>> = flowOf(properties)
    override fun getAvailableProperties(): Flow<List<PropertyWithRelations>> =
        flowOf(properties.filter { it.property.status == PropertyStatus.AVAILABLE })

    override fun filterProperties(
        type: String?,
        status: PropertyStatus?,
        agentId: Long?,
        minPrice: Int?,
        maxPrice: Int?,
        minSurface: Int?,
        maxSurface: Int?,
        areaQuery: String?,
        minEntryDate: Instant?,
        amenityNames: List<String>,
        amenityCount: Int,
        poiNames: List<String>,
        poiCount: Int
    ): Flow<List<PropertyWithRelations>> {
        val filtered = properties.filter { item ->
            val prop = item.property
            if (type != null && prop.type != type) return@filter false
            if (status != null && prop.status != status) return@filter false
            if (agentId != null && prop.agentId != agentId) return@filter false
            if (minPrice != null && prop.priceInDollars < minPrice) return@filter false
            if (maxPrice != null && prop.priceInDollars > maxPrice) return@filter false
            if (minSurface != null && prop.surfaceInSqm < minSurface) return@filter false
            if (maxSurface != null && prop.surfaceInSqm > maxSurface) return@filter false
            if (!areaQuery.isNullOrBlank() && !prop.location.address.contains(areaQuery, ignoreCase = true)) return@filter false
            if (minEntryDate != null && prop.entryDate.isBefore(minEntryDate)) return@filter false
            true
        }
        return flowOf(filtered)
    }

    override fun getPropertyById(propertyId: Long): Flow<PropertyWithRelations?> = flowOf(properties.find { it.property.id == propertyId })
    
    override suspend fun insertProperty(property: RealEstateItem): Long {
        val newId = (properties.size + 1).toLong()
        val item = property.copy(id = newId)
        properties.add(PropertyWithRelations(property = item, agent = null, pictures = emptyList(), amenities = emptyList(), pois = emptyList()))
        return newId
    }

    override suspend fun updateProperty(property: RealEstateItem) {
        val index = properties.indexOfFirst { it.property.id == property.id }
        if (index != -1) {
            properties[index] = properties[index].copy(property = property)
        }
    }

    override suspend fun insertPictures(pictures: List<PropertyPicture>) {}
    override suspend fun insertAgent(agent: Agent): Long { agents.add(agent); return agents.size.toLong() }
    override suspend fun insertAmenity(amenity: Amenity): Long = 1L
    override suspend fun insertPoi(poi: Poi): Long = 1L
    override suspend fun insertAmenityCrossRef(crossRef: PropertyAmenityCrossRef) {}
    override suspend fun insertPoiCrossRef(crossRef: PropertyPoiCrossRef) {}
    override suspend fun deletePicture(picture: PropertyPicture) {}
    override suspend fun deletePicturesForProperty(propertyId: Long) {}
    override suspend fun deleteAmenityCrossRefsForProperty(propertyId: Long) {}
    override suspend fun deletePoiCrossRefsForProperty(propertyId: Long) {}
    override suspend fun getAmenityIdByName(name: String): Long? = null
    override suspend fun getPoiIdByName(name: String): Long? = null

    override fun getAllPropertiesCursor(): Cursor = throw UnsupportedOperationException()
    override fun getPropertyByIdCursor(propertyId: Long): Cursor = throw UnsupportedOperationException()
    override fun getAllAgents(): Flow<List<Agent>> = flowOf(agents)
}

/**
 * Local JVM unit tests for PropertyRepository data operations.
 */
class PropertyRepositoryTest {

    @Test
    fun testInsertAndRetrieveProperty() = runBlocking {
        val fakeDao = FakePropertyDao()
        val repository = PropertyRepository(fakeDao)

        val property = RealEstateItem(
            type = "Penthouse",
            priceInDollars = 3000000,
            surfaceInSqm = 250,
            numberOfRooms = 5,
            description = "Test penthouse",
            location = PropertyLocation(address = "Test St, NY"),
            status = PropertyStatus.AVAILABLE,
            entryDate = Instant.now(),
            agentId = 1L
        )

        repository.insertProperty(property, emptyList())

        // Verify insertion
        repository.allProperties.collect { list ->
            assertEquals(1, list.size)
            assertEquals("Penthouse", list[0].property.type)
            assertEquals(3000000, list[0].property.priceInDollars)
        }
    }
}
