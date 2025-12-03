package com.example.securenest.pages

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.securenest.ui.theme.SecureNestTheme
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.imePadding

// Model za shranjevanje enega vnosa gesla
data class PasswordEntry(
    val title: String,
    val username: String,
    val password: String,
    val webAddress: String,
    val note: String
)

// Funkcija za zapis gesla v lokalno datoteko passwords.txt
fun saveEntryToFile(context: Context, entry: PasswordEntry) {
    val fileName = "passwords.txt"

    // Pripravimo formatirane vrstice
    val lines = listOf(
        "Title: ${entry.title.trim()}",
        "Username: ${entry.username.trim()}",
        "Password: ${entry.password.trim()}",
        "Web Address: ${entry.webAddress.trim()}",
        "Note: ${entry.note.trim()}",
        "-----------------------------"
    )

    // Vsak vnos dodamo v datoteko
    val formatted = lines.joinToString("\n") + "\n"
    context.openFileOutput(fileName, Context.MODE_APPEND).use {
        it.write(formatted.toByteArray())
    }
}

// Funkcija ki oceni moč gesla (dolžina + kompleksnost)
fun evaluatePasswordStrength(password: String, colorScheme: ColorScheme): Pair<String, Color> {
    val lengthScore = when {
        password.length >= 12 -> 2
        password.length >= 8 -> 1
        else -> 0
    }

    val complexityScore = listOf(
        Regex("[A-Z]"), Regex("[a-z]"), Regex("[0-9]"), Regex("[^A-Za-z0-9]")
    ).count { it.containsMatchIn(password) }

    val totalScore = lengthScore + complexityScore

    // Vrne label + barvo za prikaz
    return when {
        totalScore >= 5 -> "✅ Strong Password" to colorScheme.primary
        totalScore >= 3 -> "⚠️ Medium Strength" to colorScheme.tertiary
        else -> "❌ Weak Password" to colorScheme.error
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPasswordScreen(navController: NavHostController) {
    val context = LocalContext.current

    // Pomnilnik za vrednosti v obrazcu (preživijo rotacijo zaslona)
    var title by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var webAddress by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    // Če smo prišli iz GeneratePassword, preberemo generirano geslo
    LaunchedEffect(savedStateHandle) {
        val generated = savedStateHandle?.get<String>("generatedPassword")
        if (!generated.isNullOrEmpty()) {
            password = generated
            savedStateHandle.remove<String>("generatedPassword")
        }
    }

    // Izračunamo moč gesla
    val (strengthLabel, strengthColor) =
        evaluatePasswordStrength(password, MaterialTheme.colorScheme)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // Zgornja vrstica z gumbom nazaj
            TopAppBar(
                title = { Text("Add Password") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        // bottomBar removed completely
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        // Glavna vsebina obrazca
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .imePadding(), // poskrbi, da vsebina ne gre pod tipkovnico
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Vnos naslova
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title (Required)") },
                modifier = Modifier.fillMaxWidth().height(65.dp)
            )

            Text("LOGIN DETAILS", style = MaterialTheme.typography.titleSmall)

            // Vnos username ali email
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Email or Username") },
                modifier = Modifier.fillMaxWidth().height(65.dp)
            )

            // Vnos gesla + gumb za prikaz/skrivanje
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth().height(65.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            // Prikaz ocene moči gesla
            Text(strengthLabel, color = strengthColor)

            // Gumb za generiranje gesla
            Button(
                onClick = { navController.navigate("generate") },
                modifier = Modifier.align(Alignment.End)
            ) { Text("Generate Password") }

            Text("WEBSITES AND APPS", style = MaterialTheme.typography.titleSmall)

            OutlinedTextField(
                value = webAddress,
                onValueChange = { webAddress = it },
                label = { Text("Web Address") },
                modifier = Modifier.fillMaxWidth().height(65.dp)
            )

            Text("Notes", style = MaterialTheme.typography.titleSmall)

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth().height(75.dp)
            )

            // Gumb za shranjevanje vnosa
            Button(
                onClick = {
                    coroutineScope.launch {

                        if (title.isBlank()) {
                            snackbarHostState.showSnackbar("Title is required!")
                            return@launch
                        }

                        val entry = PasswordEntry(title, username, password, webAddress, note)
                        saveEntryToFile(context, entry)

                        snackbarHostState.showSnackbar("Your password was saved")

                        title = ""
                        username = ""
                        password = ""
                        webAddress = ""
                        note = ""

                        delay(500)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddPasswordScreenPreview() {
    SecureNestTheme {
        AddPasswordScreen(navController = rememberNavController())
    }
}
