package com.hciproject.exploresite.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class User(
    val fullName: String,
    val email: String,
    val password: String,
    val dateOfBirth: String,
    val nationality: String,
    val profileImageUri: String? = null,
    val visitedPois: List<String> = emptyList()
) {
    val totalXp: Int get() = visitedPois.size * 20

    /**
     * Calculates level based on XP.
     * Level up formula: 25 * currentLevel XP to reach the next level.
     * Lvl 1 -> 2: 25 XP (Total: 25)
     * Lvl 2 -> 3: 50 XP (Total: 75)
     * Lvl 3 -> 4: 75 XP (Total: 150)
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
}

data class LevelInfo(
    val level: Int,
    val label: String,
    val progress: Float
)

// Global state for the current logged-in user. Null means logged out.
var CurrentUser by mutableStateOf<User?>(User(
    fullName = "ALESSIO DI CAPRIO",
    email = "alessio.dicaprio@email.com",
    password = "Password123",
    dateOfBirth = "01/01/1995",
    nationality = "Italiana",
    profileImageUri = null,
    visitedPois = listOf("Scavi di Pompei", "Museo Irpino", "Reggia di Caserta") // Order: latest first
))
