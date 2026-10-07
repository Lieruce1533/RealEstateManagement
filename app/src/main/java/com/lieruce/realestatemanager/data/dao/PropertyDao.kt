package com.lieruce.realestatemanager.data.dao

import androidx.room.*
import android.database.Cursor
import com.lieruce.realestatemanager.data.model.*
import kotlinx.coroutines.flow.Flow
import java.time.Instant

/**
 * Data Access Object (DAO) providing structured SQL operations for properties,
 * agents, amenities, points of interest, and relational cross-references.
 */
@Dao
interface PropertyDao {

    @Transaction
    @Query("SELECT * FROM real_estate_items")
    fun getAllProperties(): Flow<List<PropertyWithRelations>>

    /**
     * Retrieves a live stream of all available real estate properties.
     */
    @Transaction
    @Query("SELECT * FROM real_estate_items WHERE status = 'AVAILABLE'")
    fun getAvailableProperties(): Flow<List<PropertyWithRelations>>

    /**
     * Executes a comprehensive multi-criteria SQL query filtering properties by type, status, agent,
     * price range, surface area range, address location query, creation date cutoff, required amenities, and required POIs.
     */
    @Transaction
    @Query("""
        SELECT * FROM real_estate_items 
        WHERE (:type IS NULL OR type = :type)
          AND (:status IS NULL OR status = :status)
          AND (:agentId IS NULL OR agentId = :agentId)
          AND (:minPrice IS NULL OR priceInDollars >= :minPrice)
          AND (:maxPrice IS NULL OR priceInDollars <= :maxPrice)
          AND (:minSurface IS NULL OR surfaceInSqm >= :minSurface)
          AND (:maxSurface IS NULL OR surfaceInSqm <= :maxSurface)
          AND (:areaQuery IS NULL OR LOWER(address) LIKE '%' || LOWER(:areaQuery) || '%')
          AND (:minEntryDate IS NULL OR entryDate >= :minEntryDate)
          AND (:amenityCount = 0 OR id IN (
              SELECT propertyId FROM property_amenities 
              INNER JOIN amenities ON property_amenities.amenityId = amenities.id 
              WHERE amenities.name IN (:amenityNames) 
              GROUP BY propertyId 
              HAVING COUNT(DISTINCT amenities.name) = :amenityCount
          ))
          AND (:poiCount = 0 OR id IN (
              SELECT propertyId FROM property_pois 
              INNER JOIN pois ON property_pois.poiId = pois.id 
              WHERE pois.name IN (:poiNames) 
              GROUP BY propertyId 
              HAVING COUNT(DISTINCT pois.name) = :poiCount
          ))
    """)
    fun filterProperties(
        type: String? = null,
        status: PropertyStatus? = null,
        agentId: Long? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        minSurface: Int? = null,
        maxSurface: Int? = null,
        areaQuery: String? = null,
        minEntryDate: Instant? = null,
        amenityNames: List<String> = emptyList(),
        amenityCount: Int = amenityNames.size,
        poiNames: List<String> = emptyList(),
        poiCount: Int = poiNames.size
    ): Flow<List<PropertyWithRelations>>


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

    @Query("DELETE FROM property_amenities WHERE propertyId = :propertyId")
    suspend fun deleteAmenityCrossRefsForProperty(propertyId: Long)

    @Query("DELETE FROM property_pois WHERE propertyId = :propertyId")
    suspend fun deletePoiCrossRefsForProperty(propertyId: Long)

    @Query("SELECT id FROM amenities WHERE name = :name")
    suspend fun getAmenityIdByName(name: String): Long?

    @Query("SELECT id FROM pois WHERE name = :name")
    suspend fun getPoiIdByName(name: String): Long?

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
