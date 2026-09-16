package com.lieruce.realestatemanager.data

import android.util.Log
import com.lieruce.realestatemanager.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * CurrencyRepository is responsible for fetching live currency conversion rates 
 * from an external API in the background and updating Utils with the fresh rates.
 */
class CurrencyRepository {

    companion object {
        private const val TAG = "CurrencyRepository"
        // Target API endpoint for USD to EUR conversion
        private const val API_URL = "https://fxapi.app/api/USD/EUR.json"
    }

    /**
     * Fetches the latest USD to EUR conversion rate from the network.
     * Uses Dispatchers.IO to ensure network requests run on a background thread pool,
     * protecting the main UI thread from lagging or crashing.
     */
    suspend fun fetchAndUpdateExchangeRate() {
        withContext(Dispatchers.IO) {
            try {
                // Open connection to the API endpoint
                val url = URL(API_URL)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000 // 5 seconds timeout
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the response stream into a plain JSON string
                    val responseString = connection.inputStream.bufferedReader().use { it.readText() }
                    Log.d(TAG, "Successfully fetched rate JSON: $responseString")

                    // Parse JSON using Android's built-in JSONObject
                    val jsonObject = JSONObject(responseString)
                    
                    // Extract the rate (falling back to 0.812 if the key is missing)
                    val rate = jsonObject.optDouble("rate", 0.812)
                    
                    if (rate > 0.0) {
                        // Update Utils static rate so convertDollarToEuro uses the live rate
                        Utils.setDollarEuroRate(rate)
                        Log.d(TAG, "Updated live conversion rate to: $rate")
                    }
                } else {
                    Log.e(TAG, "Failed to fetch exchange rate, HTTP response code: $responseCode")
                }
            } catch (e: Exception) {
                // Catch network errors, timeouts, or JSON parsing exceptions gracefully
                Log.e(TAG, "Error fetching exchange rate", e)
            }
        }
    }
}
