package com.lieruce.realestatemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.lieruce.realestatemanager.data.AppDatabase
import com.lieruce.realestatemanager.data.CurrencyRepository
import com.lieruce.realestatemanager.data.LocationRepository
import com.lieruce.realestatemanager.data.PropertyRepository
import com.lieruce.realestatemanager.ui.navigation.RealEstateNavGraph
import com.lieruce.realestatemanager.ui.theme.RealEstateManagerTheme
import com.lieruce.realestatemanager.ui.viewmodel.PropertyViewModel
import com.lieruce.realestatemanager.ui.viewmodel.PropertyViewModelFactory
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { PropertyRepository(database.propertyDao()) }
    
    // Instantiate our LocationRepository to handle geocoding addresses to GPS coordinates
    private val locationRepository by lazy { LocationRepository(this) }
    
    // Instantiate our CurrencyRepository to fetch live rates
    private val currencyRepository by lazy { CurrencyRepository() }
    
    // Provide repository and locationRepository to ViewModel via factory
    private val viewModel: PropertyViewModel by viewModels {
        PropertyViewModelFactory(repository, locationRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // osmdroid configuration
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        
        // Fetch fresh currency exchange rates in the background when the app launches
        lifecycleScope.launch {
            currencyRepository.fetchAndUpdateExchangeRate()
        }
        
        enableEdgeToEdge()
        setContent {
            RealEstateManagerTheme {
                RealEstateNavGraph(viewModel = viewModel)
            }
        }
    }
}
