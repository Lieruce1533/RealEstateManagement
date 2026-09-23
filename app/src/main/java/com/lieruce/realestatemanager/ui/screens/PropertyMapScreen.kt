package com.lieruce.realestatemanager.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lieruce.realestatemanager.R
import com.lieruce.realestatemanager.Utils
import com.lieruce.realestatemanager.ui.viewmodel.PropertyViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

/**
 * PropertyMapScreen displays an interactive OpenStreetMap (osmdroid) 
 * showing property markers and the agent's live or simulated GPS location,
 * verifying internet connectivity and GPS services.
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
    val context = LocalContext.current

    // Check internet and GPS connectivity status
    val hasInternet = remember { Utils.isInternetAvailableNew(context) }
    val hasGps = remember { Utils.isGpsEnabled(context) }

    // State for runtime location permission
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Permission launcher to request location permission at runtime
    val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }

    // Request permission on first composition if not granted
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(LOCATION_PERMISSION)
        }
    }

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
        // Requirement Check: Both Internet and GPS must be active to display pins and agent position
        if (!hasInternet || !hasGps) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⚠️ Map Unavailable",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "To view property locations and your live position on the map, please ensure both Internet access and GPS Location services are enabled in your device settings.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // AndroidView bridges traditional osmdroid MapView into Jetpack Compose
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        // Use standard OpenStreetMap tiles
                        setTileSource(TileSourceFactory.MAPNIK)
                        // Enable pinch-to-zoom and multi-touch gestures
                        setMultiTouchControls(true)
                        // Center map on New York City by default with a good overview zoom level
                        controller.setZoom(13.0)
                        controller.setCenter(GeoPoint(40.7128, -74.0060)) // New York City coordinates

                        // If mock GPS is enabled in settings, center on NYC and add a simulated agent marker
                        if (viewModel.isMockGpsEnabled) {
                            val nycPoint = GeoPoint(40.7128, -74.0060)
                            controller.setCenter(nycPoint)
                        } else if (hasLocationPermission) {
                            // Otherwise use real device GPS location overlay
                            val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this).apply {
                                enableMyLocation()
                                enableFollowLocation()
                            }
                            overlays.add(myLocationOverlay)
                        }
                    }
                },
                update = { mapView ->
                    // Clear previous property and agent markers
                    val overlaysToRemove = mapView.overlays.filterIsInstance<Marker>()
                    mapView.overlays.removeAll(overlaysToRemove)

                    // If mock GPS is enabled, add simulated agent marker at NYC center
                    if (viewModel.isMockGpsEnabled) {
                        val nycPoint = GeoPoint(40.7128, -74.0060)
                        mapView.controller.setCenter(nycPoint)
                        val agentMarker = Marker(mapView).apply {
                            position = nycPoint
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = "Agent Position (Simulated)"
                            snippet = "New York, NY"
                            // Explicitly assign professional agent pin vector drawable to guarantee marker rendering
                            icon = ContextCompat.getDrawable(mapView.context, R.drawable.ic_agent_pin)
                        }
                        mapView.overlays.add(agentMarker)
                    }

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
}
