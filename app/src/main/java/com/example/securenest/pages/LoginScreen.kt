package com.example.securenest.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.securenest.ui.theme.SecureNestTheme
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun LoginScreen(navController: NavHostController) {

    // Shranjujemo vnešeni username in geslo
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Če je napačna prijava → pokažemo error
    var error by remember { mutableStateOf(false) }

    // Form je veljaven, ko oba polja nista prazna
    val isFormValid = username.isNotBlank() && password.isNotBlank()

    // Pokaži/skrij geslo
    var passwordVisible by remember { mutableStateOf(false) }

    // Glavna postavitev na sredini ekrana
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // Naslov
            Text(
                "Enter Master Password",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Username vnos
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier
                    .width(300.dp)
                    .height(65.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Password vnos z možnostjo prikazovanja
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Master password") },
                visualTransformation =
                    if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),

                trailingIcon = {
                    // Ikona za prikaz/skrij geslo
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = image,
                            contentDescription =
                                if (passwordVisible) "Hide password"
                                else "Show password"
                        )
                    }
                },
                singleLine = true,
                modifier = Modifier.width(300.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Prijava
            Button(
                onClick = {
                    // Trda (hardcoded) prijava za primer aplikacije
                    if (username == "admin" && password == "admin") {
                        navController.navigate("welcome")
                    } else {
                        error = true
                    }
                },
                enabled = isFormValid, // gumb je onemogočen, dokler polja niso izpolnjena
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.width(220.dp)
            ) {
                Text("Unlock SecureNest")
            }

            // Prikaz napake pri napačni prijavi
            if (error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Master password is invalid",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    SecureNestTheme {
        LoginScreen(navController = rememberNavController())
    }
}
