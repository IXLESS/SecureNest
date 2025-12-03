package com.example.securenest

import android.content.Context
import com.example.securenest.pages.parsePasswordFile
import java.io.File

// Pretvori zapis v format, ki ga shranimo v datoteko
fun formatEntry(entry: Map<String, String>): String =
    "Title: ${entry["Title"]}\n" +
            "Username: ${entry["Username"]}\n" +
            "Password: ${entry["Password"]}\n" +
            "Web Address: ${entry["Web Address"]}\n" +
            "Note: ${entry["Note"]}\n" +
            "-----------------------------\n"

// Premakne geslo iz glavnega seznama v koš
fun movePasswordToTrash(context: Context, title: String) {
    val entries = parsePasswordFile(context, "passwords.txt")
    val entry = entries.find { it["Title"] == title } ?: return

    File(context.filesDir, "trash.txt").appendText(formatEntry(entry))

    val newList = entries.filterNot { it["Title"] == title }
    File(context.filesDir, "passwords.txt")
        .writeText(newList.joinToString("\n") { formatEntry(it) })
}

// Obnovi geslo iz koša nazaj v seznam
fun restorePassword(context: Context, entry: Map<String, String>) {
    File(context.filesDir, "passwords.txt").appendText(formatEntry(entry))
    deleteFromTrash(context, entry)
}

// Trajno izbriše geslo iz koša
fun deleteFromTrash(context: Context, entry: Map<String, String>) {
    val newList = parsePasswordFile(context, "trash.txt")
        .filterNot { it["Title"] == entry["Title"] }

    File(context.filesDir, "trash.txt")
        .writeText(newList.joinToString("\n") { formatEntry(it) })
}
