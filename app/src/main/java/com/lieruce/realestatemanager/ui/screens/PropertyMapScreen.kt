package com.lieruce.realestatemanager.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lieruce.realestatemanager.ui.viewmodel.PropertyViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

/**
 * PropertyMapScreen displays an interactive OpenStreetMap (osmdroid) 
 * showing markers for all properties in the Room database centered around New York,
 * complete with a bottom navigation bar to switch between List, Map, and Search.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyMapScreen(
    viewModel: PropertyViewModel,
    onPropertyClick: (Long) -> Unit,
    onListClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Observe all properties from the ViewModel reactively
    val properties by viewModel.allProperties.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Property Map") })
        },
        bottomBar = {
            // Bottom navigation bar with Map selected
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "List") },
                    label = { Text("List") },
                    selected = false,
                    onClick = onListClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Place, contentDescription = "Map") },
                    label = { Text("Map") },
                    selected = true,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    label = { Text("Search") },
                    selected = false,
                    onClick = onSearchClick
                )
            }
        }
    ) { padding ->
        // AndroidView bridges traditional osmdroid MapView into Jetpack Compose
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    // Use standard OpenStreetMap tiles
                    setTileSource(TileSourceFactory.MAPNIK)
                    // Enable pinch-to-zoom and multi-touch gestures
                    setMultiTouchControls(true)
                    // Center map on New York City by default with a good overview zoom level
                    controller.setZoom(12.0)
                    controller.setCenter(GeoPoint(40.7128, -74.0060)) // New York City coordinates
                }
            },
            update = { mapView ->
                // Clear previous overlays before adding updated markers
                mapView.overlays.clear()

                // Add a marker for each property that has valid GPS coordinates
                for (propertyWithRelations in properties) {
                    val property = propertyWithRelations.property
                    val lat = property.location.latitude
                    val lon = property.location.longitude

                    if (lat != null && lon != null) {
                        val point = GeoPoint(lat, lon)
                        val marker = Marker(mapView).apply {
                            position = point
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = property.type
                            snippet = "$${property.priceInDollars} - ${property.location.address}"
                            
                            // Set listener to navigate to property detail when marker is tapped
                            setOnMarkerClickListener { clickedMarker, _ ->
                                clickedMarker.showInfoWindow()
                                onPropertyClick(property.id)
                                true
                            }
                        }
                        mapView.overlays.add(marker)
                    }
                }
                mapView.invalidate()
            },
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}
