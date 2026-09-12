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
    // Form State
    var type by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var surface by remember { mutableStateOf("") }
    var rooms by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var pois by remember { mutableStateOf("") }
    var agent by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(PropertyStatus.AVAILABLE) }

    // If editing, load initial data
    val existingProperty by if (propertyId != null) {
        viewModel.getProperty(propertyId).collectAsStateWithLifecycle(initialValue = null)
    } else {
        remember { mutableStateOf(null) }
    }

    LaunchedEffect(existingProperty) {
        existingProperty?.let { p ->
            type = p.property.type
            price = p.property.priceInDollars.toString()
            surface = p.property.surfaceInSqm.toString()
            rooms = p.property.numberOfRooms.toString()
            description = p.property.description
            address = p.property.address
            pois = p.property.pointsOfInterest
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
                        val newItem = RealEstateItem(
                            id = propertyId ?: 0L,
                            type = type,
                            priceInDollars = price.toIntOrNull() ?: 0,
                            surfaceInSqm = surface.toIntOrNull() ?: 0,
                            numberOfRooms = rooms.toIntOrNull() ?: 0,
                            description = description,
                            address = address,
                            pointsOfInterest = pois,
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
            item {
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type (Flat, House, etc.)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
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
            item {
                OutlinedTextField(
                    value = rooms,
                    onValueChange = { if (it.all { char -> char.isDigit() }) rooms = it },
                    label = { Text("Number of Rooms") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
            item {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = pois,
                    onValueChange = { pois = it },
                    label = { Text("Points of Interest (Schools, Parks...)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = agent,
                    onValueChange = { agent = it },
                    label = { Text("Real Estate Agent") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
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
