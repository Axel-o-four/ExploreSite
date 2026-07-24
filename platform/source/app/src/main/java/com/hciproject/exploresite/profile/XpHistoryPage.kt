package com.hciproject.exploresite.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hciproject.exploresite.R
import com.hciproject.exploresite.TranslationManager
import com.hciproject.exploresite.poi.CulturalPOI
import com.hciproject.exploresite.poi.PointOfInterest

@Composable
fun XpHistoryPage(
    onBack: () -> Unit,
    onPoiClick: (PointOfInterest) -> Unit,
    currentLanguage: String = "it",
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val user = CurrentUser
    val visitedPois = user?.visitedPois ?: emptyList()
    val achievedMedals = ObtainableMedals.filter { it.isAchieved(user) }
    
    var selectedMedal by remember { mutableStateOf<Medal?>(null) }

    var tCronologiaXp by remember { mutableStateOf("Cronologia XP") }
    var tVisitaPoi by remember { mutableStateOf("Visita Punto di Interesse") }
    var tMedagliaOttenuta by remember { mutableStateOf("Medaglia Ottenuta") }
    var tNessunXp by remember { mutableStateOf("Non hai ancora guadagnato XP.") }
    var tHoCapito by remember { mutableStateOf("Ho capito") }

    LaunchedEffect(currentLanguage) {
        if (currentLanguage != "it") {
            tCronologiaXp = TranslationManager.translate("Cronologia XP", currentLanguage)
            tVisitaPoi = TranslationManager.translate("Visita Punto di Interesse", currentLanguage)
            tMedagliaOttenuta = TranslationManager.translate("Medaglia Ottenuta", currentLanguage)
            tNessunXp = TranslationManager.translate("Non hai ancora guadagnato XP.", currentLanguage)
            tHoCapito = TranslationManager.translate("Ho capito", currentLanguage)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F8FF)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .background(Color.White, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.left_arrow),
                        contentDescription = "Back",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = tCronologiaXp,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (visitedPois.isEmpty() && achievedMedals.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = tNessunXp, color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // Show Medals first
                    items(achievedMedals) { medal ->
                        XpItemCard(
                            title = medal.name,
                            subtitle = tMedagliaOttenuta,
                            xpAmount = 100,
                            iconRes = medal.iconRes,
                            onClick = { selectedMedal = medal }
                        )
                    }
                    
                    // Show POI visits
                    items(visitedPois.reversed()) { poiName ->
                        XpItemCard(
                            title = poiName,
                            subtitle = tVisitaPoi,
                            xpAmount = 20,
                            onClick = {
                                val poi = CulturalPOI.find { it.name.equals(poiName, ignoreCase = true) }
                                if (poi != null) {
                                    onPoiClick(poi)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
    
    if (selectedMedal != null) {
        MedalInfoDialog(
            medal = selectedMedal!!,
            onDismiss = { selectedMedal = null },
            buttonText = tHoCapito,
            currentLanguage = currentLanguage
        )
    }
}

@Composable
fun XpItemCard(
    title: String, 
    subtitle: String, 
    xpAmount: Int, 
    iconRes: Int = R.drawable.level,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFE8F5E9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF4CAF50)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Text(
                text = "+$xpAmount XP",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF4CAF50)
            )
        }
    }
}
