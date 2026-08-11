package com.lieruce.realestatemanager.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AddEditPropertyScreen(
    propertyId: Long? = null,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(if (propertyId == null) "Add Property Screen (Stub)" else "Edit Property Screen for ID: $propertyId (Stub)")
    }
}
