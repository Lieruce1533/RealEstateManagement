package com.lieruce.realestatemanager.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lieruce.realestatemanager.Utils
import com.lieruce.realestatemanager.data.LocationRepository
import com.lieruce.realestatemanager.data.PropertyRepository
import com.lieruce.realestatemanager.data.model.Agent
import com.lieruce.realestatemanager.data.model.PropertyLocation
import com.lieruce.realestatemanager.data.model.PropertyPicture
import com.lieruce.realestatemanager.data.model.PropertyStatus
import com.lieruce.realestatemanager.data.model.PropertyWithRelations
import com.lieruce.realestatemanager.data.model.RealEstateItem
import com.lieruce.realestatemanager.ui.navigation.NavKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.Instant
import java.util.Locale

/**
 * Enum representing date range filter options for properties.
 */
enum class DateFilterOption(val label: String) {
    ANY("Any Time"),
    PAST_2_WEEKS("Past 2 Weeks"),
    PAST_MONTH("Past Month"),
    PAST_3_MONTHS("Past 3 Months")
}

/**
 * Data container holding active applied search filter parameters for Room SQL query execution.
 */
data class SearchFilterParams(
    val type: String? = null,
    val status: PropertyStatus? = null,
    val agentId: Long? = null,
    val minPrice: Int? = null,
    val maxPrice: Int? = null,
    val minSurface: Int? = null,
    val maxSurface: Int? = null,
    val areaQuery: String? = null,
    val minEntryDate: Instant? = null,
    val amenityNames: List<String> = emptyList(),
    val poiNames: List<String> = emptyList()
)

/**
 * PropertyViewModel acts as the bridge between the Data Layer (Repository) and the UI (Compose).
 * It manages property data streams, save operations, test seeding, search/filter states, and geocoding.
 */
class PropertyViewModel(
    private val repository: PropertyRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    /**
     * Navigation backstack stored in the ViewModel so that screen navigation state
     * survives configuration changes (such as device rotation between phone and tablet modes).
     */
    val backStack = mutableStateListOf<Any>(NavKey.PropertyList)

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
     * allAvailableProperties is a StateFlow emitting only available real estate properties.
     */
    val allAvailableProperties: StateFlow<List<PropertyWithRelations>> = repository.allAvailableProperties
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
    // PRESENTATION SETTINGS STATES
    // ============================================================================
    var isMockGpsEnabled by mutableStateOf(false)
    var isEuroCurrency by mutableStateOf(false)

    /**
     * Formats property price into Dollars ($) or Euros (€) using Utils conversion rates.
     */
    fun formatPrice(priceInDollars: Int): String {
        return if (isEuroCurrency) {
            val euros = Utils.convertDollarToEuro(priceInDollars)
            "€${NumberFormat.getNumberInstance(Locale.US).format(euros)}"
        } else {
            NumberFormat.getCurrencyInstance(Locale.US).format(priceInDollars)
        }
    }

    // ============================================================================
    // SEARCH & FILTER DRAFT STATES & SQL SEARCH RESULTS
    // ============================================================================
    var searchType by mutableStateOf<String?>(null)
    var searchMinPrice by mutableStateOf("")
    var searchMaxPrice by mutableStateOf("")
    var searchMinSurface by mutableStateOf("")
    var searchMaxSurface by mutableStateOf("")
    var searchStatus by mutableStateOf<PropertyStatus?>(null)
    var searchAgentId by mutableStateOf<Long?>(null)
    var searchAreaQuery by mutableStateOf("")
    var searchDateFilter by mutableStateOf(DateFilterOption.ANY)
    var searchMinPictures by mutableStateOf("")
    val searchAmenities = mutableStateListOf<String>()
    val searchPois = mutableStateListOf<String>()

    // Active applied search filter parameters
    private val _appliedFilterParams = MutableStateFlow(SearchFilterParams())

    /**
     * Reactive stream of filtered properties executed directly via Room DAO SQL query.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<PropertyWithRelations>> = _appliedFilterParams
        .flatMapLatest { params ->
            repository.filterProperties(
                type = params.type,
                status = params.status,
                agentId = params.agentId,
                minPrice = params.minPrice,
                maxPrice = params.maxPrice,
                minSurface = params.minSurface,
                maxSurface = params.maxSurface,
                areaQuery = params.areaQuery?.takeIf { it.isNotBlank() },
                minEntryDate = params.minEntryDate,
                amenityNames = params.amenityNames,
                poiNames = params.poiNames
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Applies current draft search filters and executes the Room SQL query.
     */
    fun applySearchFilters() {
        val now = Instant.now()
        val cutoffInstant = when (searchDateFilter) {
            DateFilterOption.ANY -> null
            DateFilterOption.PAST_2_WEEKS -> now.minusSeconds(86400L * 14)
            DateFilterOption.PAST_MONTH -> now.minusSeconds(86400L * 30)
            DateFilterOption.PAST_3_MONTHS -> now.minusSeconds(86400L * 90)
        }

        _appliedFilterParams.value = SearchFilterParams(
            type = searchType,
            status = searchStatus,
            agentId = searchAgentId,
            minPrice = searchMinPrice.toIntOrNull(),
            maxPrice = searchMaxPrice.toIntOrNull(),
            minSurface = searchMinSurface.toIntOrNull(),
            maxSurface = searchMaxSurface.toIntOrNull(),
            areaQuery = searchAreaQuery,
            minEntryDate = cutoffInstant,
            amenityNames = searchAmenities.toList(),
            poiNames = searchPois.toList()
        )
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
        searchAreaQuery = ""
        searchDateFilter = DateFilterOption.ANY
        searchMinPictures = ""
        searchAmenities.clear()
        searchPois.clear()
        _appliedFilterParams.value = SearchFilterParams()
    }

    /**
     * Fetches a specific property and its relations by ID.
     */
    fun getProperty(id: Long) = repository.getPropertyById(id)

    /**
     * Saves a property (either inserting a new one or updating an existing one).
     * Automatically geocodes the address into GPS coordinates if latitude or longitude is missing,
     * and persists associated pictures, amenities, and POIs.
     */
    fun saveProperty(
        property: RealEstateItem,
        pictures: List<PropertyPicture>,
        amenityNames: List<String>,
        poiNames: List<String>
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
                repository.insertProperty(propertyToSave, pictures, amenityNames, poiNames)
            } else {
                repository.updateProperty(propertyToSave, pictures, amenityNames, poiNames)
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
