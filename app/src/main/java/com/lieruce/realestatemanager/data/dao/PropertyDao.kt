package com.lieruce.realestatemanager.data.dao

import androidx.room.*
import android.database.Cursor
import com.lieruce.realestatemanager.data.model.PropertyPicture
import com.lieruce.realestatemanager.data.model.RealEstateItem
import com.lieruce.realestatemanager.data.model.PropertyWithPictures
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {

    @Transaction
    @Query("SELECT * FROM real_estate_items")
    fun getAllProperties(): Flow<List<PropertyWithPictures>>

    @Transaction
    @Query("SELECT * FROM real_estate_items WHERE id = :propertyId")
    fun getPropertyById(propertyId: Long): Flow<PropertyWithPictures?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: RealEstateItem): Long

    @Update
    suspend fun updateProperty(property: RealEstateItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPictures(pictures: List<PropertyPicture>)

    @Delete
    suspend fun deletePicture(picture: PropertyPicture)

    @Transaction
    @Query("SELECT * FROM real_estate_items")
    fun getAllPropertiesCursor(): Cursor

    @Transaction
    @Query("SELECT * FROM real_estate_items WHERE id = :propertyId")
    fun getPropertyByIdCursor(propertyId: Long): Cursor

    // Search/Filter query will go here later
}
