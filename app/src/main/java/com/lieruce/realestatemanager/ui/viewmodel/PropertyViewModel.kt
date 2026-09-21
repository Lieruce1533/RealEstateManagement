package com.lieruce.realestatemanager.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lieruce.realestatemanager.data.LocationRepository
import com.lieruce.realestatemanager.data.PropertyRepository
import com.lieruce.realestatemanager.data.model.Agent
import com.lieruce.realestatemanager.data.model.PropertyLocation
import com.lieruce.realestatemanager.data.model.PropertyPicture
import com.lieruce.realestatemanager.data.model.PropertyStatus
import com.lieruce.realestatemanager.data.model.PropertyWithRelations
import com.lieruce.realestatemanager.data.model.RealEstateItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant

/**
 * PropertyViewModel acts as the bridge between the Data Layer (Repository) and the UI (Compose).
 * It manages property data streams, save operations, test seeding, search/filter states, and geocoding.
 */
class PropertyViewModel(
    private val repository: PropertyRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    /**
     * allProperties is a StateFlow emitting a live list of properties with their full normalized relations.
     */
    val allProperties: StateFlow<List<PropertyWithRelations>> = repository.allProperties
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * allAgents is a StateFlow emitting all real estate agents for selection and filtering.
     */
    val allAgents: StateFlow<List<Agent>> = repository.allAgents
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ============================================================================
    // SEARCH & FILTER STATES
    // ============================================================================
    var searchType by mutableStateOf<String?>(null)
    var searchMinPrice by mutableStateOf("")
    var searchMaxPrice by mutableStateOf("")
    var searchMinSurface by mutableStateOf("")
    var searchMaxSurface by mutableStateOf("")
    var searchStatus by mutableStateOf<PropertyStatus?>(null)
    var searchAgentId by mutableStateOf<Long?>(null)
    val searchAmenities = mutableStateListOf<String>()
    val searchPois = mutableStateListOf<String>()

    /**
     * Computes and returns properties matching the current search & filter criteria.
     */
    fun getFilteredProperties(list: List<PropertyWithRelations>): List<PropertyWithRelations> {
        return list.filter { item ->
            val prop = item.property

            // Filter by Property Type
            if (searchType != null && prop.type != searchType) return@filter false

            // Filter by Agent
            if (searchAgentId != null && prop.agentId != searchAgentId) return@filter false

            // Filter by Min Price
            val minP = searchMinPrice.toIntOrNull()
            if (minP != null && prop.priceInDollars < minP) return@filter false

            // Filter by Max Price
            val maxP = searchMaxPrice.toIntOrNull()
            if (maxP != null && prop.priceInDollars > maxP) return@filter false

            // Filter by Min Surface
            val minS = searchMinSurface.toIntOrNull()
            if (minS != null && prop.surfaceInSqm < minS) return@filter false

            // Filter by Max Surface
            val maxS = searchMaxSurface.toIntOrNull()
            if (maxS != null && prop.surfaceInSqm > maxS) return@filter false

            // Filter by Status
            if (searchStatus != null && prop.status != searchStatus) return@filter false

            // Filter by Amenities (must contain all selected amenities)
            if (searchAmenities.isNotEmpty()) {
                val propertyAmenityNames = item.amenities.map { it.name }
                if (!propertyAmenityNames.containsAll(searchAmenities)) return@filter false
            }

            // Filter by Points of Interest (must contain all selected POIs)
            if (searchPois.isNotEmpty()) {
                val propertyPoiNames = item.pois.map { it.name }
                if (!propertyPoiNames.containsAll(searchPois)) return@filter false
            }

            true
        }
    }

    /**
     * Resets all search and filter criteria.
     */
    fun clearFilters() {
        searchType = null
        searchMinPrice = ""
        searchMaxPrice = ""
        searchMinSurface = ""
        searchMaxSurface = ""
        searchStatus = null
        searchAgentId = null
        searchAmenities.clear()
        searchPois.clear()
    }

    /**
     * Fetches a specific property and its relations by ID.
     */
    fun getProperty(id: Long) = repository.getPropertyById(id)

    /**
     * Saves a property (either inserting a new one or updating an existing one).
     * Automatically geocodes the address into GPS coordinates if latitude or longitude is missing.
     */
    fun saveProperty(
        property: RealEstateItem,
        pictures: List<PropertyPicture>
    ) {
        viewModelScope.launch {
            var propertyToSave = property
            // If GPS coordinates are missing, automatically geocode the address
            if (property.location.latitude == null || property.location.longitude == null) {
                val latLng = locationRepository.getLatLngFromAddress(property.location.address)
                if (latLng != null) {
                    propertyToSave = property.copy(
                        location = property.location.copy(
                            latitude = latLng.first,
                            longitude = latLng.second
                        )
                    )
                }
            }

            if (propertyToSave.id == 0L) {
                repository.insertProperty(propertyToSave, pictures)
            } else {
                repository.updateProperty(propertyToSave, pictures)
            }
        }
    }

    /**
     * Adds a sample test property to the database.
     */
    @Suppress("NewApi")
    fun addTestProperty() {
        viewModelScope.launch {
            val testProperty = RealEstateItem(
                type = "Penthouse",
                priceInDollars = 2500000,
                surfaceInSqm = 250,
                numberOfRooms = 6,
                description = "A beautiful test penthouse in New York.",
                location = PropertyLocation(
                    address = "5th Avenue, New York, NY",
                    latitude = 40.7712,
                    longitude = -73.9674
                ),
                status = PropertyStatus.AVAILABLE,
                entryDate = Instant.now(),
                agentId = 1L // Associated with Agent Smith
            )
            repository.insertProperty(testProperty, emptyList())
        }
    }
}

/**
 * ViewModelProvider.Factory is required because our PropertyViewModel takes repository parameters.
 */
class PropertyViewModelFactory(
    private val repository: PropertyRepository,
    private val locationRepository: LocationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PropertyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PropertyViewModel(repository, locationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
