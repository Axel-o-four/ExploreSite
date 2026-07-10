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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
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
    onPoiClick: (PointOfInterest) -> Unit
) {
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<Address>>(emptyList()) }
    var mapViewInstance by remember { mutableStateOf<MapView?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(searchText) {
        if (searchText.length > 1) {
            delay(100)
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val results = withContext(Dispatchers.IO) {
                    geocoder.getFromLocationName(searchText, 5)
                }
                suggestions = results ?: emptyList()
            } catch (e: Exception) {
                suggestions = emptyList()
            }
        } else {
            suggestions = emptyList()
        }
    }

    fun onSuggestionClick(address: Address) {
        val targetPoint = GeoPoint(address.latitude, address.longitude)
        mapViewInstance?.let {
            it.controller.animateTo(targetPoint)
            it.controller.setZoom(15.0)
        }
        searchText = address.getAddressLine(0) ?: searchText
        suggestions = emptyList()
    }

    fun performSearch() {
        if (searchText.isBlank()) return
        scope.launch {
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addresses = withContext(Dispatchers.IO) {
                    geocoder.getFromLocationName(searchText, 1)
                }
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    onSuggestionClick(address)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    setBuiltInZoomControls(false)

                    // Initialize with saved state
                    controller.setZoom(mapZoom)
                    controller.setCenter(GeoPoint(mapLat, mapLon))

                    CulturalPOI.forEach { poi ->
                        val poiMarker = Marker(this)
                        poiMarker.position = GeoPoint(poi.latitude, poi.longitude)
                        poiMarker.title = poi.name
                        poiMarker.snippet = poi.description
                        poiMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        poiMarker.setOnMarkerClickListener { _, _ ->
                            onPoiClick(poi)
                            true
                        }
                        overlays.add(poiMarker)
                    }

                    if (localPermission) {
                        val fusedClient = LocationServices.getFusedLocationProviderClient(ctx)
                        fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                            .addOnSuccessListener { location ->
                                location?.let {
                                    // Only center if we haven't done it yet
                                    if (!hasInitiallyCentered) {
                                        val userPoint = GeoPoint(it.latitude, it.longitude)
                                        controller.setCenter(userPoint)
                                        onMapStateChanged(it.latitude, it.longitude, mapZoom, true)
                                    }

                                    val marker = Marker(this)
                                    marker.position = GeoPoint(it.latitude, it.longitude)
                                    marker.title = "La tua posizione"
                                    marker.icon = resources.getDrawable(R.drawable.user_position, null)
                                    overlays.add(marker)

                                    invalidate()
                                }
                            }
                    }

                    addMapListener(object : MapListener {
                        override fun onScroll(event: ScrollEvent?): Boolean {
                            onMapStateChanged(
                                mapCenter.latitude,
                                mapCenter.longitude,
                                zoomLevelDouble,
                                hasInitiallyCentered
                            )
                            return true
                        }

                        override fun onZoom(event: ZoomEvent?): Boolean {
                            onMapStateChanged(
                                mapCenter.latitude,
                                mapCenter.longitude,
                                zoomLevelDouble,
                                hasInitiallyCentered
                            )
                            return true
                        }
                    })

                    mapViewInstance = this
                }
            }
        )

        FloatingActionButton(
            onClick = {
                if (localPermission) {
                    val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                    fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener { location ->
                            location?.let {
                                val userPoint = GeoPoint(it.latitude, it.longitude)
                                mapViewInstance?.controller?.animateTo(userPoint)
                                mapViewInstance?.controller?.setZoom(15.0)
                            }
                        }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp, 4.dp),
            containerColor = Color.White,
            contentColor = Color.Black,
            shape = CircleShape
        ) {
            Icon(
                painter = painterResource(id = R.drawable.center_position),
                contentDescription = "Centra sulla mia posizione",
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .align(Alignment.TopCenter)
        ) {
            Surface(
                shape = RoundedCornerShape(48.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Cerca un luogo...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { performSearch() }
                    )
                )
            }

            if (suggestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        items(suggestions) { address ->
                            val addressLine = address.getAddressLine(0) ?: ""
                            Text(
                                text = addressLine,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSuggestionClick(address) }
                                    .padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}
