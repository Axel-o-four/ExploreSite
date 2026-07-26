package com.hciproject.exploresite.itinerary

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hciproject.exploresite.R
import com.hciproject.exploresite.TranslationManager
import com.hciproject.exploresite.poi.PointOfInterest
import com.hciproject.exploresite.profile.CurrentUser
import kotlinx.coroutines.launch

@Composable
fun ItineraryDetailsPage(
    itinerary: Itineraries,
    onBack: () -> Unit,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    onPoiClick: (PointOfInterest) -> Unit
) {
    val scope = rememberCoroutineScope()
    var isTranslationMenuVisible by remember { mutableStateOf(false) }

    // State for local modifications (saved/recommended)
    var isSaved by remember(itinerary.title) { 
        mutableStateOf(CurrentUser?.savedItineraries?.any { it.title == itinerary.title } == true)
    }
    var isRecommended by remember(itinerary.title) { mutableStateOf(false) }

    // Animation states
    val recommendationScale = remember { Animatable(1f) }
    val savedScale = remember { Animatable(1f) }

    // Translations
    var tSalva by remember { mutableStateOf("Salva") }
    var tSalvato by remember { mutableStateOf("Salvato") }
    var tConsiglia by remember { mutableStateOf("Consiglia") }
    var tTappa by remember { mutableStateOf("Tappa") }
    
    LaunchedEffect(currentLanguage) {
        if (currentLanguage == "it") {
            tSalva = "Salva"
            tSalvato = "Salvato"
            tConsiglia = "Consiglia"
            tTappa = "Tappa"
        } else {
            tSalva = TranslationManager.translate("Salva", currentLanguage)
            tSalvato = TranslationManager.translate("Salvato", currentLanguage)
            tConsiglia = TranslationManager.translate("Consiglia", currentLanguage)
            tTappa = TranslationManager.translate("Tappa", currentLanguage)
        }
    }

    val savedColor by animateColorAsState(
        if (isSaved) Color(0xFFFFD700) else Color.Black, // Yellow when saved
        label = "savedColor"
    )
    val recommendationColor by animateColorAsState(
        if (isRecommended) Color.Red else Color.Black,
        label = "recommendationColor"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onBack,
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = R.drawable.left_arrow),
                            contentDescription = "Indietro",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    }
                }

                Box {
                    Surface(
                        onClick = { isTranslationMenuVisible = true },
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 8.dp,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(id = R.drawable.translation),
                                contentDescription = "Traduci",
                                modifier = Modifier.size(24.dp),
                                tint = Color.Black
                            )
                        }
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

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = itinerary.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Author Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.profile),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = itinerary.author,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Save Button
                Surface(
                    onClick = {
                        isSaved = !isSaved
                        val currentUser = CurrentUser
                        if (currentUser != null) {
                            val newList = if (isSaved) {
                                currentUser.savedItineraries + itinerary
                            } else {
                                currentUser.savedItineraries.filter { it.title != itinerary.title }
                            }
                            CurrentUser = currentUser.copy(savedItineraries = newList)
                        }
                        
                        scope.launch {
                            savedScale.animateTo(1.2f, animationSpec = tween(100))
                            savedScale.animateTo(1f, animationSpec = spring(Spring.DampingRatioMediumBouncy))
                        }
                    },
                    shape = RoundedCornerShape(48.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .graphicsLayer(scaleX = savedScale.value, scaleY = savedScale.value)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.saved),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = savedColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSaved) tSalvato else tSalva,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = savedColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Recommend Button
                val displaySuggestionCount = if (isRecommended) itinerary.suggestionNumber + 1 else itinerary.suggestionNumber
                Surface(
                    onClick = {
                        isRecommended = !isRecommended
                        scope.launch {
                            recommendationScale.animateTo(1.2f, animationSpec = tween(100))
                            recommendationScale.animateTo(1f, animationSpec = spring(Spring.DampingRatioMediumBouncy))
                        }
                    },
                    shape = RoundedCornerShape(48.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .graphicsLayer(scaleX = recommendationScale.value, scaleY = recommendationScale.value)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.thumb_up),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = recommendationColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$tConsiglia ($displaySuggestionCount)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = recommendationColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // List of POIs in the itinerary (Stop-overs)
            itinerary.pois.forEachIndexed { index, poi ->
                StopOverItem(
                    poi = poi,
                    index = index + 1,
                    tTappa = tTappa,
                    onClick = { onPoiClick(poi) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun StopOverItem(
    poi: PointOfInterest,
    index: Int,
    tTappa: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = poi.image),
                contentDescription = poi.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Gradient scrim for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                            startY = 150f
                        )
                    )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = poi.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$tTappa $index",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
