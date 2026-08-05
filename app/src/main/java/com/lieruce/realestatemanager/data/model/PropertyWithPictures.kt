package com.lieruce.realestatemanager.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class PropertyWithPictures(
    @Embedded val property: RealEstateItem,
    @Relation(
        parentColumn = "id",
        entityColumn = "propertyId"
    )
    val pictures: List<PropertyPicture>
)
