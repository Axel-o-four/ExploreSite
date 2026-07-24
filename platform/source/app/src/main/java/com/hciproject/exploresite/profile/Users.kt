package com.hciproject.exploresite.profile

import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class User(
    val fullName: String,
    val email: String,
    val password: String, // Stored as Base64 string
    val dateOfBirth: String,
    val nationality: String,
    val profileImageUri: String? = null,
    val visitedPois: List<String> = emptyList()
) {
    /**
     * Calculates total XP based on visited POIs and achieved medals.
     * Each POI visit = 20 XP
     * Each Medal earned = 100 XP
     */
    val totalXp: Int get() {
        val poiXp = visitedPois.size * 20
        val medalXp = ObtainableMedals.count { it.isAchieved(this) } * 100
        return poiXp + medalXp
    }

    /**
     * Calculates level based on XP.
     * Level up formula: 25 * currentLevel XP to reach the next level.
     */
    fun getLevelInfo(): LevelInfo {
        var currentLevel = 1
        var xpAccumulated = 0
        var xpToNext = 25 * currentLevel
        
        while (totalXp >= xpAccumulated + xpToNext) {
            xpAccumulated += xpToNext
            currentLevel++
            xpToNext = 25 * currentLevel
        }
        
        val progress = (totalXp - xpAccumulated).toFloat() / xpToNext
        
        val label = when {
            currentLevel < 3 -> "Turista"
            currentLevel < 6 -> "Esploratore"
            currentLevel < 10 -> "Cicerone"
            else -> "Cavaliere"
        }
        
        return LevelInfo(currentLevel, label, progress)
    }

    // Helper functions for password encryption/decryption
    fun getDecryptedPassword(): String {
        return try {
            String(Base64.decode(password, Base64.DEFAULT))
        } catch (e: Exception) {
            password // Fallback if not encoded
        }
    }

    companion object {
        fun encryptPassword(password: String): String {
            return Base64.encodeToString(password.toByteArray(), Base64.DEFAULT).trim()
        }
    }
}

data class LevelInfo(
    val level: Int,
    val label: String,
    val progress: Float
)

// Global state for the current logged-in user. Null means logged out.
var CurrentUser by mutableStateOf<User?>(User(
    fullName = "Esploratore Curioso",
    email = "curious.explorer@gmail.com",
    password = User.encryptPassword("IloveTOexplore123"),
    dateOfBirth = "01/01/2000",
    nationality = "Italiana",
    // Reference to the resource image
    profileImageUri = "android.resource://com.hciproject.exploresite/drawable/esploratore_curioso",
    visitedPois = listOf(
        // Culinary POIs (Required for Culinary Medal)
        "Antica Pizzeria Da Michele", 
        "Pepe in Grani", 
        "Fabbrica Alberti - Liquore Strega", 
        "Borgo Marinaro di Cetara", 
        "Cantine Mastroberardino",
        // Salerno Province POIs (Required for Salerno Medal)
        "Castello di Arechi",
        "Parco Archeologico di Paestum",
        "Cattedrale di San Matteo (Duomo di Salerno)",
        "Museo Archeologico Provinciale di Salerno",
        // Additional visits
        "Reggia di Caserta"
    )
))
