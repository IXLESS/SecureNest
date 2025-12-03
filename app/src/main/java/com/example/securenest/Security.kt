package com.example.securenest

import okhttp3.OkHttpClient
import okhttp3.Request
import java.security.MessageDigest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

// OkHttp klient z določenimi timeouti za bolj zanesljive API klice.
private val client = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(20, TimeUnit.SECONDS)
    .writeTimeout(15, TimeUnit.SECONDS)
    .build()

suspend fun getPasswordBreachCountRaw(password: String): Int {
    return try {

        // SHA-1 hash gesla (zahteva ga HIBP API)
        val hash = MessageDigest.getInstance("SHA-1")
            .digest(password.toByteArray())
            .joinToString("") { "%02x".format(it) }
            .uppercase()

        val prefix = hash.substring(0, 5)   // prvi 5 znakov
        val suffix = hash.substring(5)      // ostanek hash-a

        // HTTP zahteva po HIBP k-anonymity protokolu
        val request = Request.Builder()
            .url("https://api.pwnedpasswords.com/range/$prefix")
            .header("User-Agent", "SecureNest/1.0 (Android)")
            .get()
            .build()

        // Izvedba v IO niti
        return withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: return@withContext 0

                // Iskanje ustreznega suffixa v API odgovoru
                for (line in body.lines()) {
                    val parts = line.split(":")
                    if (parts.size == 2) {
                        val apiSuffix = parts[0].uppercase()
                        val count = parts[1].toIntOrNull() ?: 0

                        if (apiSuffix == suffix) {
                            return@withContext count
                        }
                    }
                }

                0  // ni zadetka
            }
        }

    } catch (_: Exception) {
        0  // napaka pri klicu, obravnavamo kot 0 zadetkov
    }
}
