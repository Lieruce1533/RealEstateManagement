package com.lieruce.realestatemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.lieruce.realestatemanager.ui.navigation.RealEstateNavGraph
import com.lieruce.realestatemanager.ui.theme.RealEstateManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RealEstateManagerTheme {
                RealEstateNavGraph()
            }
        }
    }
}
