package com.lieruce.realestatemanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lieruce.realestatemanager.data.dao.PropertyDao
import com.lieruce.realestatemanager.data.dataset.AgentDataSet
import com.lieruce.realestatemanager.data.dataset.AmenityDataSet
import com.lieruce.realestatemanager.data.dataset.PoiDataSet
import com.lieruce.realestatemanager.data.dataset.PropertyDataSet
import com.lieruce.realestatemanager.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Main Room Database configuration class.
 * Declares all database entities, versioning, converters, and automatic database seeding
 * using clean, decoupled datasets from the dataset package.
 */
@Database(
    entities = [
        RealEstateItem::class,
        PropertyPicture::class,
        Agent::class,
        Amenity::class,
        Poi::class,
        PropertyAmenityCrossRef::class,
        PropertyPoiCrossRef::class
    ],
    version = 4,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun propertyDao(): PropertyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns the singleton instance of the Room database.
         * Automatically seeds normalized agents, amenities, POIs, and properties upon initial database creation.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "real_estate_database"
                )
                .fallbackToDestructiveMigration() // Recreate DB on schema version change during development
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed sample data in the background when the database is created for the first time
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { populateNormalizedInitialData(it) }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Populates the database with normalized Agents, Amenities, POIs, and 15 New York properties
         * sourced cleanly from dedicated dataset classes.
         */
        private suspend fun populateNormalizedInitialData(database: AppDatabase) {
            val dao = database.propertyDao()

            // 1. Insert normalized Agents from AgentDataSet
            var agentSmithId = 1L
            var agentJaneId = 2L
            var agentDupontId = 3L

            for (agent in AgentDataSet.getInitialAgents()) {
                val insertedId = dao.insertAgent(agent)
                when (agent.name) {
                    "Agent Smith" -> agentSmithId = insertedId
                    "Agent Jane" -> agentJaneId = insertedId
                    "Agent Dupont" -> agentDupontId = insertedId
                }
            }

            // 2. Insert standardized Amenities from AmenityDataSet into cache mapping
            val amenityCache = mutableMapOf<String, Long>()
            for (amenity in AmenityDataSet.getInitialAmenities()) {
                val insertedId = dao.insertAmenity(amenity)
                amenityCache[amenity.name] = insertedId
            }

            // 3. Insert standardized POIs from PoiDataSet into cache mapping
            val poiCache = mutableMapOf<String, Long>()
            for (poi in PoiDataSet.getInitialPois()) {
                val insertedId = dao.insertPoi(poi)
                poiCache[poi.name] = insertedId
            }

            // 4. Insert Properties, pictures, and establish normalized cross-references from PropertyDataSet
            for (record in PropertyDataSet.getInitialProperties(agentSmithId, agentJaneId, agentDupontId)) {
                val propertyId = dao.insertProperty(record.property)
                
                // Insert associated pictures
                val picturesWithId = record.pictures.map { it.copy(propertyId = propertyId) }
                dao.insertPictures(picturesWithId)

                // Link normalized amenities via cross-ref table
                for (amenityName in record.amenityNames) {
                    amenityCache[amenityName]?.let { amenityId ->
                        dao.insertAmenityCrossRef(PropertyAmenityCrossRef(propertyId = propertyId, amenityId = amenityId))
                    }
                }

                // Link normalized POIs via cross-ref table
                for (poiName in record.poiNames) {
                    poiCache[poiName]?.let { poiId ->
                        dao.insertPoiCrossRef(PropertyPoiCrossRef(propertyId = propertyId, poiId = poiId))
                    }
                }
            }
        }
    }
}
