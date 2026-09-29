package com.lieruce.realestatemanager

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lieruce.realestatemanager.data.AppDatabase
import com.lieruce.realestatemanager.data.dao.PropertyDao
import com.lieruce.realestatemanager.data.model.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

/**
 * Instrumented integration tests running on Android runtime
 * to verify Room database creation, DAO operations, and relational queries (PropertyWithRelations).
 */
@RunWith(AndroidJUnit4::class)
class RoomDatabaseTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: PropertyDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Build an in-memory Room database for isolated integration testing
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.propertyDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testInsertAndRetrievePropertyWithRelations() = runBlocking {
        // Insert a test agent
        val agentId = dao.insertAgent(Agent(name = "Test Agent", email = "test@agent.com", phone = "555-0192"))
        
        // Insert a test property
        val property = RealEstateItem(
            type = "Townhouse",
            priceInDollars = 1500000,
            surfaceInSqm = 220,
            numberOfRooms = 6,
            description = "Integration test townhouse",
            location = PropertyLocation(address = "123 Test Ave, NY", latitude = 40.7, longitude = -74.0),
            status = PropertyStatus.AVAILABLE,
            entryDate = Instant.now(),
            agentId = agentId
        )

        val propertyId = dao.insertProperty(property)
        dao.insertPictures(listOf(PropertyPicture(propertyId = propertyId, uri = "test.jpg", description = "Front")))

        // Query property with relations
        val relations = dao.getPropertyById(propertyId).first()
        
        assertNotNull("Retrieved property relations should not be null", relations)
        assertEquals("Townhouse", relations?.property?.type)
        assertEquals(1, relations?.pictures?.size)
        assertEquals("Test Agent", relations?.agent?.name)
    }
}
