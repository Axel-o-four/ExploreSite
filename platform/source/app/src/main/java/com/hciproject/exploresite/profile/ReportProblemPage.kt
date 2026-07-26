package com.hciproject.exploresite.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hciproject.exploresite.R
import com.hciproject.exploresite.TranslationManager

@Composable
fun ReportProblemPage(
    onBack: () -> Unit,
    currentLanguage: String = "it",
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    var problemText by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    var tSegnala by remember { mutableStateOf("Segnala un problema") }
    var tDescrivi by remember { mutableStateOf("Descrivi il problema riscontrato") }
    var tInvia by remember { mutableStateOf("Invia segnalazione") }
    var tSuccesso by remember { mutableStateOf("Segnalazione inviata con successo. Grazie per il tuo feedback!") }
    var tHoCapito by remember { mutableStateOf("Ho capito") }

    LaunchedEffect(currentLanguage) {
        if (currentLanguage != "it") {
            tSegnala = TranslationManager.translate("Segnala un problema", currentLanguage)
            tDescrivi = TranslationManager.translate("Descrivi il problema riscontrato", currentLanguage)
            tInvia = TranslationManager.translate("Invia segnalazione", currentLanguage)
            tSuccesso = TranslationManager.translate("Segnalazione inviata con successo. Grazie per il tuo feedback!", currentLanguage)
            tHoCapito = TranslationManager.translate("Ho capito", currentLanguage)
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false; onBack() },
            confirmButton = {
                TextButton(onClick = { showSuccessDialog = false; onBack() }) {
                    Text(tHoCapito)
                }
            },
            text = { Text(tSuccesso) },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
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
                    text = tSegnala,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = tDescrivi,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                OutlinedTextField(
                    value = problemText,
                    onValueChange = { problemText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    placeholder = { Text("...") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { showSuccessDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    enabled = problemText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(tInvia, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
