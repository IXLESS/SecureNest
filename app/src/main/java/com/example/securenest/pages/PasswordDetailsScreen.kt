package com.example.securenest.pages

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import com.example.securenest.ui.theme.SecureNestTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.securenest.getPasswordBreachCountRaw
import com.example.securenest.movePasswordToTrash
import com.example.securenest.ui.theme.DeleteRed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

// Funkcija za oceno moči gesla (dolžina + kompleksnost)
@Composable
fun evaluatePasswordStrength(password: String): Pair<String, Color> {
    val lengthScore = when {
        password.length >= 12 -> 2
        password.length >= 8 -> 1
        else -> 0
    }

    // Preverimo, koliko različnih tipov znakov vsebuje geslo
    val complexityScore = listOf(
        Regex("[A-Z]"), Regex("[a-z]"), Regex("[0-9]"), Regex("[^A-Za-z0-9]")
    ).count { it.containsMatchIn(password) }

    val totalScore = lengthScore + complexityScore
    val colorScheme = MaterialTheme.colorScheme

    // Vrne tekstovni opis + barvo, ki jo kasneje prikažemo na ekranu
    return when {
        totalScore >= 5 -> "✅ Strong Password" to colorScheme.primary
        totalScore >= 3 -> "⚠️ Medium Strength" to colorScheme.tertiary
        else -> "❌ Weak Password" to colorScheme.error
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordDetailsScreen(
    context: Context,
    title: String?,
    navController: NavHostController? = null
) {
    // Preberemo vse vnose iz datoteke
    val entries = parsePasswordFile(context, "passwords.txt")

    // Poiščemo tisti vnos, ki ima ujemajoč "Title"
    val entry = entries.find { it["Title"] == title }

    // State za prikaz/skrivanje gesla
    var passwordVisible by remember { mutableStateOf(false) }

    // Iz trenutnega vnosa vzamemo geslo (ali prazen string, če je null)
    val password = entry?.get("Password") ?: ""

    // State, kamor shranimo rezultat API klica (kolikokrat je bilo geslo izpostavljeno)
    var breachCount by remember { mutableStateOf<Int?>(null) }

    // Ko se geslo spremeni, asinhrono pokličemo API (haveibeenpwned)
    LaunchedEffect(password) {
        if (password.isNotBlank()) {
            breachCount = try {
                withContext(Dispatchers.IO) {
                    getPasswordBreachCountRaw(password)
                }
            } catch (_: Exception) {
                // Če pride do napake pri API klicu, samo nastavimo null
                null
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    // Če je vnos najden
    entry?.let { nonNullEntry ->
        Scaffold(
            topBar = {
                // Zgornja vrstica z naslovom vnosa in gumbom nazaj
                TopAppBar(
                    title = {
                        Text(
                            text = nonNullEntry["Title"] ?: "Password Details",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController?.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    // omogočimo scroll in poravnavo nad sistemsko vrstico / tipkovnico
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // USERNAME / EMAIL
                Text(
                    "Email or Username:",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                Text(
                    nonNullEntry["Username"] ?: "",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                    thickness = 1.dp
                )

                // PASSWORD + toggle za prikaz/skrivanje
                Text(
                    "Password:",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (passwordVisible) password else "•••••••••••••••",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle password visibility",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                    thickness = 1.dp
                )

                // WEB ADDRESS
                Text(
                    "Web Address:",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                Text(
                    nonNullEntry["Web Address"] ?: "",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                    thickness = 1.dp
                )

                // NOTE
                Text(
                    "Note:",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                Text(
                    nonNullEntry["Note"] ?: "",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                    thickness = 1.dp
                )

                // PASSWORD HEALTH (lokalni strength + rezultat API-ja)
                Text(
                    "Password Health",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                val (strengthLabel, strengthColor) = evaluatePasswordStrength(password)
                Text(strengthLabel, color = strengthColor, fontSize = 14.sp)

                // Prikaz rezultata iz API-ja (koliko krat je bilo geslo leakano)
                when (breachCount) {
                    null -> Text("Checking breach status...", fontSize = 14.sp)
                    0 -> Text(
                        "🛡️ Password not on the dark web",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                    else -> Text(
                        "❌ Leaked ${"%,d".format(breachCount)} times",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(60.dp))

                // Gumb za premik vnosa v trash
                Button(
                    onClick = {
                        // Vnos premaknemo v trash datoteko
                        movePasswordToTrash(context, title ?: "")

                        // Pošljemo snackbar sporočilo nazaj na HomeScreen
                        navController?.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("snackbar_message", "Password moved to trash")

                        navController?.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeleteRed,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Trash",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Move to trash", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    } ?: Text(
        // Če vnosa z danim naslovom ne najdemo, prikažemo sporočilo
        "Entry not found.",
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(24.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PasswordDetailsScreenPreview() {
    val context = LocalContext.current

    // V preview-u si ustvarimo testni vnos v passwords.txt,
    // da lahko ekran pokaže dejanske podatke
    remember {
        val file = File(context.filesDir, "passwords.txt")
        file.writeText(
            """
            Title: Preview Account
            Username: preview@example.com
            Password: Passw0rd!
            Web Address: preview.com
            Note: This is a preview entry
            -----------------------------
            """.trimIndent()
        )
        0 // dummy return, da remember nekaj vrne
    }

    // Prikaz ekrana za "Preview Account"
    SecureNestTheme {
        PasswordDetailsScreen(
            context = context,
            title = "Preview Account",
            navController = rememberNavController()
        )
    }
}
