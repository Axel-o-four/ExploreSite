package com.hciproject.exploresite.profile

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hciproject.exploresite.R
import com.hciproject.exploresite.TranslationManager
import com.hciproject.exploresite.poi.CulturalPOI
import com.hciproject.exploresite.poi.POICategory

data class Medal(
    val id: String,
    val name: String,
    val description: String,
    val iconRes: Int,
    val province: String? = null,
    val category: POICategory? = null
) {
    fun isAchieved(user: User?): Boolean {
        if (user == null) return false
        
        if (province != null) {
            val poisInProvince = CulturalPOI.filter { 
                it.address.contains(province, ignoreCase = true) || 
                it.address.contains(getProvinceCode(province), ignoreCase = true) 
            }
            if (poisInProvince.isEmpty()) return false
            return poisInProvince.all { poi -> user.visitedPois.any { it.equals(poi.name, ignoreCase = true) } }
        }
        
        if (category != null) {
            val poisInCategory = CulturalPOI.filter { it.type == category }
            if (poisInCategory.isEmpty()) return false
            return poisInCategory.all { poi -> user.visitedPois.any { it.equals(poi.name, ignoreCase = true) } }
        }
        
        if (id == "campania") {
            return CulturalPOI.all { poi -> user.visitedPois.any { it.equals(poi.name, ignoreCase = true) } }
        }
        
        return false
    }

    private fun getProvinceCode(province: String): String {
        return when (province.lowercase()) {
            "caserta" -> "(CE)"
            "napoli" -> "(NA)"
            "benevento" -> "(BN)"
            "salerno" -> "(SA)"
            "avellino" -> "(AV)"
            else -> ""
        }
    }
}

val ObtainableMedals = listOf(
    // Special Medal - First
    Medal(
        id = "campania",
        name = "Medaglia Campania",
        description = "Per ottenere questa medaglia devi visitare tutti i punti di interesse della regione Campania presenti nell'app",
        iconRes = R.drawable.medaglia_campania
    ),
    // Province Medals
    Medal(
        id = "caserta",
        name = "Medaglia Caserta",
        description = "Per ottenere questa medaglia devi visitare tutti i punti di interesse della provincia di Caserta",
        iconRes = R.drawable.medaglia_caserta,
        province = "Caserta"
    ),
    Medal(
        id = "napoli",
        name = "Medaglia Napoli",
        description = "Per ottenere questa medaglia devi visitare tutti i punti di interesse della provincia di Napoli",
        iconRes = R.drawable.medaglia_napoli,
        province = "Napoli"
    ),
    Medal(
        id = "benevento",
        name = "Medaglia Benevento",
        description = "Per ottenere questa medaglia devi visitare tutti i punti di interesse della provincia di Benevento",
        iconRes = R.drawable.medaglia_benevento,
        province = "Benevento"
    ),
    Medal(
        id = "salerno",
        name = "Medaglia Salerno",
        description = "Per ottenere questa medaglia devi visitare tutti i punti di interesse della provincia di Salerno",
        iconRes = R.drawable.medaglia_salerno,
        province = "Salerno"
    ),
    Medal(
        id = "avellino",
        name = "Medaglia Avellino",
        description = "Per ottenere questa medaglia devi visitare tutti i punti di interesse della provincia di Avellino",
        iconRes = R.drawable.medaglia_avellino,
        province = "Avellino"
    ),
    // Category Medals
    Medal(
        id = "museo",
        name = "Medaglia Musei",
        description = "Per ottenere questa medaglia devi visitare tutti i musei presenti nell'app",
        iconRes = R.drawable.medaglia_museo,
        category = POICategory.MUSEUM
    ),
    Medal(
        id = "culinaria",
        name = "Medaglia Culinaria",
        description = "Per ottenere questa medaglia devi visitare tutti i punti di interesse culinari",
        iconRes = R.drawable.medaglia_culinaria,
        category = POICategory.CULINARY
    ),
    Medal(
        id = "religiosa",
        name = "Medaglia Religiosa",
        description = "Per ottenere questa medaglia devi visitare tutti i luoghi religiosi",
        iconRes = R.drawable.medaglia_religiosa,
        category = POICategory.RELIGIOUS
    ),
    Medal(
        id = "archeologia",
        name = "Medaglia Archeologia",
        description = "Per ottenere questa medaglia devi visitare tutti i siti archeologici",
        iconRes = R.drawable.medaglia_archeologia,
        category = POICategory.ARCHEOLOGICAL
    ),
    Medal(
        id = "architettura",
        name = "Medaglia Architettonica",
        description = "Per ottenere questa medaglia devi visitare tutti i punti di interesse architettonici",
        iconRes = R.drawable.medaglia_architettura,
        category = POICategory.ARCHITECTURE
    )
)

@Composable
fun MedalInfoDialog(
    medal: Medal,
    onDismiss: () -> Unit,
    buttonText: String,
    currentLanguage: String
) {
    var translatedName by remember { mutableStateOf(medal.name) }
    var translatedDesc by remember { mutableStateOf(medal.description) }

    LaunchedEffect(currentLanguage, medal) {
        if (currentLanguage != "it") {
            translatedName = TranslationManager.translate(medal.name, currentLanguage)
            translatedDesc = TranslationManager.translate(medal.description, currentLanguage)
        } else {
            translatedName = medal.name
            translatedDesc = medal.description
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(translatedName, fontWeight = FontWeight.Bold) },
        text = { Text(translatedDesc) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(buttonText)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp)
    )
}
