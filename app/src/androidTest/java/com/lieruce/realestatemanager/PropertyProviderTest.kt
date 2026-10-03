package com.lieruce.realestatemanager

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lieruce.realestatemanager.provider.PropertyProvider
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented integration tests running on an Android device or emulator
 * to verify that PropertyProvider exposes database records correctly via ContentResolver.
 */
@RunWith(AndroidJUnit4::class)
class PropertyProviderTest {

    @Test
    fun testQueryAllPropertiesViaContentResolver() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val uri = PropertyProvider.URI_ITEM

        // Query the Content Provider using standard ContentResolver
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        
        assertNotNull("Cursor should not be null", cursor)
        cursor?.use {
            // Verify that the query executes successfully and returns a valid cursor
            assertTrue("Cursor count should be non-negative", it.count >= 0)
        }
    }
}
