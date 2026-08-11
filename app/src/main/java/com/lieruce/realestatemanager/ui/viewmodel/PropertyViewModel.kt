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
 * ViewModel acts as a bridge between the Data Layer (Repository) and the UI (Compose).
 * It uses 'viewModelScope' to run background tasks like saving to the database.
 */
class PropertyViewModel(private val repository: PropertyRepository) : ViewModel() {

    // 'allProperties' is a StateFlow. It's like a LiveData but more modern.
    // The UI will "subscribe" to this flow and update whenever the database changes.
    val allProperties: StateFlow<List<PropertyWithPictures>> = repository.allProperties
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Fetches a specific property by its ID.
     * We return a Flow so the UI stays updated if the property is edited.
     */
    fun getProperty(id: Long) = repository.getPropertyById(id)

    /**
     * Kotlin 101: Coroutines (viewModelScope.launch)
     * Database operations are slow and must not happen on the UI thread.
     * 'launch' starts a "Coroutines" (a tiny background thread) to do the work.
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
                status = PropertyStatus.AVAILABLE,
                entryDate = System.currentTimeMillis(),
                agentName = "Agent Smith"
            )
            repository.insertProperty(testProperty, emptyList())
        }
    }
}

/**
 * Since ViewModel constructors can't take parameters by default, 
 * we use this Factory to "inject" the repository.
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
