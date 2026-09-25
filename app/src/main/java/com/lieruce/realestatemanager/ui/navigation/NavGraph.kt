package com.lieruce.realestatemanager.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.lieruce.realestatemanager.ui.screens.*
import com.lieruce.realestatemanager.ui.viewmodel.PropertyViewModel

/**
 * Navigation 3 graph defining screen routing and adaptive layouts.
 * Uses type-safe NavKey objects and supports side-by-side list-detail views on tablets,
 * automatically pre-selecting the first property on wide screens (>= 600dp).
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun RealEstateNavGraph(
    viewModel: PropertyViewModel,
    modifier: Modifier = Modifier,
) {
    // The navigation backstack is retained in the ViewModel so it survives screen rotation
    val backStack = viewModel.backStack
    
    // Adaptive scene strategy that automatically shows list and detail side-by-side on wide screens (tablets/foldables)
    val listDetailStrategy = rememberListDetailSceneStrategy<Any>()
    
    // Observe all properties to check when they load from the database
    val properties by viewModel.allProperties.collectAsStateWithLifecycle()

    // Get current window container size and density to calculate width in DP reactively without configuration warnings
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val screenWidthDp = with(density) { windowInfo.containerSize.width.toDp() }
    val isWideScreen = screenWidthDp >= 600.dp

    // On wide screens (tablets), automatically pre-select the first property on startup so the detail pane isn't empty
    LaunchedEffect(properties, isWideScreen) {
        if (isWideScreen && properties.isNotEmpty() && backStack.size == 1 && backStack.first() == NavKey.PropertyList) {
            val firstId = properties.first().property.id
            val detailKey = NavKey.PropertyDetail(firstId)
            if (!backStack.contains(detailKey)) {
                backStack.add(detailKey)
            }
        }
    }
    
    // NavDisplay renders the current screen based on the backstack
    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.size - 1) },
        sceneStrategies = listOf(listDetailStrategy),
        entryProvider = entryProvider {
            // 1. Property List Screen (Master pane) with bottom navigation to Map & Search and settings action
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
                    },
                    onSettingsClick = {
                        backStack.add(NavKey.Settings)
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
            
            // 4. Add or Edit Property Screen (Detail pane)
            entry<NavKey.AddEditProperty>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { key ->
                AddEditPropertyScreen(
                    propertyId = key.propertyId,
                    viewModel = viewModel,
                    onBackClick = { backStack.removeAt(backStack.size - 1) }
                )
            }
            
            // 5. Search Screen with bottom navigation to List & Map
            entry<NavKey.Search> {
                SearchScreen(
                    viewModel = viewModel,
                    onPropertyClick = { id ->
                        // Restore master-detail split when selecting a property from search
                        backStack.clear()
                        backStack.add(NavKey.PropertyList)
                        backStack.add(NavKey.PropertyDetail(id))
                    },
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

            // 6. Presentation Settings Screen
            entry<NavKey.Settings> {
                SettingsScreen(
                    viewModel = viewModel,
                    onBackClick = { backStack.removeAt(backStack.size - 1) }
                )
            }
        }
    )
}
