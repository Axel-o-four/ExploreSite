package com.hciproject.exploresite.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hciproject.exploresite.R
import com.hciproject.exploresite.TranslationManager

@Composable
fun SettingsPage(
    onBack: () -> Unit,
    onEditProfileClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onReportProblemClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    currentLanguage: String = "it",
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    var tImpostazioni by remember { mutableStateOf("Impostazioni") }
    var tAccount by remember { mutableStateOf("Account") }
    var tAzioni by remember { mutableStateOf("Azioni") }
    var tModificaProfilo by remember { mutableStateOf("Modifica profilo") }
    var tPrivacy by remember { mutableStateOf("Privacy") }
    var tNotifiche by remember { mutableStateOf("Notifiche") }
    
    var tSegnalaProblema by remember { mutableStateOf("Segnala un problema") }
    var tRegistraNuovoAccount by remember { mutableStateOf("Registra un nuovo account") }
    var tLogout by remember { mutableStateOf("Log-out") }
    var tEliminaAccount by remember { mutableStateOf("Elimina account") }

    LaunchedEffect(currentLanguage) {
        if (currentLanguage != "it") {
            tImpostazioni = TranslationManager.translate("Impostazioni", currentLanguage)
            tAccount = TranslationManager.translate("Account", currentLanguage)
            tAzioni = TranslationManager.translate("Azioni", currentLanguage)
            tModificaProfilo = TranslationManager.translate("Modifica profilo", currentLanguage)
            tPrivacy = TranslationManager.translate("Privacy", currentLanguage)
            tNotifiche = TranslationManager.translate("Notifiche", currentLanguage)
            tSegnalaProblema = TranslationManager.translate("Segnala un problema", currentLanguage)
            tRegistraNuovoAccount = TranslationManager.translate("Registra un nuovo account", currentLanguage)
            tLogout = TranslationManager.translate("Log-out", currentLanguage)
            tEliminaAccount = TranslationManager.translate("Elimina account", currentLanguage)
        } else {
            tImpostazioni = "Impostazioni"
            tAccount = "Account"
            tAzioni = "Azioni"
            tModificaProfilo = "Modifica profilo"
            tPrivacy = "Privacy"
            tNotifiche = "Notifiche"
            tSegnalaProblema = "Segnala un problema"
            tRegistraNuovoAccount = "Registra un nuovo account"
            tLogout = "Log-out"
            tEliminaAccount = "Elimina account"
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
                    text = tImpostazioni,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    SettingsSection(title = tAccount) {
                        SettingsItem(
                            iconRes = R.drawable.profile,
                            label = tModificaProfilo,
                            onClick = onEditProfileClick,
                            enabled = CurrentUser != null
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF5F5F5))
                        SettingsItem(
                            iconRes = R.drawable.privacy,
                            label = tPrivacy,
                            onClick = { /* Handle Privacy */ }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF5F5F5))
                        SettingsItem(
                            iconRes = R.drawable.notifications,
                            label = tNotifiche,
                            onClick = { /* Handle Notifications */ }
                        )
                    }
                }

                item {
                    SettingsSection(title = tAzioni) {
                        SettingsItem(
                            iconRes = R.drawable.flag,
                            label = tSegnalaProblema,
                            onClick = onReportProblemClick
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF5F5F5))
                        SettingsItem(
                            imageVector = Icons.Default.PersonAdd,
                            label = tRegistraNuovoAccount,
                            onClick = onRegisterClick
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF5F5F5))
                        SettingsItem(
                            iconRes = R.drawable.log_out,
                            label = tLogout,
                            onClick = onLogoutClick,
                            enabled = CurrentUser != null
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF5F5F5))
                        SettingsItem(
                            iconRes = R.drawable.recycle_bin,
                            label = tEliminaAccount,
                            onClick = onDeleteAccountClick,
                            enabled = CurrentUser != null,
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconRes: Int? = null,
    imageVector: ImageVector? = null,
    enabled: Boolean = true,
    tint: Color = Color.Black
) {
    val contentAlpha = if (enabled) 1f else 0.4f
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconRes != null) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = tint.copy(alpha = contentAlpha)
            )
        } else if (imageVector != null) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = tint.copy(alpha = contentAlpha)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            color = tint.copy(alpha = contentAlpha)
        )
    }
}
