package com.lieruce.realestatemanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lieruce.realestatemanager.data.dao.PropertyDao
import com.lieruce.realestatemanager.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant

/**
 * Main Room Database configuration class.
 * Declares all database entities, versioning, converters, and automatic database seeding.
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
    version = 2,
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
         * Populates the database with normalized Agents, Amenities, POIs, and 15 New York properties.
         */
        private suspend fun populateNormalizedInitialData(database: AppDatabase) {
            val dao = database.propertyDao()

            // 1. Insert normalized Agents
            val agentSmithId = dao.insertAgent(Agent(name = "Agent Smith", email = "smith@realestate.com", phone = "+1 (555) 019-2834"))
            val agentJaneId = dao.insertAgent(Agent(name = "Agent Jane", email = "jane@realestate.com", phone = "+1 (555) 839-2041"))
            val agentDupontId = dao.insertAgent(Agent(name = "Agent Dupont", email = "dupont@realestate.com", phone = "+1 (555) 492-8102"))

            // Helper caches to avoid duplicate amenity and POI insertion
            val amenityCache = mutableMapOf<String, Long>()
            val poiCache = mutableMapOf<String, Long>()

            val rawPropertyList = listOf(
                // 1. Penthouse (Agent Smith)
                Triple(
                    RealEstateItem(
                        type = "Penthouse",
                        priceInDollars = 4500000,
                        surfaceInSqm = 350,
                        numberOfRooms = 7,
                        description = "Magnificent luxury penthouse overlooking Central Park with private elevator.",
                        address = "157 West 57th St, Manhattan, New York, NY",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = Instant.now().minusSeconds(86400L * 10),
                        agentId = agentSmithId,
                        latitude = 40.7656,
                        longitude = -73.9799
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00", description = "Living room view"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c", description = "Terrace")
                    ),
                    Pair("Swimming Pool, Terrace, Security System, Air Conditioning", "Central Park, Broadway, Carnegie Hall")
                ),
                // 2. Brownstone (Agent Jane)
                Triple(
                    RealEstateItem(
                        type = "Brownstone",
                        priceInDollars = 2800000,
                        surfaceInSqm = 300,
                        numberOfRooms = 9,
                        description = "Classic historic Harlem brownstone with original fireplaces and private garden.",
                        address = "124 West 120th St, Harlem, New York, NY",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = Instant.now().minusSeconds(86400L * 8),
                        agentId = agentJaneId,
                        latitude = 40.8065,
                        longitude = -73.9485
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1580587771525-78b9dba3b914", description = "Brownstone exterior"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c", description = "Interior fireplace")
                    ),
                    Pair("Garden, Fireplace, Garage", "Morningside Park, Columbia University, Subway")
                ),
                // 3. Loft (Agent Dupont)
                Triple(
                    RealEstateItem(
                        type = "Loft",
                        priceInDollars = 1950000,
                        surfaceInSqm = 200,
                        numberOfRooms = 5,
                        description = "Spacious Tribeca industrial loft with exposed brick and high ceilings.",
                        address = "74 Franklin St, Tribeca, New York, NY",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = Instant.now().minusSeconds(86400L * 6),
                        agentId = agentDupontId,
                        latitude = 40.7163,
                        longitude = -74.0048
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688", description = "Open space"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2", description = "Kitchen")
                    ),
                    Pair("Air Conditioning, Security System, Elevator", "Wall Street, World Trade Center, Restaurant")
                ),
                // 4. Apartment (Agent Smith)
                Triple(
                    RealEstateItem(
                        type = "Apartment",
                        priceInDollars = 1250000,
                        surfaceInSqm = 120,
                        numberOfRooms = 4,
                        description = "Elegant Upper East Side apartment steps from Fifth Avenue shopping.",
                        address = "834 Fifth Avenue, Upper East Side, New York, NY",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = Instant.now().minusSeconds(86400L * 5),
                        agentId = agentSmithId,
                        latitude = 40.7712,
                        longitude = -73.9674
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750", description = "Living room"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267", description = "Bedroom")
                    ),
                    Pair("Balcony, Elevator, Security System", "Central Park, Fifth Avenue, Museum")
                ),
                // 5. Townhouse (Agent Jane)
                Triple(
                    RealEstateItem(
                        type = "Townhouse",
                        priceInDollars = 3400000,
                        surfaceInSqm = 320,
                        numberOfRooms = 10,
                        description = "Stunning Brooklyn Heights townhouse with stunning harbor views.",
                        address = "45 Willow St, Brooklyn Heights, New York, NY",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = Instant.now().minusSeconds(86400L * 4),
                        agentId = agentJaneId,
                        latitude = 40.6970,
                        longitude = -73.9946
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9", description = "Townhouse front"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600585152220-90363fe7e115", description = "Patio")
                    ),
                    Pair("Garden, Garage, Fireplace, Terrace", "Brooklyn Bridge, DUMBO, Promenade")
                ),
                // 6. Luxury Apartment (Agent Dupont)
                Triple(
                    RealEstateItem(
                        type = "Apartment",
                        priceInDollars = 2100000,
                        surfaceInSqm = 160,
                        numberOfRooms = 5,
                        description = "Billionaires' Row ultra-luxury residence with full hotel amenities.",
                        address = "111 West 57th St, Manhattan, New York, NY",
                        status = PropertyStatus.SOLD,
                        entryDate = Instant.now().minusSeconds(86400L * 30),
                        saleDate = Instant.now().minusSeconds(86400L * 3),
                        agentId = agentDupontId,
                        latitude = 40.7648,
                        longitude = -73.9776
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00", description = "Interior view"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1512918728675-ed5a9ecdebfd", description = "Lobby")
                    ),
                    Pair("Swimming Pool, Gym, Terrace, Security System", "Carnegie Hall, Central Park, Fifth Avenue")
                ),
                // 7. Duplex (Agent Smith)
                Triple(
                    RealEstateItem(
                        type = "Duplex",
                        priceInDollars = 1650000,
                        surfaceInSqm = 150,
                        numberOfRooms = 6,
                        description = "Trendy Williamsburg duplex with double-height windows and private patio.",
                        address = "15 North 6th St, Williamsburg, Brooklyn, NY",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = Instant.now().minusSeconds(86400L * 3),
                        agentId = agentSmithId,
                        latitude = 40.7188,
                        longitude = -73.9592
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600210492486-724fe5c67fb0", description = "Living area"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600566753376-12c8ab7fb75b", description = "Kitchen")
                    ),
                    Pair("Balcony, Terrace, Air Conditioning", "Domino Park, East River, Restaurant")
                ),
                // 8. Condo (Agent Jane)
                Triple(
                    RealEstateItem(
                        type = "Apartment",
                        priceInDollars = 890000,
                        surfaceInSqm = 90,
                        numberOfRooms = 3,
                        description = "Modern Long Island City glass condo with skyline vistas.",
                        address = "42-15 Crescent St, Long Island City, NY",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = Instant.now().minusSeconds(86400L * 12),
                        agentId = agentJaneId,
                        latitude = 40.7523,
                        longitude = -73.9390
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1502005229762-cf1b4da7c5d6", description = "Condo view"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1493809842364-78817add7ffb", description = "Bedroom")
                    ),
                    Pair("Balcony, Gym, Security System", "Gantry Plaza State Park, Subway, Restaurant")
                ),
                // 9. Brownstone (Agent Dupont)
                Triple(
                    RealEstateItem(
                        type = "Brownstone",
                        priceInDollars = 2600000,
                        surfaceInSqm = 280,
                        numberOfRooms = 8,
                        description = "Park Slope historic brownstone steps from Prospect Park.",
                        address = "250 Park Slope West, Brooklyn, NY",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = Instant.now().minusSeconds(86400L * 9),
                        agentId = agentDupontId,
                        latitude = 40.6700,
                        longitude = -73.9800
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1580587771525-78b9dba3b914", description = "Facade"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c", description = "Parlor floor")
                    ),
                    Pair("Garden, Fireplace, Terrace", "Prospect Park, Library, Museum")
                ),
                // 10. Penthouse (Agent Smith)
                Triple(
                    RealEstateItem(
                        type = "Penthouse",
                        priceInDollars = 5200000,
                        surfaceInSqm = 400,
                        numberOfRooms = 8,
                        description = "Financial District crown jewel penthouse with 360-degree river views.",
                        address = "50 West St, Financial District, New York, NY",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = Instant.now().minusSeconds(86400L * 7),
                        agentId = agentSmithId,
                        latitude = 40.7081,
                        longitude = -74.0143
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00", description = "Rooftop terrace"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c", description = "Master suite")
                    ),
                    Pair("Swimming Pool, Terrace, Security System, Gym, Air Conditioning", "Battery Park, Wall Street, Subway")
                ),
                // 11. Loft (Agent Jane)
                Triple(
                    RealEstateItem(
                        type = "Loft",
                        priceInDollars = 1800000,
                        surfaceInSqm = 190,
                        numberOfRooms = 5,
                        description = "SoHo artistic loft with cast-iron columns and soaring ceilings.",
                        address = "200 Mercer St, SoHo, New York, NY",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = Instant.now().minusSeconds(86400L * 11),
                        agentId = agentJaneId,
                        latitude = 40.7250,
                        longitude = -73.9960
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688", description = "Loft space"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2", description = "Kitchen")
                    ),
                    Pair("Air Conditioning, Security System", "Washington Square Park, Shopping Mall, Restaurant")
                ),
                // 12. Mansion (Agent Dupont)
                Triple(
                    RealEstateItem(
                        type = "Manor",
                        priceInDollars = 8500000,
                        surfaceInSqm = 750,
                        numberOfRooms = 16,
                        description = "Exclusive Hamptons estate with private tennis court and heated pool.",
                        address = "777 Dune Road, The Hamptons, New York, NY",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = Instant.now().minusSeconds(86400L * 20),
                        agentId = agentDupontId,
                        latitude = 40.9100,
                        longitude = -72.4300
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1583847268964-b28dc8f51f92", description = "Estate exterior"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9", description = "Poolside")
                    ),
                    Pair("Swimming Pool, Wine Cellar, Garden, Garage, Fireplace, Security System", "Beach, Restaurant, Harbor")
                ),
                // 13. Apartment (Agent Smith)
                Triple(
                    RealEstateItem(
                        type = "Apartment",
                        priceInDollars = 1100000,
                        surfaceInSqm = 110,
                        numberOfRooms = 4,
                        description = "Midtown East classic apartment near Grand Central Terminal.",
                        address = "300 East 54th St, Midtown, New York, NY",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = Instant.now().minusSeconds(86400L * 2),
                        agentId = agentSmithId,
                        latitude = 40.7580,
                        longitude = -73.9680
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750", description = "Living room"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267", description = "Bedroom")
                    ),
                    Pair("Elevator, Balcony, Security System", "Grand Central, St. Patrick's Cathedral, Restaurant")
                ),
                // 14. Townhouse (Agent Jane)
                Triple(
                    RealEstateItem(
                        type = "Townhouse",
                        priceInDollars = 3100000,
                        surfaceInSqm = 310,
                        numberOfRooms = 9,
                        description = "Charming Brooklyn Heights brick townhouse with private courtyard.",
                        address = "88 Cranberry St, Brooklyn Heights, New York, NY",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = Instant.now().minusSeconds(86400L * 15),
                        agentId = agentJaneId,
                        latitude = 40.7000,
                        longitude = -73.9920
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c", description = "Courtyard"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600566753376-12c8ab7fb75b", description = "Interior")
                    ),
                    Pair("Garden, Garage, Fireplace", "Brooklyn Promenade, Restaurant, Park")
                ),
                // 15. Luxury Apartment (Agent Dupont)
                Triple(
                    RealEstateItem(
                        type = "Apartment",
                        priceInDollars = 2400000,
                        surfaceInSqm = 170,
                        numberOfRooms = 5,
                        description = "Prestige residence at 15 Central Park West with unmatched park views.",
                        address = "15 Central Park West, New York, NY",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = Instant.now().minusSeconds(86400L * 1),
                        agentId = agentDupontId,
                        latitude = 40.7680,
                        longitude = -73.9810
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00", description = "Park view"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1512918728675-ed5a9ecdebfd", description = "Lobby")
                    ),
                    Pair("Swimming Pool, Gym, Terrace, Security System, Air Conditioning", "Central Park, Lincoln Center, Museum")
                )
            )

            // Insert each property, pictures, and establish normalized cross-references
            for ((property, pictures, metadata) in rawPropertyList) {
                val propertyId = dao.insertProperty(property)
                
                // Insert pictures
                val picturesWithId = pictures.map { it.copy(propertyId = propertyId) }
                dao.insertPictures(picturesWithId)

                // Normalize and link amenities
                val amenitiesList = metadata.first.split(",").map { it.trim() }.filter { it.isNotBlank() }
                for (amenityName in amenitiesList) {
                    val amenityId = amenityCache.getOrPut(amenityName) {
                        dao.insertAmenity(Amenity(name = amenityName))
                    }
                    dao.insertAmenityCrossRef(PropertyAmenityCrossRef(propertyId = propertyId, amenityId = amenityId))
                }

                // Normalize and link POIs
                val poiList = metadata.second.split(",").map { it.trim() }.filter { it.isNotBlank() }
                for (poiName in poiList) {
                    val poiId = poiCache.getOrPut(poiName) {
                        dao.insertPoi(Poi(name = poiName))
                    }
                    dao.insertPoiCrossRef(PropertyPoiCrossRef(propertyId = propertyId, poiId = poiId))
                }
            }
        }
    }
}
