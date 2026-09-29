package com.lieruce.realestatemanager

import org.junit.Assert.*
import org.junit.Test

/**
 * Local JVM unit tests for Utils class methods (date formatting, currency conversions, and network availability null-safety).
 */
class UtilsTest {

    @Test
    fun testGetTodayDateNewFormat() {
        val dateString = Utils.getTodayDateNew()
        assertNotNull(dateString)
        
        // Verify format matches dd/MM/yyyy using a regular expression
        val regex = Regex("\\d{2}/\\d{2}/\\d{4}")
        assertTrue("Date string '$dateString' should match dd/MM/yyyy format", regex.matches(dateString))
    }

    @Test
    fun testGetTodayDateOldFormat() {
        val oldDateString = Utils.getTodayDateOld()
        assertNotNull(oldDateString)
        
        // Verify format matches yyyy/MM/dd using a regular expression
        val regex = Regex("\\d{4}/\\d{2}/\\d{2}")
        assertTrue("Old date string '$oldDateString' should match yyyy/MM/dd format", regex.matches(oldDateString))
    }

    @Test
    fun testIsInternetAvailableNewNullContext() {
        // Passing null context should safely return false without crashing
        assertFalse(Utils.isInternetAvailableNew(null))
    }

    @Test
    fun testDollarToEuroConversion() {
        // Set a known rate via Utils
        Utils.setDollarEuroRate(0.85)
        
        // $100 converted at 0.85 should be 85 euros
        val euros = Utils.convertDollarToEuro(100)
        assertEquals(85, euros.toLong())
    }

    @Test
    fun testEuroToDollarConversion() {
        // Set rate 0.80 -> euroDollarRate becomes 1.25
        Utils.setDollarEuroRate(0.80)
        
        // 100 euros converted at 1.25 should be 125 dollars
        val dollars = Utils.convertEuroToDollar(100)
        assertEquals(125, dollars.toLong())
    }
}
