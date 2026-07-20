package com.hciproject.exploresite.itinerary

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hciproject.exploresite.R
import com.hciproject.exploresite.TranslationManager

@Composable
fun ItineraryPage(
    modifier: Modifier = Modifier,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    onItineraryClick: (Itineraries) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var tSearchPlaceholder by remember { mutableStateOf("Cerca un itinerario...") }
    var isTranslationMenuVisible by remember { mutableStateOf(false) }

    LaunchedEffect(currentLanguage) {
        tSearchPlaceholder = if (currentLanguage == "it") "Cerca un itinerario..."
        else TranslationManager.translate("Cerca un itinerario...", currentLanguage)
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        // Top Bar with Search and Translate (Matching MapPage style exactly)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(48.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                    placeholder = { Text(tSearchPlaceholder, fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.search),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box {
                FloatingActionButton(
                    onClick = { isTranslationMenuVisible = true },
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.translation),
                        contentDescription = "Translate",
                        modifier = Modifier.size(24.dp)
                    )
                }
                DropdownMenu(
                    expanded = isTranslationMenuVisible,
                    onDismissRequest = { isTranslationMenuVisible = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    TranslationManager.supportedLanguages.forEach { langCode ->
                        DropdownMenuItem(
                            text = { Text(TranslationManager.getLanguageName(langCode)) },
                            onClick = {
                                onLanguageChange(langCode)
                                isTranslationMenuVisible = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(SampleItineraries.filter { 
                it.title.contains(searchText, ignoreCase = true) || 
                it.author.contains(searchText, ignoreCase = true) 
            }) { item ->
                ItineraryCard(
                    itineraries = item,
                    onClick = { onItineraryClick(item) }
                )
            }
        }
    }
}

@Composable
fun ItineraryCard(itineraries: Itineraries, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 1. Title (original, not translated)
            Text(
                text = itineraries.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 2. Pills for First and Last POI
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (itineraries.pois.isNotEmpty()) {
                    PoiPill(
                        name = itineraries.pois.first().name,
                        iconRes = R.drawable.route_start,
                        modifier = Modifier.weight(1f)
                    )
                    PoiPill(
                        name = itineraries.pois.last().name,
                        iconRes = R.drawable.route_end,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // 3. Bottom Row: Author and Suggestions on the same line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Author info on the left
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.profile),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = itineraries.author,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }

                // Suggestions on the right (matching author color)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.thumb_up),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = itineraries.suggestionNumber.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun PoiPill(name: String, iconRes: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier
                    .size(14.dp)
                    .align(Alignment.CenterStart),
                tint = Color.Black
            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray,
                maxLines = 1,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
