package com.lieruce.realestatemanager.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * Room Data Class representing a property bundled with its Agent, Pictures, Amenities, and POIs
 * using relational mapping (@Relation and @Junction).
 */
data class PropertyWithRelations(
    @Embedded val property: RealEstateItem,
    
    @Relation(
        parentColumn = "agentId",
        entityColumn = "id"
    )
    val agent: Agent?,

    @Relation(
        parentColumn = "id",
        entityColumn = "propertyId"
    )
    val pictures: List<PropertyPicture>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = PropertyAmenityCrossRef::class,
            parentColumn = "propertyId",
            entityColumn = "amenityId"
        )
    )
    val amenities: List<Amenity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = PropertyPoiCrossRef::class,
            parentColumn = "propertyId",
            entityColumn = "poiId"
        )
    )
    val pois: List<Poi>
)
