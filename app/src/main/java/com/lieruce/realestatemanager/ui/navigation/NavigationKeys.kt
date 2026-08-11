package com.lieruce.realestatemanager.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavKey {
    @Serializable
    data object PropertyList : NavKey
    
    @Serializable
    data class PropertyDetail(val propertyId: Long) : NavKey
    
    @Serializable
    data object PropertyMap : NavKey
    
    @Serializable
    data class AddEditProperty(val propertyId: Long? = null) : NavKey
    
    @Serializable
    data object Search : NavKey
}
