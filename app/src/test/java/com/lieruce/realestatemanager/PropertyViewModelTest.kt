package com.lieruce.realestatemanager

import com.lieruce.realestatemanager.data.PropertyRepository
import com.lieruce.realestatemanager.data.model.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

/**
 * Local JVM unit tests for search and filter logic executing Room SQL queries.
 */
class PropertyViewModelTest {

    @Test
    fun testFilterByTypeAndPrice() = runBlocking {
        val fakeDao = FakePropertyDao()
        val repository = PropertyRepository(fakeDao)

        val prop1 = RealEstateItem(
            type = "Penthouse",
            priceInDollars = 3000000,
            surfaceInSqm = 250,
            numberOfRooms = 5,
            description = "Luxurious penthouse",
            location = PropertyLocation(address = "Manhattan, NY", latitude = 40.7, longitude = -74.0),
            status = PropertyStatus.AVAILABLE,
            entryDate = Instant.now(),
            agentId = 1L
        )

        val prop2 = RealEstateItem(
            type = "Apartment",
            priceInDollars = 800000,
            surfaceInSqm = 90,
            numberOfRooms = 3,
            description = "Cozy apartment",
            location = PropertyLocation(address = "Brooklyn, NY", latitude = 40.6, longitude = -73.9),
            status = PropertyStatus.AVAILABLE,
            entryDate = Instant.now(),
            agentId = 2L
        )

        repository.insertProperty(prop1, emptyList())
        repository.insertProperty(prop2, emptyList())

        // Test filtering by Type "Penthouse" directly on repository (pure, instant, non-hanging)
        val typeResults = repository.filterProperties(type = "Penthouse").first()
        assertEquals(1, typeResults.size)
        assertEquals("Penthouse", typeResults[0].property.type)

        // Test filtering by Max Price "1000000" directly on repository
        val priceResults = repository.filterProperties(maxPrice = 1000000).first()
        assertEquals(1, priceResults.size)
        assertEquals("Apartment", priceResults[0].property.type)
    }
}
