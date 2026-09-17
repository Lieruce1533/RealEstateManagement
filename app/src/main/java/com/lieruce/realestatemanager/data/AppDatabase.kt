package com.lieruce.realestatemanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lieruce.realestatemanager.data.dao.PropertyDao
import com.lieruce.realestatemanager.data.model.PropertyPicture
import com.lieruce.realestatemanager.data.model.PropertyStatus
import com.lieruce.realestatemanager.data.model.RealEstateItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [RealEstateItem::class, PropertyPicture::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun propertyDao(): PropertyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns the singleton instance of the Room database.
         * Automatically seeds 15 realistic New York real estate properties upon initial database creation.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "real_estate_database"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed sample data in the background when the database is created for the first time
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { populateInitialData(it) }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Populates the database with 15 realistic New York real estate properties across 3 agents,
         * complete with photos, amenities, points of interest, and New York GPS coordinates.
         */
        private suspend fun populateInitialData(database: AppDatabase) {
            val dao = database.propertyDao()

            val agentSmith = "Agent Smith"
            val agentJane = "Agent Jane"
            val agentDupont = "Agent Dupont"

            val properties = listOf(
                // 1. Penthouse (Agent Smith)
                Pair(
                    RealEstateItem(
                        type = "Penthouse",
                        priceInDollars = 4500000,
                        surfaceInSqm = 350,
                        numberOfRooms = 7,
                        description = "Magnificent luxury penthouse overlooking Central Park with private elevator.",
                        address = "157 West 57th St, Manhattan, New York, NY",
                        pointsOfInterest = "Central Park, Broadway, Carnegie Hall",
                        amenities = "Swimming Pool, Terrace, Security System, Air Conditioning",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = System.currentTimeMillis() - 86400000L * 10,
                        agentName = agentSmith,
                        latitude = 40.7656,
                        longitude = -73.9799
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00", description = "Living room view"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c", description = "Terrace")
                    )
                ),
                // 2. Brownstone (Agent Jane)
                Pair(
                    RealEstateItem(
                        type = "Brownstone",
                        priceInDollars = 2800000,
                        surfaceInSqm = 300,
                        numberOfRooms = 9,
                        description = "Classic historic Harlem brownstone with original fireplaces and private garden.",
                        address = "124 West 120th St, Harlem, New York, NY",
                        pointsOfInterest = "Morningside Park, Columbia University, Subway",
                        amenities = "Garden, Fireplace, Garage",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = System.currentTimeMillis() - 86400000L * 8,
                        agentName = agentJane,
                        latitude = 40.8065,
                        longitude = -73.9485
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1580587771525-78b9dba3b914", description = "Brownstone exterior"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c", description = "Interior fireplace")
                    )
                ),
                // 3. Loft (Agent Dupont)
                Pair(
                    RealEstateItem(
                        type = "Loft",
                        priceInDollars = 1950000,
                        surfaceInSqm = 200,
                        numberOfRooms = 5,
                        description = "Spacious Tribeca industrial loft with exposed brick and high ceilings.",
                        address = "74 Franklin St, Tribeca, New York, NY",
                        pointsOfInterest = "Wall Street, World Trade Center, Restaurant",
                        amenities = "Air Conditioning, Security System, Elevator",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = System.currentTimeMillis() - 86400000L * 6,
                        agentName = agentDupont,
                        latitude = 40.7163,
                        longitude = -74.0048
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688", description = "Open space"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2", description = "Kitchen")
                    )
                ),
                // 4. Apartment (Agent Smith)
                Pair(
                    RealEstateItem(
                        type = "Apartment",
                        priceInDollars = 1250000,
                        surfaceInSqm = 120,
                        numberOfRooms = 4,
                        description = "Elegant Upper East Side apartment steps from Fifth Avenue shopping.",
                        address = "834 Fifth Avenue, Upper East Side, New York, NY",
                        pointsOfInterest = "Central Park, Fifth Avenue, Museum",
                        amenities = "Balcony, Elevator, Security System",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = System.currentTimeMillis() - 86400000L * 5,
                        agentName = agentSmith,
                        latitude = 40.7712,
                        longitude = -73.9674
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750", description = "Living room"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267", description = "Bedroom")
                    )
                ),
                // 5. Townhouse (Agent Jane)
                Pair(
                    RealEstateItem(
                        type = "Townhouse",
                        priceInDollars = 3400000,
                        surfaceInSqm = 320,
                        numberOfRooms = 10,
                        description = "Stunning Brooklyn Heights townhouse with stunning harbor views.",
                        address = "45 Willow St, Brooklyn Heights, New York, NY",
                        pointsOfInterest = "Brooklyn Bridge, DUMBO, Promenade",
                        amenities = "Garden, Garage, Fireplace, Terrace",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = System.currentTimeMillis() - 86400000L * 4,
                        agentName = agentJane,
                        latitude = 40.6970,
                        longitude = -73.9946
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9", description = "Townhouse front"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600585152220-90363fe7e115", description = "Patio")
                    )
                ),
                // 6. Luxury Apartment (Agent Dupont)
                Pair(
                    RealEstateItem(
                        type = "Apartment",
                        priceInDollars = 2100000,
                        surfaceInSqm = 160,
                        numberOfRooms = 5,
                        description = "Billionaires' Row ultra-luxury residence with full hotel amenities.",
                        address = "111 West 57th St, Manhattan, New York, NY",
                        pointsOfInterest = "Carnegie Hall, Central Park, Fifth Avenue",
                        amenities = "Swimming Pool, Gym, Terrace, Security System",
                        status = PropertyStatus.SOLD,
                        entryDate = System.currentTimeMillis() - 86400000L * 30,
                        saleDate = System.currentTimeMillis() - 86400000L * 3,
                        agentName = agentDupont,
                        latitude = 40.7648,
                        longitude = -73.9776
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00", description = "Interior view"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1512918728675-ed5a9ecdebfd", description = "Lobby")
                    )
                ),
                // 7. Duplex (Agent Smith)
                Pair(
                    RealEstateItem(
                        type = "Duplex",
                        priceInDollars = 1650000,
                        surfaceInSqm = 150,
                        numberOfRooms = 6,
                        description = "Trendy Williamsburg duplex with double-height windows and private patio.",
                        address = "15 North 6th St, Williamsburg, Brooklyn, NY",
                        pointsOfInterest = "Domino Park, East River, Restaurant",
                        amenities = "Balcony, Terrace, Air Conditioning",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = System.currentTimeMillis() - 86400000L * 3,
                        agentName = agentSmith,
                        latitude = 40.7188,
                        longitude = -73.9592
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600210492486-724fe5c67fb0", description = "Living area"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600566753376-12c8ab7fb75b", description = "Kitchen")
                    )
                ),
                // 8. Condo (Agent Jane)
                Pair(
                    RealEstateItem(
                        type = "Apartment",
                        priceInDollars = 890000,
                        surfaceInSqm = 90,
                        numberOfRooms = 3,
                        description = "Modern Long Island City glass condo with skyline vistas.",
                        address = "42-15 Crescent St, Long Island City, NY",
                        pointsOfInterest = "Gantry Plaza State Park, Subway, Restaurant",
                        amenities = "Balcony, Gym, Security System",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = System.currentTimeMillis() - 86400000L * 12,
                        agentName = agentJane,
                        latitude = 40.7523,
                        longitude = -73.9390
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1502005229762-cf1b4da7c5d6", description = "Condo view"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1493809842364-78817add7ffb", description = "Bedroom")
                    )
                ),
                // 9. Brownstone (Agent Dupont)
                Pair(
                    RealEstateItem(
                        type = "Brownstone",
                        priceInDollars = 2600000,
                        surfaceInSqm = 280,
                        numberOfRooms = 8,
                        description = "Park Slope historic brownstone steps from Prospect Park.",
                        address = "250 Park Slope West, Brooklyn, NY",
                        pointsOfInterest = "Prospect Park, Library, Museum",
                        amenities = "Garden, Fireplace, Terrace",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = System.currentTimeMillis() - 86400000L * 9,
                        agentName = agentDupont,
                        latitude = 40.6700,
                        longitude = -73.9800
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1580587771525-78b9dba3b914", description = "Facade"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c", description = "Parlor floor")
                    )
                ),
                // 10. Penthouse (Agent Smith)
                Pair(
                    RealEstateItem(
                        type = "Penthouse",
                        priceInDollars = 5200000,
                        surfaceInSqm = 400,
                        numberOfRooms = 8,
                        description = "Financial District crown jewel penthouse with 360-degree river views.",
                        address = "50 West St, Financial District, New York, NY",
                        pointsOfInterest = "Battery Park, Wall Street, Subway",
                        amenities = "Swimming Pool, Terrace, Security System, Gym, Air Conditioning",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = System.currentTimeMillis() - 86400000L * 7,
                        agentName = agentSmith,
                        latitude = 40.7081,
                        longitude = -74.0143
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00", description = "Rooftop terrace"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c", description = "Master suite")
                    )
                ),
                // 11. Loft (Agent Jane)
                Pair(
                    RealEstateItem(
                        type = "Loft",
                        priceInDollars = 1800000,
                        surfaceInSqm = 190,
                        numberOfRooms = 5,
                        description = "SoHo artistic loft with cast-iron columns and soaring ceilings.",
                        address = "200 Mercer St, SoHo, New York, NY",
                        pointsOfInterest = "Washington Square Park, Shopping Mall, Restaurant",
                        amenities = "Air Conditioning, Security System",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = System.currentTimeMillis() - 86400000L * 11,
                        agentName = agentJane,
                        latitude = 40.7250,
                        longitude = -73.9960
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688", description = "Loft space"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2", description = "Kitchen")
                    )
                ),
                // 12. Mansion (Agent Dupont)
                Pair(
                    RealEstateItem(
                        type = "Manor",
                        priceInDollars = 8500000,
                        surfaceInSqm = 750,
                        numberOfRooms = 16,
                        description = "Exclusive Hamptons estate with private tennis court and heated pool.",
                        address = "777 Dune Road, The Hamptons, New York, NY",
                        pointsOfInterest = "Beach, Restaurant, Harbor",
                        amenities = "Swimming Pool, Wine Cellar, Garden, Garage, Fireplace, Security System",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = System.currentTimeMillis() - 86400000L * 20,
                        agentName = agentDupont,
                        latitude = 40.9100,
                        longitude = -72.4300
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1583847268964-b28dc8f51f92", description = "Estate exterior"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9", description = "Poolside")
                    )
                ),
                // 13. Apartment (Agent Smith)
                Pair(
                    RealEstateItem(
                        type = "Apartment",
                        priceInDollars = 1100000,
                        surfaceInSqm = 110,
                        numberOfRooms = 4,
                        description = "Midtown East classic apartment near Grand Central Terminal.",
                        address = "300 East 54th St, Midtown, New York, NY",
                        pointsOfInterest = "Grand Central, St. Patrick's Cathedral, Restaurant",
                        amenities = "Elevator, Balcony, Security System",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = System.currentTimeMillis() - 86400000L * 2,
                        agentName = agentSmith,
                        latitude = 40.7580,
                        longitude = -73.9680
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750", description = "Living room"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267", description = "Bedroom")
                    )
                ),
                // 14. Townhouse (Agent Jane)
                Pair(
                    RealEstateItem(
                        type = "Townhouse",
                        priceInDollars = 3100000,
                        surfaceInSqm = 310,
                        numberOfRooms = 9,
                        description = "Charming Brooklyn Heights brick townhouse with private courtyard.",
                        address = "88 Cranberry St, Brooklyn Heights, New York, NY",
                        pointsOfInterest = "Brooklyn Promenade, Restaurant, Park",
                        amenities = "Garden, Garage, Fireplace",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = System.currentTimeMillis() - 86400000L * 15,
                        agentName = agentJane,
                        latitude = 40.7000,
                        longitude = -73.9920
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c", description = "Courtyard"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600566753376-12c8ab7fb75b", description = "Interior")
                    )
                ),
                // 15. Luxury Apartment (Agent Dupont)
                Pair(
                    RealEstateItem(
                        type = "Apartment",
                        priceInDollars = 2400000,
                        surfaceInSqm = 170,
                        numberOfRooms = 5,
                        description = "Prestige residence at 15 Central Park West with unmatched park views.",
                        address = "15 Central Park West, New York, NY",
                        pointsOfInterest = "Central Park, Lincoln Center, Museum",
                        amenities = "Swimming Pool, Gym, Terrace, Security System, Air Conditioning",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = System.currentTimeMillis() - 86400000L * 1,
                        agentName = agentDupont,
                        latitude = 40.7680,
                        longitude = -73.9810
                    ),
                    listOf(
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00", description = "Park view"),
                        PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/property-id/0", description = "Lobby")
                    )
                )
            )

            // Insert each property and its associated pictures into Room
            for ((property, pictures) in properties) {
                val propertyId = dao.insertProperty(property)
                val picturesWithId = pictures.map { it.copy(propertyId = propertyId) }
                dao.insertPictures(picturesWithId)
            }
        }
    }
}
