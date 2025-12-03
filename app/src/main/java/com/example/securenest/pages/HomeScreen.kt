package com.example.securenest.pages

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.securenest.ui.theme.SecureNestTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.securenest.BottomNavigationBar
import java.io.File

// Funkcija prebere datoteko passwords.txt in jo pretvori v seznam map (en map = en vnos)
fun parsePasswordFile(context: Context, filename: String): List<Map<String, String>> {
    val file = File(context.filesDir, filename)
    if (!file.exists()) return emptyList()

    val entries = mutableListOf<Map<String, String>>()
    val lines = file.readLines()
    var currentEntry = mutableMapOf<String, String>()

    // Gremo po vrsticah in iščemo polja (Title, Username, Password, ...)
    for (line in lines) {
        when {
            line.startsWith("Title:") ->
                currentEntry["Title"] = line.removePrefix("Title:").trim()

            line.startsWith("Username:") ->
                currentEntry["Username"] = line.removePrefix("Username:").trim()

            line.startsWith("Password:") ->
                currentEntry["Password"] = line.removePrefix("Password:").trim()

            line.startsWith("Web Address:") ->
                currentEntry["Web Address"] = line.removePrefix("Web Address:").trim()

            line.startsWith("Note:") ->
                currentEntry["Note"] = line.removePrefix("Note:").trim()

            // separator "-----" pomeni, da se je en vnos končal
            line.startsWith("-") -> {
                if (currentEntry.isNotEmpty()) {
                    entries.add(currentEntry)
                    currentEntry = mutableMapOf()
                }
            }
        }
    }
    // če zadnji vnos ni imel separatorja, ga še vedno dodamo
    if (currentEntry.isNotEmpty()) entries.add(currentEntry)
    return entries
}

@Composable
fun HomeScreen(navController: NavHostController) {
    val context = navController.context

    // Preberemo vse shranjene vnose iz datoteke
    val entries = parsePasswordFile(context, "passwords.txt")

    // Snackbar za sporočila (npr. "Password moved to trash")
    val snackbarHostState = remember { SnackbarHostState() }

    // Preverimo, ali je prejšnji ekran nastavil kakšno snackbar sporočilo
    val snackbarMessage = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<String>("snackbar_message")

    // Ko se snackbarMessage spremeni, ga prikažemo in potem pobrišemo
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.remove<String>("snackbar_message")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },

        // Plavajoči gumb za dodajanje novega gesla
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create") },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new password",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }
        },

        bottomBar = {
            BottomNavigationBar(navController)
        },

        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        // Če še nimamo nobenega gesla, pokažemo info tekst
        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No passwords yet.\nTap + to add one.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        } else {
            // Če vnosi obstajajo, jih prikažemo v scrollable seznamu (LazyColumn)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // items(...) namesto ročnega for-loop – bolj učinkovito za večje sezname
                items(entries) { entry ->
                    PasswordRowCard(
                        entry = entry,
                        onClick = {
                            // Ob kliku gremo na podrobnosti izbranega vnosa
                            navController.navigate("details/${entry["Title"]}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PasswordRowCard(
    entry: Map<String, String>,
    onClick: () -> Unit
) {
    // Iz mape potegnemo relevantne podatke
    val title = entry["Title"].orEmpty().ifBlank { "Untitled" }
    val username = entry["Username"].orEmpty()
    val web = entry["Web Address"].orEmpty()

    // Kartica, ki zasede celotno širino vrstice
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Krog z začetnico naslova (ikona "account")
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title.first().uppercaseChar().toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Tekstovni podatki (naslov, username, web)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                )
                if (username.isNotBlank()) {
                    Text(
                        text = username,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }
                if (web.isNotBlank()) {
                    Text(
                        text = web,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                    )
                }
            }

            // Majhen “arrow” na desni, da nakazuje, da je kartica klikabilna
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    SecureNestTheme {
        HomeScreen(navController = rememberNavController())
    }
}
