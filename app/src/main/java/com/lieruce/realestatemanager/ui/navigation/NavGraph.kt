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
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.lieruce.realestatemanager.ui.screens.*
import com.lieruce.realestatemanager.ui.viewmodel.PropertyViewModel

/**
 * Navigation 3 graph defining screen routing and adaptive layouts.
 * Uses type-safe NavKey objects and supports side-by-side list-detail views on tablets.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun RealEstateNavGraph(
    viewModel: PropertyViewModel,
    modifier: Modifier = Modifier,
) {
    // The navigation backstack storing active navigation keys (starting with PropertyList)
    val backStack = remember { mutableStateListOf<Any>(NavKey.PropertyList) }
    
    // Adaptive scene strategy that automatically shows list and detail side-by-side on wide screens (tablets/foldables)
    val listDetailStrategy = rememberListDetailSceneStrategy<Any>()
    
    // NavDisplay renders the current screen based on the backstack
    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
        sceneStrategies = listOf(listDetailStrategy),
        entryProvider = entryProvider {
            // 1. Property List Screen (Master pane) with bottom navigation to Map & Search
            entry<NavKey.PropertyList>(
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
                        val detailKey = NavKey.PropertyDetail(id)
                        // Prevent duplicate entries in backstack
                        if (backStack.lastOrNull() != detailKey) {
                            backStack.add(detailKey)
                        }
                    },
                    onAddClick = {
                        backStack.add(NavKey.AddEditProperty())
                    },
                    onMapClick = {
                        backStack.clear()
                        backStack.add(NavKey.PropertyMap)
                    },
                    onSearchClick = {
                        backStack.clear()
                        backStack.add(NavKey.Search)
                    }
                )
            }
            
            // 2. Property Detail Screen (Detail pane)
            entry<NavKey.PropertyDetail>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { key ->
                PropertyDetailScreen(
                    propertyId = key.propertyId,
                    viewModel = viewModel,
                    onBackClick = { backStack.removeAt(backStack.size - 1) },
                    onEditClick = { id ->
                        backStack.add(NavKey.AddEditProperty(id))
                    }
                )
            }
            
            // 3. Map Screen with bottom navigation to List & Search
            entry<NavKey.PropertyMap> {
                PropertyMapScreen(
                    viewModel = viewModel,
                    onPropertyClick = { id ->
                        val detailKey = NavKey.PropertyDetail(id)
                        if (backStack.lastOrNull() != detailKey) {
                            backStack.add(detailKey)
                        }
                    },
                    onListClick = {
                        backStack.clear()
                        backStack.add(NavKey.PropertyList)
                    },
                    onSearchClick = {
                        backStack.clear()
                        backStack.add(NavKey.Search)
                    }
                )
            }
            
            // 4. Add or Edit Property Screen
            entry<NavKey.AddEditProperty> { key ->
                AddEditPropertyScreen(
                    propertyId = key.propertyId,
                    viewModel = viewModel,
                    onBackClick = { backStack.removeAt(backStack.size - 1) }
                )
            }
            
            // 5. Search Screen with bottom navigation to List & Map
            entry<NavKey.Search> {
                SearchScreen(
                    onListClick = {
                        backStack.clear()
                        backStack.add(NavKey.PropertyList)
                    },
                    onMapClick = {
                        backStack.clear()
                        backStack.add(NavKey.PropertyMap)
                    }
                )
            }
        }
    )
}
