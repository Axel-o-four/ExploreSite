package com.hciproject.exploresite.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hciproject.exploresite.R
import com.hciproject.exploresite.TranslationManager

@Composable
fun MedalsPage(
    onBack: () -> Unit,
    currentLanguage: String = "it",
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val user = CurrentUser
    var selectedMedal by remember { mutableStateOf<Medal?>(null) }

    var tMedaglie by remember { mutableStateOf("Medaglie") }
    var tHoCapito by remember { mutableStateOf("Ho capito") }

    LaunchedEffect(currentLanguage) {
        if (currentLanguage != "it") {
            tMedaglie = TranslationManager.translate("Medaglie", currentLanguage)
            tHoCapito = TranslationManager.translate("Ho capito", currentLanguage)
        } else {
            tMedaglie = "Medaglie"
            tHoCapito = "Ho capito"
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
                    text = tMedaglie,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(ObtainableMedals) { medal ->
                    val achieved = medal.isAchieved(user)
                    MedalGridItem(
                        medal = medal,
                        achieved = achieved,
                        onClick = { selectedMedal = medal }
                    )
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
fun MedalGridItem(
    medal: Medal,
    achieved: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = medal.iconRes),
            contentDescription = medal.name,
            modifier = Modifier
                .size(80.dp)
                .alpha(if (achieved) 1f else 0.4f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = medal.name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 14.sp
        )
    }
}
