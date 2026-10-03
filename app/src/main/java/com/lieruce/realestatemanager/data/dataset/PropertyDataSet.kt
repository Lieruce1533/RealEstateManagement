package com.lieruce.realestatemanager.data.dataset

import com.lieruce.realestatemanager.data.model.PropertyLocation
import com.lieruce.realestatemanager.data.model.PropertyPicture
import com.lieruce.realestatemanager.data.model.PropertyStatus
import com.lieruce.realestatemanager.data.model.RealEstateItem
import java.time.Instant

/**
 * Data container representing a property along with its pictures and associated standardized amenity and POI names.
 */
data class PropertySeedRecord(
    val property: RealEstateItem,
    val pictures: List<PropertyPicture>,
    val amenityNames: List<String>,
    val poiNames: List<String>
)

/**
 * DataSet providing 15 authentic New York real estate listings for database seeding,
 * utilizing standardized amenities and POIs matching PropertyConstants.
 */
object PropertyDataSet {
    fun getInitialProperties(
        agentSmithId: Long,
        agentJaneId: Long,
        agentDupontId: Long
    ): List<PropertySeedRecord> {
        return listOf(
            // 1. Penthouse (Agent Smith)
            PropertySeedRecord(
                property = RealEstateItem(
                    type = "Penthouse",
                    priceInDollars = 4500000,
                    surfaceInSqm = 350,
                    numberOfRooms = 7,
                    description = "Magnificent luxury penthouse overlooking Central Park with private elevator.",
                    location = PropertyLocation(
                        address = "157 West 57th St, Manhattan, New York, NY",
                        latitude = 40.7656,
                        longitude = -73.9799
                    ),
                    status = PropertyStatus.AVAILABLE,
                    entryDate = Instant.now().minusSeconds(86400L * 10),
                    agentId = agentSmithId
                ),
                pictures = listOf(
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00", description = "Living room view"),
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c", description = "Terrace")
                ),
                amenityNames = listOf("Pool", "Terrace", "Security System", "Air Conditioning"),
                poiNames = listOf("Park", "Public Transportation")
            ),
            // 2. Brownstone (Agent Jane)
            PropertySeedRecord(
                property = RealEstateItem(
                    type = "Brownstone",
                    priceInDollars = 2800000,
                    surfaceInSqm = 300,
                    numberOfRooms = 9,
                    description = "Classic historic Harlem brownstone with original fireplaces and private garden.",
                    location = PropertyLocation(
                        address = "124 West 120th St, Harlem, New York, NY",
                        latitude = 40.8065,
                        longitude = -73.9485
                    ),
                    status = PropertyStatus.AVAILABLE,
                    entryDate = Instant.now().minusSeconds(86400L * 8),
                    agentId = agentJaneId
                ),
                pictures = listOf(
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1580587771525-78b9dba3b914", description = "Brownstone exterior"),
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c", description = "Interior fireplace")
                ),
                amenityNames = listOf("Garden", "Fireplace", "Garage"),
                poiNames = listOf("Park", "School", "Public Transportation")
            ),
            // 3. Loft (Agent Dupont)
            PropertySeedRecord(
                property = RealEstateItem(
                    type = "Loft",
                    priceInDollars = 1950000,
                    surfaceInSqm = 200,
                    numberOfRooms = 5,
                    description = "Spacious Tribeca industrial loft with exposed brick and high ceilings.",
                    location = PropertyLocation(
                        address = "74 Franklin St, Tribeca, New York, NY",
                        latitude = 40.7163,
                        longitude = -74.0048
                    ),
                    status = PropertyStatus.AVAILABLE,
                    entryDate = Instant.now().minusSeconds(86400L * 6),
                    agentId = agentDupontId
                ),
                pictures = listOf(
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688", description = "Open space"),
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2", description = "Kitchen")
                ),
                amenityNames = listOf("Air Conditioning", "Security System", "Garage"),
                poiNames = listOf("Restaurant", "Shopping Mall")
            ),
            // 4. Apartment (Agent Smith)
            PropertySeedRecord(
                property = RealEstateItem(
                    type = "Apartment",
                    priceInDollars = 1250000,
                    surfaceInSqm = 120,
                    numberOfRooms = 4,
                    description = "Elegant Upper East Side apartment steps from Fifth Avenue shopping.",
                    location = PropertyLocation(
                        address = "834 Fifth Avenue, Upper East Side, New York, NY",
                        latitude = 40.7712,
                        longitude = -73.9674
                    ),
                    status = PropertyStatus.AVAILABLE,
                    entryDate = Instant.now().minusSeconds(86400L * 5),
                    agentId = agentSmithId
                ),
                pictures = listOf(
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750", description = "Living room"),
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267", description = "Bedroom")
                ),
                amenityNames = listOf("Balcony", "Garage", "Security System"),
                poiNames = listOf("Park", "Shopping Mall", "Library")
            ),
            // 5. Townhouse (Agent Jane)
            PropertySeedRecord(
                property = RealEstateItem(
                    type = "Townhouse",
                    priceInDollars = 3400000,
                    surfaceInSqm = 320,
                    numberOfRooms = 10,
                    description = "Stunning Brooklyn Heights townhouse with stunning harbor views.",
                    location = PropertyLocation(
                        address = "45 Willow St, Brooklyn Heights, New York, NY",
                        latitude = 40.6970,
                        longitude = -73.9946
                    ),
                    status = PropertyStatus.AVAILABLE,
                    entryDate = Instant.now().minusSeconds(86400L * 4),
                    agentId = agentJaneId
                ),
                pictures = listOf(
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9", description = "Townhouse front"),
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600585152220-90363fe7e115", description = "Patio")
                ),
                amenityNames = listOf("Garden", "Garage", "Fireplace", "Terrace"),
                poiNames = listOf("Park", "Public Transportation")
            ),
            // 6. Luxury Apartment (Agent Dupont)
            PropertySeedRecord(
                property = RealEstateItem(
                    type = "Apartment",
                    priceInDollars = 2100000,
                    surfaceInSqm = 160,
                    numberOfRooms = 5,
                    description = "Billionaires' Row ultra-luxury residence with full hotel amenities.",
                    location = PropertyLocation(
                        address = "111 West 57th St, Manhattan, New York, NY",
                        latitude = 40.7648,
                        longitude = -73.9776
                    ),
                    status = PropertyStatus.SOLD,
                    entryDate = Instant.now().minusSeconds(86400L * 30),
                    saleDate = Instant.now().minusSeconds(86400L * 3),
                    agentId = agentDupontId
                ),
                pictures = listOf(
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00", description = "Interior view"),
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1512918728675-ed5a9ecdebfd", description = "Lobby")
                ),
                amenityNames = listOf("Pool", "Gym", "Terrace", "Security System"),
                poiNames = listOf("Park", "Library", "Shopping Mall")
            ),
            // 7. Duplex (Agent Smith)
            PropertySeedRecord(
                property = RealEstateItem(
                    type = "Duplex",
                    priceInDollars = 1650000,
                    surfaceInSqm = 150,
                    numberOfRooms = 6,
                    description = "Trendy Williamsburg duplex with double-height windows and private patio.",
                    location = PropertyLocation(
                        address = "15 North 6th St, Williamsburg, Brooklyn, NY",
                        latitude = 40.7188,
                        longitude = -73.9592
                    ),
                    status = PropertyStatus.AVAILABLE,
                    entryDate = Instant.now().minusSeconds(86400L * 3),
                    agentId = agentSmithId
                ),
                pictures = listOf(
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600210492486-724fe5c67fb0", description = "Living area"),
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600566753376-12c8ab7fb75b", description = "Kitchen")
                ),
                amenityNames = listOf("Balcony", "Terrace", "Air Conditioning"),
                poiNames = listOf("Park", "Restaurant")
            ),
            // 8. Condo (Agent Jane)
            PropertySeedRecord(
                property = RealEstateItem(
                    type = "Apartment",
                    priceInDollars = 890000,
                    surfaceInSqm = 90,
                    numberOfRooms = 3,
                    description = "Modern Long Island City glass condo with skyline vistas.",
                    location = PropertyLocation(
                        address = "42-15 Crescent St, Long Island City, NY",
                        latitude = 40.7523,
                        longitude = -73.9390
                    ),
                    status = PropertyStatus.AVAILABLE,
                    entryDate = Instant.now().minusSeconds(86400L * 12),
                    agentId = agentJaneId
                ),
                pictures = listOf(
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1512915922686-57c11dde9b6b", description = "Condo view"),
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1493809842364-78817add7ffb", description = "Bedroom")
                ),
                amenityNames = listOf("Balcony", "Gym", "Security System"),
                poiNames = listOf("Park", "Public Transportation", "Restaurant")
            ),
            // 9. Brownstone (Agent Dupont)
            PropertySeedRecord(
                property = RealEstateItem(
                    type = "Brownstone",
                    priceInDollars = 2600000,
                    surfaceInSqm = 280,
                    numberOfRooms = 8,
                    description = "Park Slope historic brownstone steps from Prospect Park.",
                    location = PropertyLocation(
                        address = "250 Park Slope West, Brooklyn, NY",
                        latitude = 40.6700,
                        longitude = -73.9800
                    ),
                    status = PropertyStatus.AVAILABLE,
                    entryDate = Instant.now().minusSeconds(86400L * 9),
                    agentId = agentDupontId
                ),
                pictures = listOf(
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1580587771525-78b9dba3b914", description = "Facade"),
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c", description = "Parlor floor")
                ),
                amenityNames = listOf("Garden", "Fireplace", "Terrace"),
                poiNames = listOf("Park", "Library", "School")
            ),
            // 10. Penthouse (Agent Smith)
            PropertySeedRecord(
                property = RealEstateItem(
                    type = "Penthouse",
                    priceInDollars = 5200000,
                    surfaceInSqm = 400,
                    numberOfRooms = 8,
                    description = "Financial District crown jewel penthouse with 360-degree river views.",
                    location = PropertyLocation(
                        address = "50 West St, Financial District, New York, NY",
                        latitude = 40.7081,
                        longitude = -74.0143
                    ),
                    status = PropertyStatus.AVAILABLE,
                    entryDate = Instant.now().minusSeconds(86400L * 7),
                    agentId = agentSmithId
                ),
                pictures = listOf(
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00", description = "Rooftop terrace"),
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c", description = "Master suite")
                ),
                amenityNames = listOf("Pool", "Terrace", "Security System", "Gym", "Air Conditioning"),
                poiNames = listOf("Park", "Public Transportation")
            ),
            // 11. Loft (Agent Jane)
            PropertySeedRecord(
                property = RealEstateItem(
                    type = "Loft",
                    priceInDollars = 1800000,
                    surfaceInSqm = 190,
                    numberOfRooms = 5,
                    description = "SoHo artistic loft with cast-iron columns and soaring ceilings.",
                    location = PropertyLocation(
                        address = "200 Mercer St, SoHo, New York, NY",
                        latitude = 40.7250,
                        longitude = -73.9960
                    ),
                    status = PropertyStatus.AVAILABLE,
                    entryDate = Instant.now().minusSeconds(86400L * 11),
                    agentId = agentJaneId
                ),
                pictures = listOf(
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688", description = "Loft space"),
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2", description = "Kitchen")
                ),
                amenityNames = listOf("Air Conditioning", "Security System"),
                poiNames = listOf("Park", "Shopping Mall", "Restaurant")
            ),
            // 12. Mansion (Agent Dupont)
            PropertySeedRecord(
                property = RealEstateItem(
                    type = "Manor",
                    priceInDollars = 8500000,
                    surfaceInSqm = 750,
                    numberOfRooms = 16,
                    description = "Exclusive Hamptons estate with private tennis court and heated pool.",
                    location = PropertyLocation(
                        address = "777 Dune Road, The Hamptons, New York, NY",
                        latitude = 40.9100,
                        longitude = -72.4300
                    ),
                    status = PropertyStatus.AVAILABLE,
                    entryDate = Instant.now().minusSeconds(86400L * 20),
                    agentId = agentDupontId
                ),
                pictures = listOf(
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1583847268964-b28dc8f51f92", description = "Estate exterior"),
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9", description = "Poolside")
                ),
                amenityNames = listOf("Pool", "Garden", "Garage", "Fireplace", "Security System"),
                poiNames = listOf("Restaurant", "Hospital")
            ),
            // 13. Apartment (Agent Smith)
            PropertySeedRecord(
                property = RealEstateItem(
                    type = "Apartment",
                    priceInDollars = 1100000,
                    surfaceInSqm = 110,
                    numberOfRooms = 4,
                    description = "Midtown East classic apartment near Grand Central Terminal.",
                    location = PropertyLocation(
                        address = "300 East 54th St, Midtown, New York, NY",
                        latitude = 40.7580,
                        longitude = -73.9680
                    ),
                    status = PropertyStatus.AVAILABLE,
                    entryDate = Instant.now().minusSeconds(86400L * 2),
                    agentId = agentSmithId
                ),
                pictures = listOf(
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750", description = "Living room"),
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267", description = "Bedroom")
                ),
                amenityNames = listOf("Garage", "Balcony", "Security System"),
                poiNames = listOf("Public Transportation", "Restaurant")
            ),
            // 14. Townhouse (Agent Jane)
            PropertySeedRecord(
                property = RealEstateItem(
                    type = "Townhouse",
                    priceInDollars = 3100000,
                    surfaceInSqm = 310,
                    numberOfRooms = 9,
                    description = "Charming Brooklyn Heights brick townhouse with private courtyard.",
                    location = PropertyLocation(
                        address = "88 Cranberry St, Brooklyn Heights, New York, NY",
                        latitude = 40.7000,
                        longitude = -73.9920
                    ),
                    status = PropertyStatus.AVAILABLE,
                    entryDate = Instant.now().minusSeconds(86400L * 15),
                    agentId = agentJaneId
                ),
                pictures = listOf(
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c", description = "Courtyard"),
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1600566753376-12c8ab7fb75b", description = "Interior")
                ),
                amenityNames = listOf("Garden", "Garage", "Fireplace"),
                poiNames = listOf("Park", "Restaurant")
            ),
            // 15. Luxury Apartment (Agent Dupont)
            PropertySeedRecord(
                property = RealEstateItem(
                    type = "Apartment",
                    priceInDollars = 2400000,
                    surfaceInSqm = 170,
                    numberOfRooms = 5,
                    description = "Prestige residence at 15 Central Park West with unmatched park views.",
                    location = PropertyLocation(
                        address = "15 Central Park West, New York, NY",
                        latitude = 40.7680,
                        longitude = -73.9810
                    ),
                    status = PropertyStatus.AVAILABLE,
                    entryDate = Instant.now().minusSeconds(86400L * 1),
                    agentId = agentDupontId
                ),
                pictures = listOf(
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00", description = "Park view"),
                    PropertyPicture(propertyId = 0, uri = "https://images.unsplash.com/property-id/0", description = "Lobby")
                ),
                amenityNames = listOf("Pool", "Gym", "Terrace", "Security System", "Air Conditioning"),
                poiNames = listOf("Park", "Library", "School")
            )
        )
    }
}
