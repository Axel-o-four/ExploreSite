package com.hciproject.exploresite.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hciproject.exploresite.R
import com.hciproject.exploresite.TranslationManager
import com.hciproject.exploresite.ui.theme.GrayLight

@Composable
fun GamificationDetailsPage(
    onBack: () -> Unit,
    onXpHistoryClick: () -> Unit,
    onMedalsClick: () -> Unit,
    currentLanguage: String = "it",
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val user = CurrentUser
    val levelInfo = user?.getLevelInfo() ?: LevelInfo(1, "Turista", 0f)

    var tProgressi by remember { mutableStateOf("Progressi") }
    var tMedaglie by remember { mutableStateOf("Medaglie") }
    var tLivello by remember { mutableStateOf("Livello") }
    var tProssimoLivello by remember { mutableStateOf("XP per il prossimo livello") }
    var tLevelLabel by remember { mutableStateOf(levelInfo.label) }
    var tNoMedals by remember { mutableStateOf("Non hai ancora ottenuto alcuna medaglia, continua ad esplorare per ottenere delle medaglie") }

    LaunchedEffect(currentLanguage, levelInfo.label) {
        if (currentLanguage != "it") {
            tProgressi = TranslationManager.translate("Progressi", currentLanguage)
            tMedaglie = TranslationManager.translate("Medaglie", currentLanguage)
            tLivello = TranslationManager.translate("Livello", currentLanguage)
            tProssimoLivello = TranslationManager.translate("XP per il prossimo livello", currentLanguage)
            tLevelLabel = TranslationManager.translate(levelInfo.label, currentLanguage)
            tNoMedals = TranslationManager.translate("Non hai ancora ottenuto alcuna medaglia, continua ad esplorare per ottenere delle medaglie", currentLanguage)
        } else {
            tProgressi = "Progressi"
            tMedaglie = "Medaglie"
            tLivello = "Livello"
            tProssimoLivello = "XP per il prossimo livello"
            tLevelLabel = levelInfo.label
            tNoMedals = "Non hai ancora ottenuto alcuna medaglia, continua ad esplorare per ottenere delle medaglie"
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
                    text = tProgressi,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Medaglie Box
                item {
                    val achievedMedals = ObtainableMedals.filter { it.isAchieved(user) }
                    
                    GamificationBox(
                        title = tMedaglie,
                        onActionClick = onMedalsClick
                    ) {
                        if (achievedMedals.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tNoMedals,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                items(achievedMedals) { medal ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(80.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = medal.iconRes),
                                            contentDescription = medal.name,
                                            modifier = Modifier.size(60.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = medal.name,
                                            fontSize = 10.sp,
                                            textAlign = TextAlign.Center,
                                            maxLines = 2,
                                            lineHeight = 12.sp,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Livello Box
                item {
                    GamificationBox(
                        title = tLivello,
                        onActionClick = onXpHistoryClick
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "LVL ${levelInfo.level}",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black
                            )
                            Text(
                                text = tLevelLabel,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Gray
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            LinearProgressIndicator(
                                progress = { levelInfo.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                color = Color.Black,
                                trackColor = GrayLight
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val xpToNext = 25 * levelInfo.level
                            val currentXpInLevel = (levelInfo.progress * xpToNext).toInt()
                            
                            Text(
                                text = "$currentXpInLevel / $xpToNext XP",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = tProssimoLivello,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GamificationBox(
    title: String,
    onActionClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (onActionClick != null) {
                    IconButton(
                        onClick = onActionClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.left_arrow),
                            contentDescription = "Details",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(20.dp)
                                .rotate(180f)
                        )
                    }
                }
            }
            content()
        }
    }
}
