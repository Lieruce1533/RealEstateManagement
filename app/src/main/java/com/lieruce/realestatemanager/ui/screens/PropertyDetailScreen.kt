package com.lieruce.realestatemanager.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.lieruce.realestatemanager.data.model.PropertyPicture
import com.lieruce.realestatemanager.data.model.PropertyStatus
import com.lieruce.realestatemanager.ui.viewmodel.PropertyViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * PropertyDetailScreen displays the complete details of a selected real estate property,
 * including its photo carousel (with tap-to-zoom full screen landscape-ready swipe pager),
 * price, surface, description, osmdroid map location, normalized amenities, POIs, and agent info.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(
    propertyId: Long,
    viewModel: PropertyViewModel,
    onBackClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // Observe the specific property with all its normalized relations reactively from ViewModel
    val propertyWithRelations by viewModel.getProperty(propertyId).collectAsStateWithLifecycle(initialValue = null)

    // State to track the index of the picture currently tapped for full-screen zoom inspection
    var zoomedPictureIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Property Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEditClick(propertyId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { padding ->
        val data = propertyWithRelations
        if (data == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val property = data.property
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Photo Carousel feeding property pictures or showing a fallback sample image
                item {
                    val pictures = data.pictures
                    if (pictures.isEmpty()) {
                        // Fallback sample image card if no pictures are attached yet
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clickable { 
                                    zoomedPictureIndex = 0
                                },
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = "https://images.unsplash.com/photo-1564013799919-ab600027ffc6",
                                    contentDescription = "Property fallback photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Surface(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Exterior View (Tap to enlarge)",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        // Horizontal scrolling photo carousel powered by Coil's AsyncImage
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(pictures.indices.toList()) { index ->
                                val picture = pictures[index]
                                Card(
                                    modifier = Modifier
                                        .size(width = 280.dp, height = 200.dp)
                                        .clickable { zoomedPictureIndex = index }, // Tap card to open full-screen zoom pager at this index
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        AsyncImage(
                                            model = picture.uri,
                                            contentDescription = picture.description ?: "Property photo",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        // Optional caption overlay at the bottom of the photo card
                                        if (!picture.description.isNullOrBlank()) {
                                            Surface(
                                                color = Color.Black.copy(alpha = 0.6f),
                                                modifier = Modifier
                                                    .align(Alignment.BottomCenter)
                                                    .fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = picture.description,
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier.padding(8.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Column {
                        Text(
                            text = property.type,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = NumberFormat.getCurrencyInstance(Locale.US).format(property.priceInDollars),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoChip(label = "Surface", value = "${property.surfaceInSqm} m²")
                        InfoChip(label = "Rooms", value = "${property.numberOfRooms}")
                        StatusChip(status = property.status)
                    }
                }

                item {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = property.description,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    Text(
                        text = "Location",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = property.location.address,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Real Map Implementation
                    if (property.location.latitude != null && property.location.longitude != null) {
                        PropertyMap(
                            latitude = property.location.latitude,
                            longitude = property.location.longitude,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .background(Color.LightGray, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No GPS coordinates available", color = Color.DarkGray)
                        }
                    }
                }

                // Display normalized Points of Interest
                item {
                    Text(
                        text = "Nearby",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = data.pois.joinToString(", ") { it.name },
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Display normalized property amenities if any are provided
                if (data.amenities.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Amenities",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = data.amenities.joinToString(", ") { it.name },
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                item {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    PropertyMetadataRow(label = "Agent", value = data.agent?.name ?: "Unknown")
                    PropertyMetadataRow(label = "Entry Date", value = formatDate(property.entryDate))
                    if (property.status == PropertyStatus.SOLD && property.saleDate != null) {
                        PropertyMetadataRow(label = "Sale Date", value = formatDate(property.saleDate))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Full-screen landscape-ready zoom dialog with HorizontalPager for swiping through photos
    zoomedPictureIndex?.let { initialIndex ->
        val rawPictures = propertyWithRelations?.pictures ?: emptyList()
        val pictures = if (rawPictures.isEmpty()) {
            listOf(PropertyPicture(propertyId = propertyId, uri = "https://images.unsplash.com/photo-1564013799919-ab600027ffc6", description = "Exterior View"))
        } else {
            rawPictures
        }

        val pagerState = rememberPagerState(
            initialPage = initialIndex.coerceIn(0, pictures.size - 1),
            pageCount = { pictures.size }
        )

        Dialog(
            onDismissRequest = { zoomedPictureIndex = null },
            properties = DialogProperties(usePlatformDefaultWidth = false) // Allow full-screen utilization
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                // HorizontalPager enables smooth left/right swiping between property images
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val picture = pictures[page]
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = picture.uri,
                            contentDescription = picture.description ?: "Enlarged property photo",
                            contentScale = ContentScale.Fit, // Proportional scaling ready for both portrait & landscape
                            modifier = Modifier.fillMaxSize()
                        )

                        // Caption overlay and photo counter at the bottom
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (!picture.description.isNullOrBlank()) {
                                Text(
                                    text = picture.description,
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Text(
                                text = "Photo ${page + 1} of ${pictures.size}",
                                color = Color.LightGray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Close button in top right corner
                IconButton(
                    onClick = { zoomedPictureIndex = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(48.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                ) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Close full-screen view",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Compose 101: AndroidView (View Interop)
 * osmdroid is a traditional Android View. To use it in Compose, we use 'AndroidView'.
 * This acts as a bridge that allows us to host any old-school View inside a Composable function.
 */
@Composable
fun PropertyMap(
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(15.0)
            }
        },
        update = { view ->
            val point = GeoPoint(latitude, longitude)
            view.controller.setCenter(point)
            
            // Add a marker
            view.overlays.clear()
            val marker = Marker(view)
            marker.position = point
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            view.overlays.add(marker)
            view.invalidate()
        },
        modifier = modifier
    )
}

@Composable
fun InfoChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StatusChip(status: PropertyStatus) {
    val color = if (status == PropertyStatus.AVAILABLE) Color(0xFF4CAF50) else Color.Red
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, color)
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            color = color,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PropertyMetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

/**
 * Formats a java.time.Instant into a clean human-readable date string.
 */
fun formatDate(instant: Instant): String {
    val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.getDefault())
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}
