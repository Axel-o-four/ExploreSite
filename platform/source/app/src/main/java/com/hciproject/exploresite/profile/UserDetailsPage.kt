package com.hciproject.exploresite.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hciproject.exploresite.R
import com.hciproject.exploresite.TranslationManager

@Composable
fun UserDetailsPage(
    onBack: () -> Unit,
    currentLanguage: String = "it",
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    // Capture the global state into a local immutable constant to allow smart casting
    val user = CurrentUser
    
    // If no user is logged in, we shouldn't be on this page.
    if (user == null) {
        LaunchedEffect(Unit) {
            onBack()
        }
        return
    }

    var tNomeCompleto by remember { mutableStateOf("Nome completo") }
    var tEmail by remember { mutableStateOf("Email") }
    var tDataDiNascita by remember { mutableStateOf("Data di nascita") }
    var tNazionalita by remember { mutableStateOf("Nazionalità") }
    var tDatiPersonali by remember { mutableStateOf("Dati Personali") }

    LaunchedEffect(currentLanguage) {
        if (currentLanguage != "it") {
            tNomeCompleto = TranslationManager.translate("Nome completo", currentLanguage)
            tEmail = TranslationManager.translate("Email", currentLanguage)
            tDataDiNascita = TranslationManager.translate("Data di nascita", currentLanguage)
            tNazionalita = TranslationManager.translate("Nazionalità", currentLanguage)
            tDatiPersonali = TranslationManager.translate("Dati Personali", currentLanguage)
        } else {
            tNomeCompleto = "Nome completo"
            tEmail = "Email"
            tDataDiNascita = "Data di nascita"
            tNazionalita = "Nazionalità"
            tDatiPersonali = "Dati Personali"
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
                    text = tDatiPersonali,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Profile Image
                val profileImageUri = user.profileImageUri
                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.default_profile_picture)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.default_profile_picture),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentScale = ContentScale.Crop
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))

                // User Info Cards - Using the local 'user' reference which is smart-casted to non-null
                UserInfoCard(label = tNomeCompleto, value = user.fullName)
                Spacer(modifier = Modifier.height(16.dp))
                UserInfoCard(label = tEmail, value = user.email)
                Spacer(modifier = Modifier.height(16.dp))
                UserInfoCard(label = tDataDiNascita, value = user.dateOfBirth)
                Spacer(modifier = Modifier.height(16.dp))
                UserInfoCard(label = tNazionalita, value = user.nationality)
            }
        }
    }
}

@Composable
fun UserInfoCard(label: String, value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                fontSize = 12.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                fontSize = 16.sp
            )
        }
    }
}
