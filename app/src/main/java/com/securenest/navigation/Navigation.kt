package com.securenest.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.securenest.ThemeMode
import com.example.securenest.pages.AddPasswordScreen
import com.example.securenest.pages.GeneratePassword
import com.example.securenest.pages.HomeScreen
import com.example.securenest.pages.LoginScreen
import com.example.securenest.pages.PasswordDetailsScreen
import com.example.securenest.pages.Settings
import com.example.securenest.pages.Trash
import com.example.securenest.pages.WelcomeScreen

@Composable
fun AppNavHost(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    navController: NavHostController = rememberNavController()
) {
    // Glavna navigacijska struktura aplikacije.
    // NavHost določa startDestination in deklarira vse screens.
    NavHost(navController = navController, startDestination = "login") {

        // Login screen
        composable("login") { LoginScreen(navController) }

        // Welcome screen po uspešni prijavi
        composable("welcome") { WelcomeScreen(navController) }

        // Glavni seznam shranjenih gesel
        composable("home") { HomeScreen(navController) }

        // Dodajanje novega gesla
        composable("create") { AddPasswordScreen(navController) }

        // Koš za izbrisana gesla
        composable("trash") {
            val context = LocalContext.current
            Trash(navController = navController, context = context)
        }

        // Nastavitve aplikacije + izbira temnega/močega načina
        composable("settings") {
            Settings(
                navController = navController,
                themeMode = themeMode,
                onThemeModeChange = onThemeModeChange
            )
        }

        // Generator gesel
        composable("generate") { GeneratePassword(navController) }

        // Podrobnosti posameznega gesla (dinamični argument: title)
        composable("details/{title}") { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title")
            PasswordDetailsScreen(
                context = navController.context,
                title = title,
                navController = navController
            )
        }
    }
}
