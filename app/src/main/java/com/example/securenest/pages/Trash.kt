package com.example.securenest.pages

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.securenest.BottomNavigationBar
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import com.example.securenest.ui.theme.SecureNestTheme
import com.example.securenest.ui.theme.RestoreGreen
import com.example.securenest.ui.theme.DeleteRed
import com.example.securenest.restorePassword
import com.example.securenest.deleteFromTrash

@Composable
fun Trash(navController: NavHostController, context: Context) {

    // Preberemo izbrisane vnose iz datoteke trash.txt
    // Uporabimo remember, da se stanje ohrani med recomposi
    var trashEntries by remember { mutableStateOf(parsePasswordFile(context, "trash.txt")) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)   // spodnja navigacija
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        // Če ni nobenega izbrisanega vnosa → prikažemo info tekst
        if (trashEntries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Trash is empty",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

        } else {

            // Če obstajajo vnosi → prikažemo celoten seznam
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Za vsak vnos v trash naredimo eno kartico
                items(trashEntries) { entry ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {

                        // Vsebina posamezne kartice
                        Column(modifier = Modifier.padding(16.dp)) {

                            // Naslov vnosa
                            Text(
                                entry["Title"] ?: "Untitled",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Ostali podatki
                            Text(
                                "Username: ${entry["Username"] ?: ""}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Web: ${entry["Web Address"] ?: ""}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                "Note: ${entry["Note"] ?: ""}",
                                style = MaterialTheme.typography.bodySmall
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Gumbi: Restore in Permanent Delete
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                                // Obnovi vnos iz koša
                                Button(
                                    onClick = {
                                        restorePassword(context, entry)
                                        trashEntries = parsePasswordFile(context, "trash.txt") // osveži seznam
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = RestoreGreen,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text("Restore")
                                }

                                // Trajno izbriši vnos
                                Button(
                                    onClick = {
                                        deleteFromTrash(context, entry)
                                        trashEntries = parsePasswordFile(context, "trash.txt") // osveži seznam
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DeleteRed,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text("Delete")
                                }
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
fun TrashPreview() {
    val context = LocalContext.current

    // Preprosto prikažemo UI s testnim navControllerjem
    SecureNestTheme {
        Trash(
            navController = rememberNavController(),
            context = context
        )
    }
}
