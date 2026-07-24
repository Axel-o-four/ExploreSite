package com.hciproject.exploresite

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
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hciproject.exploresite.poi.CulturalPOI
import com.hciproject.exploresite.poi.PointOfInterest
import com.hciproject.exploresite.ui.theme.ExploreSiteTheme
import com.hciproject.exploresite.ui.theme.GrayLight

@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    currentLanguage: String = "it",
    onPoiClick: (PointOfInterest) -> Unit = {}
) {
    // Selection of POIs for the "Previously Visited" section
    val previousVisits = remember {
        CulturalPOI.filter { it.name == "Reggia di Caserta" || it.name == "Museo Irpino" }
    }

    var tPreviouslyVisited by remember { mutableStateOf("Visitato in precedenza") }
    LaunchedEffect(currentLanguage) {
        tPreviouslyVisited = if (currentLanguage == "it") "Visitato in precedenza"
                            else TranslationManager.translate("Visitato in precedenza", currentLanguage)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8FF))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ProfileTopSection()
        }
        item {
            ProfileHeaderCard()
        }
        item {
            LevelProgressCard(currentLanguage = currentLanguage)
        }
        item {
            ActionButtonsGrid(currentLanguage = currentLanguage)
        }
        item {
            PreviousVisitsSection(
                title = tPreviouslyVisited,
                pois = previousVisits,
                onPoiClick = onPoiClick
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProfileTopSection(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(18.dp),
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun ProfileHeaderCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
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
            Image(
                painter = ColorPainter(Color.LightGray),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(R.string.profile_name),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun LevelProgressCard(modifier: Modifier = Modifier, currentLanguage: String) {
    var tUserLevel by remember { mutableStateOf("Turista") }
    var tLevel by remember { mutableStateOf("LVL 2") }

    LaunchedEffect(currentLanguage) {
        if (currentLanguage != "it") {
            tUserLevel = TranslationManager.translate("Turista", currentLanguage)
            tLevel = TranslationManager.translate("LVL 2", currentLanguage)
        } else {
            tUserLevel = "Turista"
            tLevel = "LVL 2"
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
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
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp)),
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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = tUserLevel,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = tLevel,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { 0.6f },
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
fun ActionButtonsGrid(modifier: Modifier = Modifier, currentLanguage: String) {
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
            text = tSaved
        )
        ActionButton(
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.create_itinerary,
            text = tCreate
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    modifier: Modifier = Modifier,
    iconRes: Int? = null
) {
    Surface(
        modifier = modifier.aspectRatio(1f),
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
                    .size(48.dp)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (iconRes != null) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
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
        Scaffold(
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(48.dp),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        modifier = Modifier.height(56.dp)
                    ) {
                        NavigationBarItem(
                            icon = {
                                Row(
                                    modifier = Modifier.padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.explore),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Esplora")
                                }
                            },
                            selected = false,
                            onClick = {},
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                        )
                        NavigationBarItem(
                            icon = {
                                Row(
                                    modifier = Modifier.padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.map),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Mappa")
                                }
                            },
                            selected = false,
                            onClick = {},
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                        )
                        NavigationBarItem(
                            icon = {
                                Row(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .background(Color.Black, CircleShape)
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.profile),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Profilo", color = Color.White)
                                }
                            },
                            selected = true,
                            onClick = {},
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                        )
                    }
                }
            }
        ) { paddingValues ->
            ProfilePage(
                modifier = Modifier.padding(paddingValues),
                currentLanguage = "it",
                onPoiClick = {}
            )
        }
    }
}
