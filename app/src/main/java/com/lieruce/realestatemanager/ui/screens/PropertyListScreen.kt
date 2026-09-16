package com.lieruce.realestatemanager.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.lieruce.realestatemanager.R
import com.lieruce.realestatemanager.data.model.PropertyStatus
import com.lieruce.realestatemanager.data.model.PropertyWithPictures
import com.lieruce.realestatemanager.data.model.RealEstateItem
import com.lieruce.realestatemanager.ui.theme.RealEstateManagerTheme
import com.lieruce.realestatemanager.ui.viewmodel.PropertyViewModel

/**
 * Stateful wrapper for PropertyListScreen.
 * It connects to the ViewModel to observe the live Flow of properties from the Room database.
 */
@Composable
fun PropertyListScreen(
    viewModel: PropertyViewModel,
    onPropertyClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // collectAsStateWithLifecycle connects the ViewModel's Flow to Compose state.
    // Whenever database records change, this list updates and triggers recomposition.
    val properties by viewModel.allProperties.collectAsStateWithLifecycle()

    // Delegate UI rendering to the stateless content composable so it can be previewed
    PropertyListContent(
        properties = properties,
        onPropertyClick = onPropertyClick,
        onAddClick = onAddClick,
        modifier = modifier
    )
}

/**
 * Stateless UI content for PropertyListScreen.
 * Purely renders data passed to it without knowing about ViewModels or Databases,
 * which makes it fully previewable in Android Studio.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyListContent(
    properties: List<PropertyWithPictures>,
    onPropertyClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Real Estate Manager") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Property")
            }
        }
    ) { padding ->
        // Handle empty state vs list display
        if (properties.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No properties found. Click + to add test data.")
            }
        } else {
            // Compose 101: LazyColumn is the modern replacement for RecyclerView.
            // It only renders items currently visible on screen for high performance.
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
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

/**
 * Card item representing an individual real estate property in the list,
 * displaying its thumbnail image and core details side-by-side in a Row.
 */
@Composable
fun PropertyItem(
    propertyWithPictures: PropertyWithPictures,
    onClick: () -> Unit
) {
    val property = propertyWithPictures.property
    
    // Grab the URI of the first picture if available, or null otherwise
    val firstPictureUri = propertyWithPictures.pictures.firstOrNull()?.uri
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        border = BorderStroke(1.dp, Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // Row places the thumbnail on the left and property details on the right side-by-side
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // AsyncImage from Coil loads local URIs (gallery/camera) or URLs asynchronously
            AsyncImage(
                model = firstPictureUri ?: "https://images.unsplash.com/photo-1564013799919-ab600027ffc6", // Fallback sample image if no picture attached yet
                contentDescription = "Property thumbnail",
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp)),
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.ic_launcher_background)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Property details column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = property.type, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${property.priceInDollars}", 
                    style = MaterialTheme.typography.bodyLarge, 
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = property.address, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// ============================================================================
// COMPOSE PREVIEWS
// ============================================================================

@Preview(showBackground = true, name = "Property List - With Data")
@Composable
fun PropertyListContentPreview() {
    RealEstateManagerTheme {
        PropertyListContent(
            properties = listOf(
                PropertyWithPictures(
                    property = RealEstateItem(
                        id = 1L,
                        type = "Manor",
                        priceInDollars = 1500000,
                        surfaceInSqm = 450,
                        numberOfRooms = 12,
                        description = "A historic manor in the countryside.",
                        address = "123 Castle Road, Loire Valley",
                        pointsOfInterest = "Park, School",
                        amenities = "Swimming Pool, Gym",
                        status = PropertyStatus.AVAILABLE,
                        entryDate = System.currentTimeMillis(),
                        agentName = "Agent Smith"
                    ),
                    pictures = emptyList()
                ),
                PropertyWithPictures(
                    property = RealEstateItem(
                        id = 2L,
                        type = "Penthouse",
                        priceInDollars = 950000,
                        surfaceInSqm = 180,
                        numberOfRooms = 5,
                        description = "Luxury downtown penthouse with panoramic views.",
                        address = "456 Skyline Ave, Metropolis",
                        pointsOfInterest = "Subway, Shopping Mall",
                        amenities = "Balcony, Terrace, Security System",
                        status = PropertyStatus.SOLD,
                        entryDate = System.currentTimeMillis(),
                        agentName = "Agent Jane",
                        saleDate = System.currentTimeMillis()
                    ),
                    pictures = emptyList()
                )
            ),
            onPropertyClick = {},
            onAddClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Property List - Empty State")
@Composable
fun PropertyListEmptyPreview() {
    RealEstateManagerTheme {
        PropertyListContent(
            properties = emptyList(),
            onPropertyClick = {},
            onAddClick = {}
        )
    }
}
