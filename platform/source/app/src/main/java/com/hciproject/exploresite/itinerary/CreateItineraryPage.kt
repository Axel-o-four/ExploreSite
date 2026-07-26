package com.hciproject.exploresite.itinerary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hciproject.exploresite.R
import com.hciproject.exploresite.TranslationManager
import com.hciproject.exploresite.poi.CulturalPOI
import com.hciproject.exploresite.poi.PointOfInterest
import com.hciproject.exploresite.profile.CurrentUser
import com.hciproject.exploresite.profile.UserManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateItineraryPage(
    onBack: () -> Unit,
    onItineraryCreated: (Itineraries) -> Unit,
    currentLanguage: String = "it",
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val context = LocalContext.current
    val user = CurrentUser

    var title by remember { mutableStateOf("") }
    val pois = remember { mutableStateListOf<PointOfInterest?>(null, null) }

    val allPoisSorted = remember { CulturalPOI.sortedBy { it.name } }

    var tCreaItinerario by remember { mutableStateOf("Crea Itinerario") }
    var tTitolo by remember { mutableStateOf("Titolo dell'itinerario") }
    var tInizio by remember { mutableStateOf("Punto di partenza") }
    var tFine by remember { mutableStateOf("Punto di arrivo") }
    var tTappa by remember { mutableStateOf("Tappa intermedia") }
    var tSalva by remember { mutableStateOf("Salva Itinerario") }
    var tSelezionaPoi by remember { mutableStateOf("Seleziona un luogo") }

    LaunchedEffect(currentLanguage) {
        if (currentLanguage != "it") {
            tCreaItinerario = TranslationManager.translate("Crea Itinerario", currentLanguage)
            tTitolo = TranslationManager.translate("Titolo dell'itinerario", currentLanguage)
            tInizio = TranslationManager.translate("Punto di partenza", currentLanguage)
            tFine = TranslationManager.translate("Punto di arrivo", currentLanguage)
            tTappa = TranslationManager.translate("Tappa intermedia", currentLanguage)
            tSalva = TranslationManager.translate("Salva Itinerario", currentLanguage)
            tSelezionaPoi = TranslationManager.translate("Seleziona un luogo", currentLanguage)
        }
    }

    val canSave = title.isNotBlank() && pois.size >= 2 && pois.all { it != null }

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
            // 1. Left-aligned Header matching EditProfilePage
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
                    text = tCreaItinerario,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // 2. Content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
            ) {
                item {
                    // Title Field
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(tTitolo) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Black,
                            cursorColor = Color.Black
                        ),
                        singleLine = true
                    )
                }

                itemsIndexed(pois) { index, selectedPoi ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val label = when (index) {
                            0 -> tInizio
                            pois.size - 1 -> tFine
                            else -> "$tTappa $index"
                        }
                        
                        PoiDropdown(
                            label = label,
                            selectedPoi = selectedPoi,
                            allPois = allPoisSorted,
                            onPoiSelected = { pois[index] = it },
                            tSelezionaPoi = tSelezionaPoi,
                            showDelete = index != 0 && index != pois.size - 1,
                            onDelete = { pois.removeAt(index) }
                        )

                        // Styled "+" button with shadow between menus
                        if (index < pois.size - 1) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Surface(
                                onClick = { pois.add(index + 1, null) },
                                shape = CircleShape,
                                color = Color.White,
                                shadowElevation = 4.dp,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Aggiungi tappa",
                                        tint = Color.Black,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Save Button area with white background rectangle
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            if (canSave) {
                                val newItinerary = Itineraries(
                                    title = title,
                                    author = user?.fullName ?: "Utente",
                                    suggestionNumber = 0,
                                    saved = false,
                                    poiNames = pois.filterNotNull().map { it.name },
                                    isPrivate = user?.isPrivateItineraries ?: false
                                )
                                if (user != null) {
                                    val updatedUser = user.copy(
                                        createdItineraries = user.createdItineraries + newItinerary
                                    )
                                    UserManager.saveCurrentUser(context, updatedUser)
                                }
                                onItineraryCreated(newItinerary)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        enabled = canSave,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White,
                            disabledContainerColor = Color.LightGray
                        )
                    ) {
                        Text(
                            text = tSalva,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoiDropdown(
    label: String,
    selectedPoi: PointOfInterest?,
    allPois: List<PointOfInterest>,
    onPoiSelected: (PointOfInterest) -> Unit,
    tSelezionaPoi: String,
    showDelete: Boolean = false,
    onDelete: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = selectedPoi?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text(tSelezionaPoi) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.Black,
                        cursorColor = Color.Black
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    allPois.forEach { poi ->
                        DropdownMenuItem(
                            text = { Text(poi.name) },
                            onClick = {
                                onPoiSelected(poi)
                                expanded = false
                            }
                        )
                    }
                }
            }
            if (showDelete) {
                Spacer(modifier = Modifier.width(12.dp))
                Surface(
                    onClick = onDelete,
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = R.drawable.recycle_bin),
                            contentDescription = "Elimina tappa",
                            tint = Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
