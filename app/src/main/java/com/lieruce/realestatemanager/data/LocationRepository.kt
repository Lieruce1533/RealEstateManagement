package com.lieruce.realestatemanager.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * LocationRepository handles geocoding using OpenStreetMap's Nominatim API
 * to convert human-readable address strings to GPS latitude/longitude coordinates.
 */
class LocationRepository(private val context: Context) {

    companion object {
        private const val TAG = "LocationRepository"
        private const val NOMINATIM_URL = "https://nominatim.openstreetmap.org/search"
    }

    /**
     * Converts a human-readable address string into a Pair of (Latitude, Longitude) using OpenStreetMap Nominatim.
     * Uses Dispatchers.IO to ensure network requests run off the main UI thread.
     */
    suspend fun getLatLngFromAddress(address: String): Pair<Double, Double>? {
        return withContext(Dispatchers.IO) {
            try {
                // URL encode the address string for safe transmission over HTTP
                val encodedAddress = URLEncoder.encode(address, "UTF-8")
                val urlString = "$NOMINATIM_URL?q=$encodedAddress&format=json&limit=1"

                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                // Nominatim usage policy strictly requires a descriptive User-Agent header
                connection.setRequestProperty("User-Agent", "RealEstateManagerApp/1.0")
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the JSON response string from the input stream
                    val responseString = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonArray = JSONArray(responseString)

                    if (jsonArray.length() > 0) {
                        val jsonObject = jsonArray.getJSONObject(0)
                        val lat = jsonObject.getDouble("lat")
                        val lon = jsonObject.getDouble("lon")
                        Pair(lat, lon)
                    } else {
                        Log.w(TAG, "Nominatim found no coordinates for address: $address")
                        null
                    }
                } else {
                    Log.e(TAG, "Nominatim HTTP error code: $responseCode")
                    null
                }
            } catch (e: Exception) {
                // Catch network timeouts, connection drops, or JSON parsing errors gracefully
                Log.e(TAG, "Nominatim geocoding failed for address: $address", e)
                null
            }
        }
    }
}
