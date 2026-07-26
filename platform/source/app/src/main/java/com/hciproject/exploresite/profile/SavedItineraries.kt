package com.hciproject.exploresite.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hciproject.exploresite.TranslationManager
import com.hciproject.exploresite.itinerary.Itineraries
import com.hciproject.exploresite.itinerary.ItineraryCard

@Composable
fun SavedItinerariesPage(
    onBack: () -> Unit,
    onItineraryClick: (Itineraries) -> Unit,
    currentLanguage: String = "it",
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    var title by remember { mutableStateOf("Itinerari salvati e creati") }
    var tSaved by remember { mutableStateOf("Salvati") }
    var tCreated by remember { mutableStateOf("Creati") }
    var tEmpty by remember { mutableStateOf("Non hai ancora salvato o creato alcun itinerario.") }
    
    LaunchedEffect(currentLanguage) {
        if (currentLanguage == "it") {
            title = "Itinerari salvati e creati"
            tSaved = "Salvati"
            tCreated = "Creati"
            tEmpty = "Non hai ancora salvato o creato alcun itinerario."
        } else {
            title = TranslationManager.translate("Itinerari salvati e creati", currentLanguage)
            tSaved = TranslationManager.translate("Salvati", currentLanguage)
            tCreated = TranslationManager.translate("Creati", currentLanguage)
            tEmpty = TranslationManager.translate("Non hai ancora salvato o creato alcun itinerario.", currentLanguage)
        }
    }

    val user = CurrentUser
    val savedItineraries = user?.savedItineraries ?: emptyList()
    val createdItineraries = user?.createdItineraries ?: emptyList()

    Surface(
        modifier = Modifier
            .fillMaxSize(),
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
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .background(Color.White, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (savedItineraries.isEmpty() && createdItineraries.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tEmpty,
                        modifier = Modifier.padding(32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (createdItineraries.isNotEmpty()) {
                        item {
                            Text(
                                text = tCreated,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(createdItineraries) { itinerary ->
                            ItineraryCard(
                                itineraries = itinerary,
                                onClick = { onItineraryClick(itinerary) }
                            )
                        }
                    }

                    if (savedItineraries.isNotEmpty()) {
                        item {
                            Text(
                                text = tSaved,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(savedItineraries) { itinerary ->
                            ItineraryCard(
                                itineraries = itinerary,
                                onClick = { onItineraryClick(itinerary) }
                            )
                        }
                    }
                }
            }
        }
    }
}
