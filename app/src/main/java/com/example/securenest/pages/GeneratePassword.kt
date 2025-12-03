package com.example.securenest.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.securenest.ui.theme.SecureNestTheme
import androidx.navigation.NavHostController
import kotlin.random.Random

// Funkcija za generiranje naključnega gesla
fun generatePassword(
    length: Int = 16,          // privzeta dolžina gesla
    useLetters: Boolean,
    useNumbers: Boolean,
    useSymbols: Boolean
): String {

    // Znaki, ki jih lahko uporabimo
    val letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val numbers = "0123456789"
    val symbols = "!@#$%^&*()-_=+[]{}<>?/|"

    // Sestavimo "pool" znakov glede na to, kaj je uporabnik izbral
    var pool = ""
    if (useLetters) pool += letters
    if (useNumbers) pool += numbers
    if (useSymbols) pool += symbols

    // Če ni izbran noben tip znakov, vrne prazno
    if (pool.isEmpty()) return ""

    // Zgradimo naključno geslo iz izbranega nabora znakov
    return (1..length)
        .map { pool[Random.nextInt(pool.length)] }
        .joinToString("")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratePassword(navController: NavHostController) {

    // Stanja, ki hranijo izbire uporabnika
    var useLetters by remember { mutableStateOf(true) }
    var useNumbers by remember { mutableStateOf(true) }
    var useSymbols by remember { mutableStateOf(true) }

    // Trenutno generirano geslo
    var generatedPassword by remember { mutableStateOf("") }

    Scaffold(
        // Zgornja vrstica z naslovom in gumbom nazaj
        topBar = {
            TopAppBar(
                title = { Text("Generate Password") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },

                // Gumb "Use", ki geslo vrne na AddPasswordScreen
                actions = {
                    TextButton(onClick = {

                        if (generatedPassword.isNotEmpty()) {

                            // Shranimo geslo v SavedStateHandle prejšnje strani
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("generatedPassword", generatedPassword)

                            // Vrni nazaj na AddPasswordScreen
                            navController.popBackStack()
                        }
                    }) {
                        Text("Use")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        // bottomBar je odstranjen
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        // Glavna vsebina ekrana
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Polje, kjer se pokaže generirano geslo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                if (generatedPassword.isNotEmpty()) {

                    // Prikaz gesla, če je že generirano
                    Text(
                        text = generatedPassword,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {

                    // Privzeto sporočilo, kadar geslo še ni generirano
                    Text(
                        text = "Your password will appear here...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Choose character types:", style = MaterialTheme.typography.titleMedium)

            // Checkboxi za izbiro tipov znakova
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = useLetters, onCheckedChange = { useLetters = it })
                Spacer(modifier = Modifier.width(8.dp))
                Text("Include letters (A–Z, a–z)")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = useNumbers, onCheckedChange = { useNumbers = it })
                Spacer(modifier = Modifier.width(8.dp))
                Text("Include numbers (0–9)")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = useSymbols, onCheckedChange = { useSymbols = it })
                Spacer(modifier = Modifier.width(8.dp))
                Text("Include symbols (!@#...)")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Gumb za generiranje novega gesla
            Button(
                onClick = {
                    generatedPassword = generatePassword(
                        useLetters = useLetters,
                        useNumbers = useNumbers,
                        useSymbols = useSymbols
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
            ) {
                Text("Generate Password")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gumb "Add" – enako kot zgornji "Use", samo večji
            Button(
                onClick = {
                    if (generatedPassword.isNotEmpty()) {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("generatedPassword", generatedPassword)

                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
            ) {
                Text("Add")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GeneratePasswordPreview() {
    SecureNestTheme {
        GeneratePassword(navController = rememberNavController())
    }
}
