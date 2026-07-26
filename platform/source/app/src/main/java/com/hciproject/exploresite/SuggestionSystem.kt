package com.hciproject.exploresite

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hciproject.exploresite.poi.CulturalPOI
import com.hciproject.exploresite.poi.PointOfInterest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.*

/**
 * Calculates a "cheap" squared Euclidean distance for sorting purposes.
 * This is much faster than the Haversine formula and sufficient for finding nearby POIs.
 */
private fun calculateCheapDistanceSq(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val dLat = lat2 - lat1
    val dLon = lon2 - lon1
    return dLat * dLat + dLon * dLon
}

@Composable
fun SuggestionItem(
    poi: PointOfInterest,
    modifier: Modifier = Modifier,
    currentLanguage: String,
    fontSize: TextUnit = 13.sp,
    padding: Dp = 12.dp,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Lower elevation is cheaper to render
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
                    .padding(padding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = poi.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = fontSize,
                    modifier = Modifier.weight(1f),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = String.format(Locale.US, "%.2f€", poi.basePrice),
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = fontSize
                )
            }
        }
    }
}

@Composable
fun MapPageSuggestionSystem(
    userLat: Double?,
    userLon: Double?,
    currentLanguage: String,
    onPoiClick: (PointOfInterest) -> Unit
) {
    if (userLat == null || userLon == null) return

    // Offload distance calculation to background thread
    val nearestPoi by produceState<PointOfInterest?>(initialValue = null, userLat, userLon) {
        value = withContext(Dispatchers.Default) {
            CulturalPOI.minByOrNull { calculateCheapDistanceSq(userLat, userLon, it.latitude, it.longitude) }
        }
    }

    nearestPoi?.let {
        Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            SuggestionItem(poi = it, currentLanguage = currentLanguage, fontSize = 13.sp) { onPoiClick(it) }
        }
    }
}

@Composable
fun POIDetailSuggestionSystem(
    currentPoi: PointOfInterest,
    currentLanguage: String,
    onPoiClick: (PointOfInterest) -> Unit
) {
    // Optimization: Async calculation with a delay to let the initial transition finish
    val suggestions by produceState<List<PointOfInterest>>(initialValue = emptyList(), currentPoi) {
        value = withContext(Dispatchers.Default) {
            delay(300) // Delay to spread out the CPU spike
            CulturalPOI
                .filter { it.name != currentPoi.name }
                .sortedBy { calculateCheapDistanceSq(currentPoi.latitude, currentPoi.longitude, it.latitude, it.longitude) }
                .take(2)
        }
    }

    if (suggestions.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            var title by remember(currentLanguage) { mutableStateOf("Ti potrebbe piacere anche…") }

            LaunchedEffect(currentLanguage) {
                if (currentLanguage != "it") {
                    title = TranslationManager.translate("Ti potrebbe piacere anche…", currentLanguage)
                } else {
                    title = "Ti potrebbe piacere anche…"
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                suggestions.forEach { poi ->
                    SuggestionItem(
                        poi = poi,
                        modifier = Modifier.weight(1f),
                        currentLanguage = currentLanguage,
                        fontSize = 10.sp, // Smaller font for POIDetailsPage suggestions
                        padding = 8.dp,   // Reduced padding
                        onClick = { onPoiClick(poi) }
                    )
                }
            }
        }
    }
}
