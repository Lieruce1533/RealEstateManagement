package com.lieruce.realestatemanager.data.dao

import androidx.room.*
import android.database.Cursor
import com.lieruce.realestatemanager.data.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) providing structured SQL operations for properties,
 * agents, amenities, points of interest, and relational cross-references.
 */
@Dao
interface PropertyDao {

    @Transaction
    @Query("SELECT * FROM real_estate_items")
    fun getAllProperties(): Flow<List<PropertyWithRelations>>

    @Transaction
    @Query("SELECT * FROM real_estate_items WHERE id = :propertyId")
    fun getPropertyById(propertyId: Long): Flow<PropertyWithRelations?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: RealEstateItem): Long

    @Update
    suspend fun updateProperty(property: RealEstateItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPictures(pictures: List<PropertyPicture>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgent(agent: Agent): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAmenity(amenity: Amenity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoi(poi: Poi): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAmenityCrossRef(crossRef: PropertyAmenityCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoiCrossRef(crossRef: PropertyPoiCrossRef)

    @Delete
    suspend fun deletePicture(picture: PropertyPicture)

    /**
     * Deletes all picture records associated with a specific property ID (used during updates).
     */
    @Query("DELETE FROM property_pictures WHERE propertyId = :propertyId")
    suspend fun deletePicturesForProperty(propertyId: Long)

    @Transaction
    @Query("SELECT * FROM real_estate_items")
    fun getAllPropertiesCursor(): Cursor

    @Transaction
    @Query("SELECT * FROM real_estate_items WHERE id = :propertyId")
    fun getPropertyByIdCursor(propertyId: Long): Cursor

    /**
     * Retrieves all real estate agents from the database for filtering purposes.
     */
    @Query("SELECT * FROM agents")
    fun getAllAgents(): Flow<List<Agent>>
}
