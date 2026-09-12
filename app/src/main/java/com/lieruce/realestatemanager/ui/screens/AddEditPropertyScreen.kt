package com.lieruce.realestatemanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lieruce.realestatemanager.data.model.PropertyConstants
import com.lieruce.realestatemanager.data.model.PropertyStatus
import com.lieruce.realestatemanager.data.model.RealEstateItem
import com.lieruce.realestatemanager.ui.viewmodel.PropertyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPropertyScreen(
    propertyId: Long? = null,
    viewModel: PropertyViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
    
    var agent by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(PropertyStatus.AVAILABLE) }
    
    // Control state for the property type dropdown menu
    var typeExpanded by remember { mutableStateOf(false) }

    // If editing, load initial data from Room database
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
            address = p.property.address
            
            // Convert comma-separated string back into a Set of strings for chip selection
            selectedPois = p.property.pointsOfInterest.split(",").map { it.trim() }.filter { it.isNotBlank() }.toSet()
            selectedAmenities = p.property.amenities.split(",").map { it.trim() }.filter { it.isNotBlank() }.toSet()
            
            agent = p.property.agentName
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
                        // Build the RealEstateItem entity with standardized dropdown type and joined comma-separated strings for POIs and amenities
                        val newItem = RealEstateItem(
                            id = propertyId ?: 0L,
                            type = type,
                            priceInDollars = price.toIntOrNull() ?: 0,
                            surfaceInSqm = surface.toIntOrNull() ?: 0,
                            numberOfRooms = rooms.toIntOrNull() ?: 0,
                            description = description,
                            address = address,
                            pointsOfInterest = selectedPois.joinToString(", "),
                            amenities = selectedAmenities.joinToString(", "),
                            status = status,
                            entryDate = existingProperty?.property?.entryDate ?: System.currentTimeMillis(),
                            agentName = agent
                        )
                        viewModel.saveProperty(newItem, emptyList())
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
            
            // Agent name
            item {
                OutlinedTextField(
                    value = agent,
                    onValueChange = { agent = it },
                    label = { Text("Real Estate Agent") },
                    modifier = Modifier.fillMaxWidth()
                )
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
