package com.lieruce.realestatemanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lieruce.realestatemanager.data.model.PropertyConstants
import com.lieruce.realestatemanager.ui.viewmodel.PropertyViewModel

/**
 * SearchScreen allows agents to filter real estate properties by type, agent, price range,
 * surface area, required amenities, and required points of interest, displaying live matching search results.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: PropertyViewModel,
    onPropertyClick: (Long) -> Unit,
    onListClick: () -> Unit,
    onMapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Observe all properties and agents from ViewModel reactively
    val allProperties by viewModel.allProperties.collectAsStateWithLifecycle()
    val allAgents by viewModel.allAgents.collectAsStateWithLifecycle()
    
    // Compute filtered properties using ViewModel filter states
    val filteredProperties = viewModel.getFilteredProperties(allProperties)

    // Control states for dropdown menus
    var typeExpanded by remember { mutableStateOf(false) }
    var agentExpanded by remember { mutableStateOf(false) }

    // Find selected agent name for display in the dropdown
    val ALL_AGENTS_LABEL = "All Agents"
    val selectedAgentName = allAgents.find { it.id == viewModel.searchAgentId }?.name ?: ALL_AGENTS_LABEL

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search & Filter (${filteredProperties.size} results)") },
                actions = {
                    // Action button to clear all active search filters
                    IconButton(onClick = { viewModel.clearFilters() }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear Filters")
                    }
                }
            )
        },
        bottomBar = {
            // Bottom navigation bar with Search selected
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
                    selected = false,
                    onClick = onMapClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    label = { Text("Search") },
                    selected = true,
                    onClick = {}
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Property Type Dropdown Filter
            item {
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = viewModel.searchType ?: "All Types",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Filter by Property Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Types") },
                            onClick = {
                                viewModel.searchType = null
                                typeExpanded = false
                            }
                        )
                        PropertyConstants.PROPERTY_TYPES.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    viewModel.searchType = option
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // 2. Agent Filter Dropdown
            item {
                ExposedDropdownMenuBox(
                    expanded = agentExpanded,
                    onExpandedChange = { agentExpanded = !agentExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedAgentName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Filter by Agent") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = agentExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                    )
                    ExposedDropdownMenu(
                        expanded = agentExpanded,
                        onDismissRequest = { agentExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(ALL_AGENTS_LABEL) },
                            onClick = {
                                viewModel.searchAgentId = null
                                agentExpanded = false
                            }
                        )
                        allAgents.forEach { agent ->
                            DropdownMenuItem(
                                text = { Text(agent.name) },
                                onClick = {
                                    viewModel.searchAgentId = agent.id
                                    agentExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // 3. Price Range Filter Inputs
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = viewModel.searchMinPrice,
                        onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.searchMinPrice = it },
                        label = { Text("Min Price ($)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = viewModel.searchMaxPrice,
                        onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.searchMaxPrice = it },
                        label = { Text("Max Price ($)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            // 4. Surface Range Filter Inputs
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = viewModel.searchMinSurface,
                        onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.searchMinSurface = it },
                        label = { Text("Min Surface (m²)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = viewModel.searchMaxSurface,
                        onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.searchMaxSurface = it },
                        label = { Text("Max Surface (m²)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            // 5. Required Amenities Filter Chips
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Required Amenities",
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
                                selected = viewModel.searchAmenities.contains(amenity),
                                onClick = {
                                    if (viewModel.searchAmenities.contains(amenity)) {
                                        viewModel.searchAmenities.remove(amenity)
                                    } else {
                                        viewModel.searchAmenities.add(amenity)
                                    }
                                },
                                label = { Text(amenity) }
                            )
                        }
                    }
                }
            }

            // 6. Required Points of Interest Filter Chips
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Required Points of Interest",
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
                                selected = viewModel.searchPois.contains(poi),
                                onClick = {
                                    if (viewModel.searchPois.contains(poi)) {
                                        viewModel.searchPois.remove(poi)
                                    } else {
                                        viewModel.searchPois.add(poi)
                                    }
                                },
                                label = { Text(poi) }
                            )
                        }
                    }
                }
            }

            // 7. Results Header
            item {
                Text(
                    text = "Matching Properties (${filteredProperties.size})",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // 8. Filtered Results List using PropertyItem
            items(filteredProperties) { propertyWithRelations ->
                PropertyItem(
                    propertyWithRelations = propertyWithRelations,
                    onClick = { onPropertyClick(propertyWithRelations.property.id) }
                )
            }
        }
    }
}
