package com.lieruce.realestatemanager

import com.lieruce.realestatemanager.data.LocationRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Local JVM unit tests for LocationRepository error handling and edge cases.
 */
class LocationRepositoryTest {

    @Test
    fun testGeocodeBlankAddressReturnsNull() = runBlocking {
        val repository = LocationRepository()

        // Passing a blank address should trigger network/parsing catch blocks gracefully and return null
        val result = repository.getLatLngFromAddress("")
        assertNull(result)
    }
}
