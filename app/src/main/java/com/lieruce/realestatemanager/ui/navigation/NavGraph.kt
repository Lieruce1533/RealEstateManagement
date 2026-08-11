package com.lieruce.realestatemanager.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.lieruce.realestatemanager.ui.screens.*
import com.lieruce.realestatemanager.ui.viewmodel.PropertyViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun RealEstateNavGraph(
    viewModel: PropertyViewModel,
    modifier: Modifier = Modifier,
) {
    val backStack = remember { mutableStateListOf<NavKey>(NavKey.PropertyList) }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
    
    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
        sceneStrategy = listDetailStrategy
    ) { key ->
        when (key) {
            is NavKey.PropertyList -> {
                NavEntry(
                    key = key,
                    metadata = ListDetailSceneStrategy.listPane(
                        detailPlaceholder = {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Select a property from the list")
                            }
                        }
                    )
                ) {
                    PropertyListScreen(
                        viewModel = viewModel,
                        onPropertyClick = { id ->
                            // If the key is already in the backstack, we don't add it again (simple logic)
                            if (backStack.lastOrNull() != NavKey.PropertyDetail(id)) {
                                backStack.add(NavKey.PropertyDetail(id))
                            }
                        }
                    )
                }
            }
            is NavKey.PropertyDetail -> {
                NavEntry(
                    key = key,
                    metadata = ListDetailSceneStrategy.detailPane()
                ) {
                    PropertyDetailScreen(
                        propertyId = key.propertyId,
                        viewModel = viewModel,
                        onBackClick = { backStack.removeAt(backStack.size - 1) },
                        onEditClick = { id ->
                            backStack.add(NavKey.AddEditProperty(id))
                        }
                    )
                }
            }
            is NavKey.PropertyMap -> {
                NavEntry(key = key) {
                    PropertyMapScreen()
                }
            }
            is NavKey.AddEditProperty -> {
                NavEntry(key = key) {
                    AddEditPropertyScreen(propertyId = key.propertyId)
                }
            }
            is NavKey.Search -> {
                NavEntry(key = key) {
                    SearchScreen()
                }
            }
        }
    }
}
