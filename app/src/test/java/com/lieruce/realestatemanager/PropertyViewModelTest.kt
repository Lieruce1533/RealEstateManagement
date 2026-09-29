package com.lieruce.realestatemanager

import com.lieruce.realestatemanager.data.LocationRepository
import com.lieruce.realestatemanager.data.PropertyRepository
import com.lieruce.realestatemanager.data.model.*
import com.lieruce.realestatemanager.ui.viewmodel.PropertyViewModel
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

/**
 * Local JVM unit tests for PropertyViewModel search and filter logic.
 */
class PropertyViewModelTest {

    @Test
    fun testFilterByTypeAndPrice() {
        val fakeDao = FakePropertyDao()
        val repository = PropertyRepository(fakeDao)
        val locationRepo = LocationRepository()
        val viewModel = PropertyViewModel(repository, locationRepo)

        val prop1 = RealEstateItem(
            id = 1L,
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
            id = 2L,
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

        val relations = listOf(
            PropertyWithRelations(property = prop1, agent = null, pictures = listOf(PropertyPicture(propertyId = 1L, uri = "test.jpg")), amenities = emptyList(), pois = emptyList()),
            PropertyWithRelations(property = prop2, agent = null, pictures = listOf(PropertyPicture(propertyId = 2L, uri = "test2.jpg"), PropertyPicture(propertyId = 2L, uri = "test3.jpg")), amenities = emptyList(), pois = emptyList())
        )

        // Test filtering by Type "Penthouse"
        viewModel.searchType = "Penthouse"
        val filteredByType = viewModel.getFilteredProperties(relations)
        assertEquals(1, filteredByType.size)
        assertEquals("Penthouse", filteredByType[0].property.type)

        // Reset and test filtering by Max Price "1000000"
        viewModel.clearFilters()
        viewModel.searchMaxPrice = "1000000"
        val filteredByPrice = viewModel.getFilteredProperties(relations)
        assertEquals(1, filteredByPrice.size)
        assertEquals("Apartment", filteredByPrice[0].property.type)

        // Test filtering by Minimum Pictures >= 2
        viewModel.clearFilters()
        viewModel.searchMinPictures = "2"
        val filteredByPics = viewModel.getFilteredProperties(relations)
        assertEquals(1, filteredByPics.size)
        assertEquals("Apartment", filteredByPics[0].property.type)
    }
}
