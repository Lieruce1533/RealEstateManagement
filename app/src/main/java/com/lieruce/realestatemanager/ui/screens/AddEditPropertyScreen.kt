package com.lieruce.realestatemanager.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.lieruce.realestatemanager.data.model.PropertyConstants
import com.lieruce.realestatemanager.data.model.PropertyLocation
import com.lieruce.realestatemanager.data.model.PropertyPicture
import com.lieruce.realestatemanager.data.model.PropertyStatus
import com.lieruce.realestatemanager.data.model.RealEstateItem
import com.lieruce.realestatemanager.ui.viewmodel.PropertyViewModel
import com.lieruce.realestatemanager.util.ImageManager
import java.time.Instant

/**
 * State holder representing an editable property picture with its local file path and description caption.
 */
data class EditPictureState(
    val uri: String,
    val description: String = ""
)

/**
 * AddEditPropertyScreen allows agents to create new real estate properties or edit existing ones,
 * including selecting multiple property photos, editing their descriptions, and saving everything securely.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPropertyScreen(
    propertyId: Long? = null,
    viewModel: PropertyViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Form state variables initialized with defaults or empty values
    var type by remember { mutableStateOf(PropertyConstants.PROPERTY_TYPES.first()) }
    var price by remember { mutableStateOf("") }
    var surface by remember { mutableStateOf("") }
    var rooms by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    
    // Using Sets for selected POIs and Amenities to easily toggle multi-select filter chips
    var selectedPois by remember { mutableStateOf(setOf<String>()) }
    var selectedAmenities by remember { mutableStateOf(setOf<String>()) }
    
    // State list holding editable picture states (URI + description caption)
    val selectedPictures = remember { mutableStateListOf<EditPictureState>() }
    
    var status by remember { mutableStateOf(PropertyStatus.AVAILABLE) }
    
    // Control state for the property type dropdown menu
    var typeExpanded by remember { mutableStateOf(false) }

    // System gallery content multi-selector launcher to pick multiple photos at once
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { sourceUri ->
            // Copy and compress each image into internal app storage via ImageManager
            val internalPath = ImageManager.saveImageToInternalStorage(context, sourceUri)
            if (internalPath != null) {
                selectedPictures.add(EditPictureState(uri = internalPath, description = ""))
            }
        }
    }

    // If editing, load initial data from Room database (PropertyWithRelations)
    val existingProperty by if (propertyId != null) {
        viewModel.getProperty(propertyId).collectAsStateWithLifecycle(initialValue = null)
    } else {
        remember { mutableStateOf(null) }
    }

    // Populate form fields when existing property data is loaded
    LaunchedEffect(existingProperty) {
        existingProperty?.let { p ->
            type = p.property.type.ifBlank { PropertyConstants.PROPERTY_TYPES.first() }
            price = p.property.priceInDollars.toString()
            surface = p.property.surfaceInSqm.toString()
            rooms = p.property.numberOfRooms.toString()
            description = p.property.description
            address = p.property.location.address
            
            // Convert normalized POIs and amenities lists into Sets of strings for chip selection
            selectedPois = p.pois.map { it.name }.toSet()
            selectedAmenities = p.amenities.map { it.name }.toSet()
            
            // Load existing pictures with their descriptions
            selectedPictures.clear()
            selectedPictures.addAll(p.pictures.map { EditPictureState(uri = it.uri, description = it.description ?: "") })
            
            status = p.property.status
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (propertyId == null) "New Property" else "Edit Property") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Check if the user modified the address string during editing
                        val addressChanged = existingProperty?.property?.location?.address != address
                        
                        // If address changed, clear latitude/longitude so ViewModel triggers fresh geocoding.
                        // Otherwise, preserve existing GPS coordinates.
                        val latToSave = if (addressChanged) null else existingProperty?.property?.location?.latitude
                        val lonToSave = if (addressChanged) null else existingProperty?.property?.location?.longitude

                        // Build the RealEstateItem entity
                        val newItem = RealEstateItem(
                            id = propertyId ?: 0L,
                            type = type,
                            priceInDollars = price.toIntOrNull() ?: 0,
                            surfaceInSqm = surface.toIntOrNull() ?: 0,
                            numberOfRooms = rooms.toIntOrNull() ?: 0,
                            description = description,
                            location = PropertyLocation(
                                address = address,
                                latitude = latToSave,
                                longitude = lonToSave
                            ),
                            status = status,
                            entryDate = existingProperty?.property?.entryDate ?: Instant.now(),
                            agentId = existingProperty?.property?.agentId ?: 1L // Default to Agent Smith (id = 1)
                        )
                        
                        // Map selected pictures and descriptions into PropertyPicture entities
                        val picturesList = selectedPictures.map { item ->
                            PropertyPicture(
                                propertyId = propertyId ?: 0L,
                                uri = item.uri,
                                description = item.description
                            )
                        }

                        viewModel.saveProperty(newItem, picturesList)
                        
                        // Show success Toast message
                        val toastMessage = if (propertyId == null) "New property successfully created!" else "Property updated successfully!"
                        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()

                        onBackClick()
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Photo Picker Section with multi-select and description inputs
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Property Photos (${selectedPictures.size})",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(onClick = { galleryLauncher.launch("image/*") }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Photos")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Photos")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    if (selectedPictures.isEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No photos added yet. Tap 'Add Photos' to select from gallery.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        // Horizontal scroll row showing selected photo thumbnails with description fields and delete buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            selectedPictures.forEachIndexed { index, item ->
                                Card(
                                    modifier = Modifier.width(160.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Box(modifier = Modifier.size(144.dp, 100.dp)) {
                                            AsyncImage(
                                                model = item.uri,
                                                contentDescription = "Selected photo",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(RoundedCornerShape(8.dp))
                                            )
                                            // Delete button overlay
                                            IconButton(
                                                onClick = { selectedPictures.removeAt(index) },
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .size(28.dp)
                                                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Remove photo",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        // Input field to add or edit picture description caption
                                        OutlinedTextField(
                                            value = item.description,
                                            onValueChange = { newDesc ->
                                                selectedPictures[index] = item.copy(description = newDesc)
                                            },
                                            label = { Text("Caption") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Standardized Property Type Dropdown Menu
            item {
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Property Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        PropertyConstants.PROPERTY_TYPES.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    type = option
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            
            // Price and Surface input row
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { if (it.all { char -> char.isDigit() }) price = it },
                        label = { Text("Price ($)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = surface,
                        onValueChange = { if (it.all { char -> char.isDigit() }) surface = it },
                        label = { Text("Surface (m²)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
            
            // Number of rooms
            item {
                OutlinedTextField(
                    value = rooms,
                    onValueChange = { if (it.all { char -> char.isDigit() }) rooms = it },
                    label = { Text("Number of Rooms") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            
            // Description
            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
            
            // Address
            item {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Standardized Points of Interest selection via Filter Chips
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Nearby Points of Interest",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        PropertyConstants.AVAILABLE_POIS.forEach { poi ->
                            FilterChip(
                                selected = selectedPois.contains(poi),
                                onClick = {
                                    selectedPois = if (selectedPois.contains(poi)) {
                                        selectedPois - poi
                                    } else {
                                        selectedPois + poi
                                    }
                                },
                                label = { Text(poi) }
                            )
                        }
                    }
                }
            }
            
            // Standardized Amenities selection via Filter Chips
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Property Amenities",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        PropertyConstants.AVAILABLE_AMENITIES.forEach { amenity ->
                            FilterChip(
                                selected = selectedAmenities.contains(amenity),
                                onClick = {
                                    selectedAmenities = if (selectedAmenities.contains(amenity)) {
                                        selectedAmenities - amenity
                                    } else {
                                        selectedAmenities + amenity
                                    }
                                },
                                label = { Text(amenity) }
                            )
                        }
                    }
                }
            }
            
            // Property Status selection (Available vs Sold)
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Status: ")
                    Spacer(modifier = Modifier.width(8.dp))
                    PropertyStatus.entries.forEach { s ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = (status == s),
                                onClick = { status = s }
                            )
                            Text(text = s.name, modifier = Modifier.padding(end = 8.dp))
                        }
                    }
                }
            }
        }
    }
}
