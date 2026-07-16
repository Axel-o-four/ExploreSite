package com.hciproject.exploresite

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.hciproject.exploresite.poi.POIDetailsPage
import com.hciproject.exploresite.poi.PointOfInterest
import com.hciproject.exploresite.ui.theme.ExploreSiteTheme
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    private val permissionState = mutableStateOf(false)

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        permissions -> permissionState.value =  permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                                                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = applicationContext.packageName
        permissionState.value = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!permissionState.value) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        enableEdgeToEdge()
        setContent {
            ExploreSiteTheme {
                ExploreSiteApp(
                    localPermission = permissionState.value
                )
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun ExploreSiteApp(localPermission: Boolean) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.MAP) }
    var selectedPoi by remember { mutableStateOf<PointOfInterest?>(null) }
    var currentLanguage by rememberSaveable { mutableStateOf("it") }

    // State for translated destination labels
    var translatedLabels by remember { mutableStateOf(AppDestinations.entries.associateWith { it.label }) }

    // Translate destination labels whenever language changes
    LaunchedEffect(currentLanguage) {
        if (currentLanguage == "it") {
            translatedLabels = AppDestinations.entries.associateWith { it.label }
        } else {
            val newLabels = AppDestinations.entries.associateWith { destination ->
                TranslationManager.translate(destination.label, currentLanguage)
            }
            translatedLabels = newLabels
        }
    }

    // Persistent Map State
    var mapLat by rememberSaveable { mutableStateOf(41.9028) }
    var mapLon by rememberSaveable { mutableStateOf(12.4964) }
    var mapZoom by rememberSaveable { mutableStateOf(15.0) }
    var hasInitiallyCentered by rememberSaveable { mutableStateOf(false) }

    if (selectedPoi != null) {
        BackHandler {
            selectedPoi = null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        bottomBar = {
            if (selectedPoi == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(8.dp, 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = Color.White,
                        shape = AbsoluteRoundedCornerShape(48.dp, 48.dp, 48.dp, 48.dp),
                        shadowElevation = 8.dp
                    ) {
                        NavigationBar(
                            containerColor = Color.Transparent,
                            modifier = Modifier.height(56.dp)
                        ) {
                            AppDestinations.entries.forEach { destinations ->
                                val label = translatedLabels[destinations] ?: destinations.label
                                NavigationBarItem(
                                    icon = {
                                        Row(
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .background(
                                                    color = if (destinations == currentDestination) Color.Black else Color.Transparent,
                                                    shape = AbsoluteRoundedCornerShape(48.dp, 48.dp, 48.dp, 48.dp)
                                                )
                                                .padding(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                painter = painterResource(destinations.iconRes),
                                                contentDescription = label,
                                                tint = if (destinations == currentDestination) Color.White else Color.Black,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                label,
                                                color = if (destinations == currentDestination) Color.White else Color.Black
                                            )
                                        }
                                    },
                                    selected = destinations == currentDestination,
                                    onClick = { currentDestination = destinations },
                                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            MapPage(
                modifier = Modifier.fillMaxSize(),
                localPermission = localPermission,
                mapLat = mapLat,
                mapLon = mapLon,
                mapZoom = mapZoom,
                hasInitiallyCentered = hasInitiallyCentered,
                onMapStateChanged = { lat, lon, zoom, centered ->
                    mapLat = lat
                    mapLon = lon
                    mapZoom = zoom
                    hasInitiallyCentered = centered
                },
                onPoiClick = { poi -> selectedPoi = poi },
                uiVisible = (selectedPoi == null && currentDestination == AppDestinations.MAP),
                contentPadding = innerPadding
            )

            if (selectedPoi != null) {
                POIDetailsPage(
                    poi = selectedPoi!!,
                    onBack = { selectedPoi = null },
                    currentLanguage = currentLanguage,
                    onLanguageChange = { currentLanguage = it }
                )
            } else if (currentDestination == AppDestinations.EXPLORE) {
                ExplorePage(
                    modifier = Modifier.padding(innerPadding).background(Color.White),
                    currentLanguage = currentLanguage
                )
            } else if (currentDestination == AppDestinations.PROFILE) {
                ProfilePage(
                    modifier = Modifier.padding(innerPadding).background(Color.White),
                    currentLanguage = currentLanguage
                )
            }
        }
    }
}

enum class AppDestinations(val label: String, val iconRes: Int) {
    EXPLORE("Esplora", R.drawable.explore),
    MAP("Mappa", R.drawable.map),
    PROFILE("Profilo", R.drawable.profile);
}

@Composable
fun ExplorePage(modifier: Modifier = Modifier, currentLanguage: String) {
    var tExplore by remember { mutableStateOf("Esplora") }
    LaunchedEffect(currentLanguage) {
        tExplore = if (currentLanguage == "it") "Esplora" else TranslationManager.translate("Esplora", currentLanguage)
    }
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = tExplore, style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
fun ProfilePage(modifier: Modifier = Modifier, currentLanguage: String) {
    var tProfile by remember { mutableStateOf("Profilo") }
    LaunchedEffect(currentLanguage) {
        tProfile = if (currentLanguage == "it") "Profilo" else TranslationManager.translate("Profilo", currentLanguage)
    }
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = tProfile, style = MaterialTheme.typography.headlineLarge)
    }
}
