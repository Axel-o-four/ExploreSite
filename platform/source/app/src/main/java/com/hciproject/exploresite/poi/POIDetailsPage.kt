package com.hciproject.exploresite.poi

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hciproject.exploresite.R
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun POIDetailsPage(
    poi: PointOfInterest,
    onBack: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val sharedPrefs = remember { context.getSharedPreferences("poi_recommendations", Context.MODE_PRIVATE) }
    
    var showPriceDialog by remember { mutableStateOf(false) }
    var showOpeningDialog by remember { mutableStateOf(false) }
    var showJourneyDialog by remember { mutableStateOf(false) }
    var showAccessibilityDialog by remember { mutableStateOf(false) }
    var showNoTicketDialog by remember { mutableStateOf(false) }

    var currentCardIndex by remember { mutableStateOf(0) }
    var isNextAnimation by remember { mutableStateOf(true) }

    var isRecommended by remember(poi.name) { 
        mutableStateOf(sharedPrefs.getBoolean(poi.name, false)) 
    }
    
    val displaySuggestionCount = if (isRecommended) poi.suggestionNumber + 1 else poi.suggestionNumber
    val recommendationColor by animateColorAsState(if (isRecommended) Color.Red else Color.Black)
    val recommendationScale = remember { Animatable(1f) }

    LaunchedEffect(isRecommended) {
        sharedPrefs.edit().putBoolean(poi.name, isRecommended).apply()
    }

    if (showPriceDialog) {
        InfoDialog(title = "Informazione di prezzo", text = poi.detailedPrice) { showPriceDialog = false }
    }
    if (showOpeningDialog) {
        InfoDialog(title = "Informazione orari di apertura", text = poi.detailedOpeningTime) { showOpeningDialog = false }
    }
    if (showJourneyDialog) {
        InfoDialog(title = "Informazione orari di percorrenza", text = poi.detailedJourneyDuration) { showJourneyDialog = false }
    }
    if (showAccessibilityDialog) {
        InfoDialog(title = "Informazione accessibilità", text = poi.detailedAccessibility) { showAccessibilityDialog = false }
    }
    if (showNoTicketDialog) {
        InfoDialog(
            title = "Informazioni biglietteria",
            text = "${poi.name} non dispone di una biglietteria ufficiale online o di un sistema di prenotazione online."
        ) { showNoTicketDialog = false }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Image Section (16:9 with fade)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            ) {
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
                            brush = Brush.verticalGradient(
                                0.5f to Color.Transparent,
                                1.0f to Color.White
                            )
                        )
                )
                Surface(
                    onClick = onBack,
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.8f),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(16.dp)
                        .size(44.dp)
                        .align(Alignment.TopStart)
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
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Title and Price row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = poi.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = String.format(Locale.US, "%.2f€", poi.basePrice),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.clickable { showPriceDialog = true }
                )
            }

            // 3. Address row (Darker gray, clickable, explore icon on the left)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null 
                    ) {
                        val location = poi.location
                        if (location is LinkAnnotation.Url) {
                            uriHandler.openUri(location.url)
                        }
                    }
                    .padding(horizontal = 24.dp, vertical = 2.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.explore),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF424242)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = poi.address,
                    style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.None),
                    color = Color(0xFF424242),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Feature Pills Row 1 (Opening, Journey, Accessibility)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FeaturePill(R.drawable.opening_times, poi.baseOpeningTime, Modifier.weight(1f)) { showOpeningDialog = true }
                FeaturePill(R.drawable.journey_duration, poi.baseJourneyDuration, Modifier.weight(1f)) { showJourneyDialog = true }
                AccessibilityPill(poi.baseAccessibility, Modifier.weight(1f)) { showAccessibilityDialog = true }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 5. Feature Pills Row 2 (Tickets and Recommendations)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Ticket Shop Pill
                FeaturePill(
                    iconId = R.drawable.cart,
                    text = "Biglietteria e prenotazioni",
                    onClick = {
                        val shop = poi.ticketShop
                        if (shop is LinkAnnotation.Url && shop.url.isNotBlank()) {
                            uriHandler.openUri(shop.url)
                        } else {
                            showNoTicketDialog = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                // Recommendation Pill with pop animation
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
                        .height(44.dp)
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
                            modifier = Modifier.size(16.dp),
                            tint = recommendationColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Consiglia questo posto ($displaySuggestionCount)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = recommendationColor,
                            textAlign = TextAlign.Center,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 6. Sliding Info Card Carousel (Continuous)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                AnimatedContent(
                    targetState = currentCardIndex,
                    transitionSpec = {
                        if (isNextAnimation) {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(slideOutHorizontally { width -> -width } + fadeOut())
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(slideOutHorizontally { width -> width } + fadeOut())
                        }
                    },
                    label = "CarouselAnimation"
                ) { index ->
                    when (index) {
                        0 -> InfoCarouselCard(
                            title = "Descrizione",
                            content = poi.description,
                            imageId = poi.descriptionImage,
                            onPrev = { 
                                isNextAnimation = false
                                currentCardIndex = 2 
                            },
                            onNext = { 
                                isNextAnimation = true
                                currentCardIndex = 1 
                            }
                        )
                        1 -> InfoCarouselCard(
                            title = poi.curiosity1,
                            content = poi.curiosity1Title,
                            imageId = poi.curiosity1Image,
                            onPrev = { 
                                isNextAnimation = false
                                currentCardIndex = 0 
                            },
                            onNext = { 
                                isNextAnimation = true
                                currentCardIndex = 2 
                            }
                        )
                        2 -> InfoCarouselCard(
                            title = poi.curiosity2,
                            content = poi.curiosity2Title,
                            imageId = poi.curiosity2Image,
                            onPrev = { 
                                isNextAnimation = false
                                currentCardIndex = 1 
                            },
                            onNext = { 
                                isNextAnimation = true
                                currentCardIndex = 0 
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun InfoCarouselCard(
    title: String,
    content: String,
    imageId: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                textAlign = TextAlign.Center
            )
            
            Row(
                modifier = Modifier.weight(1f).padding(top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    modifier = Modifier.weight(1f),
                    maxLines = 8,
                    lineHeight = 17.sp,
                    textAlign = TextAlign.Start
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Image(
                    painter = painterResource(id = imageId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .aspectRatio(1f) // Strict 1:1 ratio
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPrev,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(painterResource(R.drawable.left_arrow_card), null, Modifier.size(24.dp))
                }
                IconButton(
                    onClick = onNext,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(painterResource(R.drawable.right_arrow_card), null, Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
fun FeaturePill(
    iconId: Int,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(48.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = modifier.height(44.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(painterResource(iconId), null, Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center, // Centers multiline text
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun AccessibilityPill(
    accessibility: POIAccessibility,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(48.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = modifier.height(44.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(painterResource(R.drawable.information), null, Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("|", color = Color.LightGray, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            if (accessibility.wheelchair) Icon(painterResource(R.drawable.wheelchair), null, Modifier.size(16.dp).padding(horizontal = 1.dp))
            if (accessibility.deaf) Icon(painterResource(R.drawable.deaf), null, Modifier.size(16.dp).padding(horizontal = 1.dp))
            if (accessibility.blind) Icon(painterResource(R.drawable.blind), null, Modifier.size(16.dp).padding(horizontal = 1.dp))
        }
    }
}

@Composable
fun InfoDialog(title: String, text: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Text(text) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Ho capito")
            }
        }
    )
}
