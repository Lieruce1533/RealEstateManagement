package com.lieruce.realestatemanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "property_pictures",
    foreignKeys = [
        ForeignKey(
            entity = RealEstateItem::class,
            parentColumns = ["id"],
            childColumns = ["propertyId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PropertyPicture(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val propertyId: Long,
    val uri: String, // URI of the image (gallery or camera)
    val description: String? = null
)
