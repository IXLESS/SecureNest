package com.example.securenest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.securenest.ui.theme.SecureNestTheme
import com.securenest.navigation.AppNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // Trenutni način teme (SYSTEM / LIGHT / DARK), shranjen skozi rotacije.
            var themeMode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }

            // Glavna tema aplikacije, odvisna od izbranega ThemeMode.
            SecureNestTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Glavna navigacija aplikacije + možnost spreminjanja teme iz Settings ekrana.
                    AppNavHost(
                        themeMode = themeMode,
                        onThemeModeChange = { themeMode = it }
                    )
                }
            }
        }
    }
}
