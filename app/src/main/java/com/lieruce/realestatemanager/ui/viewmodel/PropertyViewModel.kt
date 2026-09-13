package com.lieruce.realestatemanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lieruce.realestatemanager.data.PropertyRepository
import com.lieruce.realestatemanager.data.model.PropertyPicture
import com.lieruce.realestatemanager.data.model.PropertyStatus
import com.lieruce.realestatemanager.data.model.PropertyWithPictures
import com.lieruce.realestatemanager.data.model.RealEstateItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * PropertyViewModel acts as the bridge between the Data Layer (Repository) and the UI (Compose).
 * It survives configuration changes (like screen rotation) and runs background operations
 * safely using coroutines (viewModelScope).
 */
class PropertyViewModel(private val repository: PropertyRepository) : ViewModel() {

    /**
     * allProperties is a StateFlow that emits a live list of properties with their pictures.
     * We convert the Repository's Flow into a StateFlow using stateIn so that Compose 
     * can collect it efficiently via collectAsStateWithLifecycle().
     */
    val allProperties: StateFlow<List<PropertyWithPictures>> = repository.allProperties
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Fetches a specific property and its pictures by ID.
     * Returns a Flow so the detail screen updates reactively if the property is modified.
     */
    fun getProperty(id: Long) = repository.getPropertyById(id)

    /**
     * Saves a property (either inserting a new one or updating an existing one).
     * Uses viewModelScope.launch to run the database query on a background thread.
     */
    fun saveProperty(
        property: RealEstateItem,
        pictures: List<PropertyPicture>
    ) {
        viewModelScope.launch {
            if (property.id == 0L) {
                repository.insertProperty(property, pictures)
            } else {
                repository.updateProperty(property, pictures)
            }
        }
    }

    /**
     * Adds a sample test property to the database to help verify UI layout and map integration.
     * Uses a coroutine (viewModelScope.launch) to perform the database insertion off the main thread.
     */
    fun addTestProperty() {
        viewModelScope.launch {
            val testProperty = RealEstateItem(
                type = "Manor",
                priceInDollars = 1500000,
                surfaceInSqm = 450,
                numberOfRooms = 12,
                description = "A beautiful historic manor in the countryside.",
                address = "123 Castle Road, Loire Valley",
                pointsOfInterest = "Park, School",
                amenities = "Swimming Pool, Gym, Wine Cellar",
                status = PropertyStatus.AVAILABLE,
                entryDate = System.currentTimeMillis(),
                agentName = "Agent Smith",
                latitude = 47.4116,
                longitude = 0.9850 // Amboise, France
            )
            repository.insertProperty(testProperty, emptyList())
        }
    }
}

/**
 * ViewModelProvider.Factory is required because our PropertyViewModel takes a repository parameter.
 * This factory creates instances of PropertyViewModel and injects the repository dependency.
 */
class PropertyViewModelFactory(private val repository: PropertyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PropertyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PropertyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
