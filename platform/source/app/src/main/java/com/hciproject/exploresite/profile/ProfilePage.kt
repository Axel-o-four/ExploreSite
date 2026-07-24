package com.hciproject.exploresite.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hciproject.exploresite.R
import com.hciproject.exploresite.TranslationManager
import com.hciproject.exploresite.poi.CulturalPOI
import com.hciproject.exploresite.poi.PointOfInterest
import com.hciproject.exploresite.ui.theme.ExploreSiteTheme
import com.hciproject.exploresite.ui.theme.GrayLight

@Composable
fun ProfilePage(
    onSavedItinerariesClick: () -> Unit,
    onUserProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLoginClick: () -> Unit,
    onGamificationClick: () -> Unit,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    currentLanguage: String = "it",
    onPoiClick: (PointOfInterest) -> Unit = {}
) {
    val user = CurrentUser
    
    // Selection of POIs based on the user's visited list, limited to last 4
    val previousVisits = remember(user) {
        user?.visitedPois?.takeLast(4)?.reversed()?.mapNotNull { name ->
            CulturalPOI.find { it.name.equals(name, ignoreCase = true) }
        } ?: emptyList()
    }

    var tPreviouslyVisited by remember { mutableStateOf("Visitato in precedenza") }
    LaunchedEffect(currentLanguage) {
        tPreviouslyVisited = if (currentLanguage == "it") "Visitato in precedenza"
                            else TranslationManager.translate("Visitato in precedenza", currentLanguage)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8FF))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 16.dp,
                bottom = paddingValues.calculateBottomPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ProfileTopSection(onClick = onSettingsClick)
            }
            
            if (user != null) {
                val levelInfo = user.getLevelInfo()
                
                item {
                    ProfileHeaderCard(user = user, onClick = onUserProfileClick)
                }
                item {
                    LevelProgressCard(
                        levelInfo = levelInfo,
                        currentLanguage = currentLanguage,
                        onClick = onGamificationClick
                    )
                }
                item {
                    ActionButtonsGrid(
                        currentLanguage = currentLanguage,
                        onSavedItinerariesClick = onSavedItinerariesClick
                    )
                }
                if (previousVisits.isNotEmpty()) {
                    item {
                        PreviousVisitsSection(
                            title = tPreviouslyVisited,
                            pois = previousVisits,
                            onPoiClick = onPoiClick,
                            currentLanguage = currentLanguage
                        )
                    }
                }
            } else {
                item {
                    LoginPromptCard(onLoginClick = onLoginClick, currentLanguage = currentLanguage)
                }
            }
        }
    }
}

@Composable
fun ProfileTopSection(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopEnd
    ) {
        Surface(
            modifier = Modifier
                .size(56.dp)
                .clickable { onClick() },
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(28.dp),
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun ProfileHeaderCard(user: User, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val profileImageUri = user.profileImageUri
            if (profileImageUri != null) {
                AsyncImage(
                    model = profileImageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.default_profile_picture)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.default_profile_picture),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun LoginPromptCard(onLoginClick: () -> Unit, currentLanguage: String, modifier: Modifier = Modifier) {
    var tAccedi by remember { mutableStateOf("Accedi") }
    LaunchedEffect(currentLanguage) {
        tAccedi = if (currentLanguage == "it") "Accedi" else TranslationManager.translate("Accedi", currentLanguage)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onLoginClick() },
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFFF5F5F5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.log_in),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = tAccedi,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun LevelProgressCard(
    levelInfo: LevelInfo,
    currentLanguage: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var label by remember { mutableStateOf(levelInfo.label) }
    var lvlText by remember { mutableStateOf("LVL ${levelInfo.level}") }

    LaunchedEffect(currentLanguage, levelInfo) {
        if (currentLanguage != "it") {
            label = TranslationManager.translate(levelInfo.label, currentLanguage)
            lvlText = TranslationManager.translate("LVL", currentLanguage) + " ${levelInfo.level}"
        } else {
            label = levelInfo.label
            lvlText = "LVL ${levelInfo.level}"
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.level),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = lvlText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = label,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { levelInfo.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color.Black,
                    trackColor = GrayLight
                )
            }
        }
    }
}

@Composable
fun ActionButtonsGrid(
    modifier: Modifier = Modifier,
    currentLanguage: String,
    onSavedItinerariesClick: () -> Unit
) {
    var tSaved by remember { mutableStateOf("Itinerari salvati e creati") }
    var tCreate by remember { mutableStateOf("Crea Itinerario") }

    LaunchedEffect(currentLanguage) {
        if (currentLanguage != "it") {
            tSaved = TranslationManager.translate("Itinerari salvati e creati", currentLanguage)
            tCreate = TranslationManager.translate("Crea Itinerario", currentLanguage)
        } else {
            tSaved = "Itinerari salvati e creati"
            tCreate = "Crea Itinerario"
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ActionButton(
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.books,
            text = tSaved,
            onClick = onSavedItinerariesClick
        )
        ActionButton(
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.create_itinerary,
            text = tCreate,
            onClick = { /* Handle Create Itinerary */ }
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    modifier: Modifier = Modifier,
    iconRes: Int? = null,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (iconRes != null) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun PreviousVisitsSection(
    title: String,
    pois: List<PointOfInterest>,
    onPoiClick: (PointOfInterest) -> Unit,
    currentLanguage: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(pois) { poi ->
                ProfileVisitCard(
                    poi = poi,
                    modifier = Modifier.width(160.dp),
                    onClick = { onPoiClick(poi) }
                )
            }
        }
    }
}

@Composable
fun ProfileVisitCard(
    poi: PointOfInterest,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
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
            Text(
                text = poi.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                maxLines = 2
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePagePreview() {
    ExploreSiteTheme {
        ProfilePage(onSavedItinerariesClick = {}, onUserProfileClick = {}, onSettingsClick = {}, onLoginClick = {}, onGamificationClick = {})
    }
}
