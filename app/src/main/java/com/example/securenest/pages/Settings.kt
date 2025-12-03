package com.example.securenest.pages

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.securenest.BottomNavigationBar
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.securenest.ui.theme.SecureNestTheme
import com.example.securenest.ThemeMode
import com.example.securenest.getPasswordBreachCountRaw
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    navController: NavHostController,
    themeMode: ThemeMode,                 // trenutno izbrana tema v aplikaciji
    onThemeModeChange: (ThemeMode) -> Unit // callback za spremembo teme
) {
    val scope = rememberCoroutineScope()
    val systemDark = isSystemInDarkTheme() // preveri, ali je telefon v dark mode

    // state za password leak checker
    var password by remember { mutableStateOf("") }
    var breachCount by remember { mutableStateOf<Int?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isChecking by remember { mutableStateOf(false) }

    Scaffold(
        // zgornja vrstica z napisom "Settings"
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") }
            )
        },
        // navigacijska vrstica na dnu
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ----------------------------------
            //  APPEARANCE – nastavitve teme
            // ----------------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Appearance",
                        style = MaterialTheme.typography.titleMedium
                    )

                    // prikaz trenutne sistemske teme
                    Text(
                        text = "System theme: " + if (systemDark) "Dark" else "Light",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "App theme:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // izbira teme v aplikaciji preko FilterChip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = themeMode == ThemeMode.SYSTEM,
                            onClick = { onThemeModeChange(ThemeMode.SYSTEM) },
                            label = { Text("System") }
                        )
                        FilterChip(
                            selected = themeMode == ThemeMode.LIGHT,
                            onClick = { onThemeModeChange(ThemeMode.LIGHT) },
                            label = { Text("Light") }
                        )
                        FilterChip(
                            selected = themeMode == ThemeMode.DARK,
                            onClick = { onThemeModeChange(ThemeMode.DARK) },
                            label = { Text("Dark") }
                        )
                    }
                }
            }

            // ----------------------------------
            //  ACCOUNT – odjava iz aplikacije
            // ----------------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Account",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "You are currently logged in.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // gumb za odjavo
                    Button(
                        onClick = { navController.navigate("login") },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Log Out")
                    }
                }
            }

            // ----------------------------------
            //  SECURITY TOOLS – preverjanje leakanih gesel
            // ----------------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Security tools",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Check if a password appears in known data breaches.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // input za geslo
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            breachCount = null
                            errorMessage = null
                        },
                        label = { Text("Enter password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // gumb za API klic
                    Button(
                        onClick = {
                            if (password.isBlank()) {
                                errorMessage = "Enter a password first."
                                breachCount = null
                                return@Button
                            }

                            scope.launch {
                                isChecking = true
                                errorMessage = null
                                try {
                                    // REST API klic (HaveIBeenPwned)
                                    val count = getPasswordBreachCountRaw(password)
                                    breachCount = count
                                } catch (e: Exception) {
                                    Log.e("LeakCheck", "Error checking password", e)
                                    errorMessage = "Error checking password. Try again."
                                    breachCount = null
                                } finally {
                                    isChecking = false
                                }
                            }
                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isChecking) {
                            // prikaz loading indikatorja
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Checking…")
                        } else {
                            Text("Test password")
                        }
                    }

                    // rezultat REST klica (ali napaka)
                    when {
                        errorMessage != null -> {
                            Text(
                                text = errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        breachCount != null -> {
                            if (breachCount == 0) {
                                Text(
                                    text = "Good news – this password was not found in known breaches.",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            } else {
                                Text(
                                    text = "Warning: this password appears in $breachCount breaches.",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    SecureNestTheme {
        Settings(
            navController = rememberNavController(),
            themeMode = ThemeMode.SYSTEM,
            onThemeModeChange = {}
        )
    }
}