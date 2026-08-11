package com.lieruce.realestatemanager.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lieruce.realestatemanager.data.model.PropertyWithPictures
import com.lieruce.realestatemanager.ui.viewmodel.PropertyViewModel

/**
 * Compose 101: LazyColumn
 * This is the modern replacement for RecyclerView. It only renders items that are 
 * visible on the screen, making it very fast.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyListScreen(
    viewModel: PropertyViewModel,
    onPropertyClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // collectAsStateWithLifecycle: This is the "Adapter" that connects the Flow
    // from the ViewModel to our UI. If the DB changes, this 'properties' list 
    // will update and the screen will "Recompose".
    val properties by viewModel.allProperties.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Real Estate Manager") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.addTestProperty() }) {
                Icon(Icons.Default.Add, contentDescription = "Add Test")
            }
        }
    ) { padding ->
        if (properties.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No properties found. Click + to add test data.")
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(properties) { propertyWithPictures ->
                    PropertyItem(
                        propertyWithPictures = propertyWithPictures,
                        onClick = { onPropertyClick(propertyWithPictures.property.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PropertyItem(
    propertyWithPictures: PropertyWithPictures,
    onClick: () -> Unit
) {
    val property = propertyWithPictures.property
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = property.type, style = MaterialTheme.typography.titleLarge)
            Text(text = "$${property.priceInDollars}", style = MaterialTheme.typography.bodyLarge)
            Text(text = property.address, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
