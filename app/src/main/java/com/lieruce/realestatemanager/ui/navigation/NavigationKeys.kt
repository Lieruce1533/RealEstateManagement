package com.lieruce.realestatemanager.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Defines type-safe navigation keys (routes) for the app using Navigation 3.
 * Each object or data class represents a screen destination.
 * 
 * @Serializable acts as the modern equivalent of Parcelable, allowing objects
 * and their arguments (like propertyId) to be serialized and passed across screens safely.
 */
@Serializable
sealed interface NavKey {
    
    // Main list screen showing all real estate properties
    @Serializable
    data object PropertyList : NavKey
    
    // Detail screen for a specific property, requiring the property's unique ID
    @Serializable
    data class PropertyDetail(val propertyId: Long) : NavKey
    
    // Map view screen showing properties on a map
    @Serializable
    data object PropertyMap : NavKey
    
    // Add or edit screen. If propertyId is null, we are adding a new property;
    // if propertyId is provided, we are editing an existing property.
    @Serializable
    data class AddEditProperty(val propertyId: Long? = null) : NavKey
    
    // Search filter screen
    @Serializable
    data object Search : NavKey

    // Presentation settings screen for currency and mock GPS preferences
    @Serializable
    data object Settings : NavKey
}
