package com.hciproject.exploresite

import android.location.Address
import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.hciproject.exploresite.poi.CulturalPOI
import com.hciproject.exploresite.poi.POICategory
import com.hciproject.exploresite.poi.PointOfInterest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.*

@Composable
@androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
fun MapPage(
    modifier: Modifier = Modifier,
    localPermission: Boolean,
    mapLat: Double,
    mapLon: Double,
    mapZoom: Double,
    hasInitiallyCentered: Boolean,
    onMapStateChanged: (Double, Double, Double, Boolean) -> Unit,
    onPoiClick: (PointOfInterest) -> Unit,
    uiVisible: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    currentLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<Address>>(emptyList()) }
    var mapViewInstance by remember { mutableStateOf<MapView?>(null) }
    val scope = rememberCoroutineScope()

    // UI Translation States
    var tSearchPlaceholder by remember { mutableStateOf("Cerca un luogo...") }
    var isTranslationMenuVisible by remember { mutableStateOf(false) }

    // Filter State
    var selectedCategories by rememberSaveable { mutableStateOf(POICategory.entries.toSet()) }
    var isFilterMenuVisible by remember { mutableStateOf(false) }

    // User location for suggestion system
    var userLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    // Translate UI strings
    LaunchedEffect(currentLanguage) {
        tSearchPlaceholder = if (currentLanguage == "it") "Cerca un luogo..." 
                            else TranslationManager.translate("Cerca un luogo...", currentLanguage)
    }

    // Initial Centering Logic and tracking user location
    LaunchedEffect(localPermission, hasInitiallyCentered) {
        if (localPermission) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    location?.let {
                        userLocation = it.latitude to it.longitude
                        if (!hasInitiallyCentered) {
                            val userPoint = GeoPoint(it.latitude, it.longitude)
                            mapViewInstance?.controller?.setCenter(userPoint)
                            onMapStateChanged(it.latitude, it.longitude, mapZoom, true)
                        }
                    }
                }
        }
    }

    // Overlay Management Logic
    LaunchedEffect(selectedCategories, localPermission, mapViewInstance, hasInitiallyCentered) {
        mapViewInstance?.let { mapView ->
            mapView.overlays.clear()
            CulturalPOI.filter { it.type in selectedCategories }.forEach { poi ->
                val poiMarker = Marker(mapView)
                poiMarker.position = GeoPoint(poi.latitude, poi.longitude)
                poiMarker.title = poi.name
                poiMarker.snippet = poi.address
                poiMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                poiMarker.icon = when(poi.type) {
                    POICategory.CULINARY -> context.resources.getDrawable(R.drawable.culinary_position, null)
                    POICategory.ARCHEOLOGICAL -> context.resources.getDrawable(R.drawable.archeological_position, null)
                    POICategory.MUSEUM -> context.resources.getDrawable(R.drawable.museum_position, null)
                    POICategory.RELIGIOUS -> context.resources.getDrawable(R.drawable.religious_position, null)
                    POICategory.ARCHITECTURE -> context.resources.getDrawable(R.drawable.architecture_position, null)
                }
                poiMarker.setOnMarkerClickListener { _, _ -> onPoiClick(poi); true }
                mapView.overlays.add(poiMarker)
            }
            if (localPermission) {
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                fusedClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val userMarker = Marker(mapView)
                        userMarker.position = GeoPoint(it.latitude, it.longitude)
                        userMarker.title = "Tu sei qui"
                        userMarker.icon = context.resources.getDrawable(R.drawable.user_position, null)
                        mapView.overlays.add(userMarker)
                        mapView.invalidate()
                    }
                }
            }
            mapView.invalidate()
        }
    }

    LaunchedEffect(searchText) {
        if (searchText.length > 1) {
            delay(100)
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val results = withContext(Dispatchers.IO) { geocoder.getFromLocationName(searchText, 5) }
                suggestions = results ?: emptyList()
            } catch (e: Exception) { suggestions = emptyList() }
        } else { suggestions = emptyList() }
    }

    fun onSuggestionClick(address: Address) {
        val targetPoint = GeoPoint(address.latitude, address.longitude)
        onMapStateChanged(targetPoint.latitude, targetPoint.longitude, 15.0, true)
        mapViewInstance?.let {
            it.controller.animateTo(targetPoint)
            it.controller.setZoom(15.0)
        }
        searchText = address.getAddressLine(0) ?: searchText
        suggestions = emptyList()
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    setBuiltInZoomControls(false)
                    controller.setZoom(mapZoom)
                    controller.setCenter(GeoPoint(mapLat, mapLon))
                    addMapListener(object : MapListener {
                        override fun onScroll(event: ScrollEvent?): Boolean {
                            onMapStateChanged(mapCenter.latitude, mapCenter.longitude, zoomLevelDouble, true)
                            return true
                        }
                        override fun onZoom(event: ZoomEvent?): Boolean {
                            onMapStateChanged(mapCenter.latitude, mapCenter.longitude, zoomLevelDouble, true)
                            return true
                        }
                    })
                    mapViewInstance = this
                }
            },
            update = { /* Updates handled by Effects */ }
        )

        if (uiVisible) {
            Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
                // Centering Button
                FloatingActionButton(
                    onClick = {
                        if (localPermission) {
                            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener { location ->
                                location?.let {
                                    val userPoint = GeoPoint(it.latitude, it.longitude)
                                    mapViewInstance?.controller?.animateTo(userPoint)
                                    mapViewInstance?.controller?.setZoom(15.0)
                                    onMapStateChanged(it.latitude, it.longitude, 15.0, true)
                                }
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                    containerColor = Color.White, contentColor = Color.Black, shape = CircleShape
                ) { Icon(painter = painterResource(id = R.drawable.center_position), contentDescription = "Center", modifier = Modifier.size(24.dp)) }

                // Top Search UI
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.TopCenter)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(48.dp), color = Color.White, shadowElevation = 8.dp) {
                            TextField(
                                value = searchText, 
                                onValueChange = { searchText = it }, 
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                                placeholder = { Text(tSearchPlaceholder, fontSize = 12.sp) },
                                leadingIcon = { Icon(painter = painterResource(R.drawable.search), contentDescription = null, modifier = Modifier.size(24.dp)) },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, disabledContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = { if (searchText.isNotBlank()) scope.launch { val geocoder = Geocoder(context, Locale.getDefault()); try { val addresses = withContext(Dispatchers.IO) { geocoder.getFromLocationName(searchText, 1) }; if (!addresses.isNullOrEmpty()) onSuggestionClick(addresses[0]) } catch (e: Exception) {} } })
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box {
                            FloatingActionButton(onClick = { isFilterMenuVisible = true }, containerColor = Color.White, contentColor = Color.Black, shape = CircleShape, modifier = Modifier.size(56.dp)) {
                                Icon(painter = painterResource(id = R.drawable.filter), contentDescription = "Filtra", modifier = Modifier.size(24.dp))
                            }
                            DropdownMenu(expanded = isFilterMenuVisible, onDismissRequest = { isFilterMenuVisible = false }, modifier = Modifier.background(Color.White)) {
                                POICategory.entries.forEach { category ->
                                    var label by remember { mutableStateOf(category.name.lowercase().replaceFirstChar { it.uppercase() }) }
                                    LaunchedEffect(currentLanguage) { label = if(currentLanguage=="it") (when(category){POICategory.CULINARY->"Gastronomia";POICategory.ARCHEOLOGICAL->"Archeologia";POICategory.MUSEUM->"Musei";POICategory.RELIGIOUS->"Religione";POICategory.ARCHITECTURE->"Architettura"}) else TranslationManager.translate(category.name, currentLanguage) }
                                    DropdownMenuItem(text = { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = category in selectedCategories, onCheckedChange = null); Spacer(modifier = Modifier.width(8.dp)); Text(label) } }, onClick = { selectedCategories = if (category in selectedCategories) selectedCategories - category else selectedCategories + category })
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box {
                            FloatingActionButton(onClick = { isTranslationMenuVisible = true }, containerColor = Color.White, contentColor = Color.Black, shape = CircleShape, modifier = Modifier.size(56.dp)) {
                                Icon(painter = painterResource(id = R.drawable.translation), contentDescription = "Translate", modifier = Modifier.size(24.dp))
                            }
                            DropdownMenu(expanded = isTranslationMenuVisible, onDismissRequest = { isTranslationMenuVisible = false }, modifier = Modifier.background(Color.White)) {
                                TranslationManager.supportedLanguages.forEach { langCode ->
                                    DropdownMenuItem(text = { Text(TranslationManager.getLanguageName(langCode)) }, onClick = { onLanguageChange(langCode); isTranslationMenuVisible = false })
                                }
                            }
                        }
                    }

                    // Map Page Suggestion System
                    if (searchText.isEmpty() && suggestions.isEmpty()) {
                        MapPageSuggestionSystem(
                            userLat = userLocation?.first,
                            userLon = userLocation?.second,
                            currentLanguage = currentLanguage,
                            onPoiClick = onPoiClick
                        )
                    }

                    if (suggestions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(24.dp), color = Color.White, shadowElevation = 8.dp) {
                                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                                    items(suggestions) { address ->
                                        val line = address.getAddressLine(0) ?: ""
                                        Text(text = line, modifier = Modifier.fillMaxWidth().clickable { onSuggestionClick(address) }.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(128.dp)) 
                        }
                    }
                }
            }
        }
    }
}
