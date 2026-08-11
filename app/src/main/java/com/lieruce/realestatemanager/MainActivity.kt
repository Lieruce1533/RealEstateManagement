package com.lieruce.realestatemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.lieruce.realestatemanager.data.AppDatabase
import com.lieruce.realestatemanager.data.PropertyRepository
import com.lieruce.realestatemanager.ui.navigation.RealEstateNavGraph
import com.lieruce.realestatemanager.ui.theme.RealEstateManagerTheme
import com.lieruce.realestatemanager.ui.viewmodel.PropertyViewModel
import com.lieruce.realestatemanager.ui.viewmodel.PropertyViewModelFactory

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { PropertyRepository(database.propertyDao()) }
    
    private val viewModel: PropertyViewModel by viewModels {
        PropertyViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RealEstateManagerTheme {
                RealEstateNavGraph(viewModel = viewModel)
            }
        }
    }
}
