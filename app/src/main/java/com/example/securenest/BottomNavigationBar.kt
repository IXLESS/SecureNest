package com.example.securenest

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
@Composable
fun BottomNavigationBar(navController: NavHostController) {

    // Spremljamo trenutno navigacijsko stanje (kateri screen je aktiven)
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    // Spodnja navigacijska vrstica (Home – Settings – Trash)
    NavigationBar {

        // HOME BUTTON
        NavigationBarItem(
            selected = currentRoute == "home", // označi gumb kot izbran
            onClick = {
                // Navigacija na Home, z ohranjanjem stanja
                navController.navigate("home") {
                    // vrne se na začetno destinacijo, če je potrebno
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true      // prepreči podvajanje screenov
                    restoreState = true         // obnovi prejšnji layout
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("home") }
        )

        // SETTINGS BUTTON
        NavigationBarItem(
            selected = currentRoute == "settings",
            onClick = {
                navController.navigate("settings") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("settings") }
        )

        // TRASH BUTTON
        NavigationBarItem(
            selected = currentRoute == "trash",
            onClick = {
                navController.navigate("trash") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.Delete, contentDescription = "Trash") },
            label = { Text("trash") }
        )
    }
}
