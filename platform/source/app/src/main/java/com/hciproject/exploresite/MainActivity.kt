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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.hciproject.exploresite.itinerary.Itineraries
import com.hciproject.exploresite.itinerary.ItineraryDetailsPage
import com.hciproject.exploresite.itinerary.ItineraryPage
import com.hciproject.exploresite.poi.POIDetailsPage
import com.hciproject.exploresite.poi.PointOfInterest
import com.hciproject.exploresite.profile.ProfilePage
import com.hciproject.exploresite.profile.SavedItinerariesPage
import com.hciproject.exploresite.profile.UserDetailsPage
import com.hciproject.exploresite.profile.SettingsPage
import com.hciproject.exploresite.profile.EditProfilePage
import com.hciproject.exploresite.profile.LoginPage
import com.hciproject.exploresite.profile.RegisterPage
import com.hciproject.exploresite.profile.ReportProblemPage
import com.hciproject.exploresite.profile.GamificationDetailsPage
import com.hciproject.exploresite.profile.XpHistoryPage
import com.hciproject.exploresite.profile.MedalsPage
import com.hciproject.exploresite.profile.UserManager
import com.hciproject.exploresite.profile.CurrentUser
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

        // Load the saved user data on startup
        UserManager.loadCurrentUser(applicationContext)

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
    val context = LocalContext.current
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.MAP) }
    var selectedPoi by remember { mutableStateOf<PointOfInterest?>(null) }
    var selectedItinerary by remember { mutableStateOf<Itineraries?>(null) }
    var currentLanguage by rememberSaveable { mutableStateOf("it") }

    // Navigation state within Profile
    var showSavedItineraries by rememberSaveable { mutableStateOf(false) }
    var showUserDetails by rememberSaveable { mutableStateOf(false) }
    var showSettings by rememberSaveable { mutableStateOf(false) }
    var showEditProfile by rememberSaveable { mutableStateOf(false) }
    var showLogin by rememberSaveable { mutableStateOf(false) }
    var showRegister by rememberSaveable { mutableStateOf(false) }
    var showReportProblem by rememberSaveable { mutableStateOf(false) }
    var showGamificationDetails by rememberSaveable { mutableStateOf(false) }
    var showXpHistory by rememberSaveable { mutableStateOf(false) }
    var showMedals by rememberSaveable { mutableStateOf(false) }

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

    // Handle back presses
    if (selectedPoi != null) {
        BackHandler {
            selectedPoi = null
        }
    } else if (showMedals) {
        BackHandler {
            showMedals = false
        }
    } else if (showXpHistory) {
        BackHandler {
            showXpHistory = false
        }
    } else if (showGamificationDetails) {
        BackHandler {
            showGamificationDetails = false
        }
    } else if (showReportProblem) {
        BackHandler {
            showReportProblem = false
        }
    } else if (showRegister) {
        BackHandler {
            showRegister = false
        }
    } else if (showLogin) {
        BackHandler {
            showLogin = false
        }
    } else if (showEditProfile) {
        BackHandler {
            showEditProfile = false
        }
    } else if (showSavedItineraries) {
        BackHandler {
            showSavedItineraries = false
        }
    } else if (showUserDetails) {
        BackHandler {
            showUserDetails = false
        }
    } else if (showSettings) {
        BackHandler {
            showSettings = false
        }
    }
    
    if (selectedItinerary != null) {
        BackHandler {
            selectedItinerary = null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        bottomBar = {
            if (selectedPoi == null && !showSavedItineraries && !showUserDetails && !showSettings && !showEditProfile && !showLogin && !showRegister && !showReportProblem && !showGamificationDetails && !showXpHistory && !showMedals) {
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
                                    onClick = { 
                                        currentDestination = destinations
                                        if (destinations != AppDestinations.PROFILE) {
                                            showSavedItineraries = false
                                            showUserDetails = false
                                            showSettings = false
                                            showEditProfile = false
                                            showLogin = false
                                            showRegister = false
                                            showReportProblem = false
                                            showGamificationDetails = false
                                            showXpHistory = false
                                            showMedals = false
                                        }
                                    },
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
                uiVisible = (selectedPoi == null && selectedItinerary == null && currentDestination == AppDestinations.MAP),
                currentLanguage = currentLanguage,
                onLanguageChange = { currentLanguage = it },
                contentPadding = innerPadding
            )

            if (selectedPoi != null) {
                POIDetailsPage(
                    poi = selectedPoi!!,
                    onBack = { selectedPoi = null },
                    currentLanguage = currentLanguage,
                    onLanguageChange = { currentLanguage = it },
                    onPoiClick = { poi -> selectedPoi = poi }
                )
            } else if (selectedItinerary != null) {
                ItineraryDetailsPage(
                    itinerary = selectedItinerary!!,
                    onBack = { selectedItinerary = null },
                    currentLanguage = currentLanguage,
                    onLanguageChange = { currentLanguage = it },
                    onPoiClick = { poi -> selectedPoi = poi }
                )
            } else if (currentDestination == AppDestinations.EXPLORE) {
                ItineraryPage(
                    modifier = Modifier.padding(innerPadding).background(Color.White),
                    currentLanguage = currentLanguage,
                    onLanguageChange = { currentLanguage = it },
                    onItineraryClick = { itinerary -> selectedItinerary = itinerary }
                )
            } else if (currentDestination == AppDestinations.PROFILE) {
                if (showMedals) {
                    MedalsPage(
                        onBack = { showMedals = false },
                        currentLanguage = currentLanguage,
                        paddingValues = innerPadding
                    )
                } else if (showXpHistory) {
                    XpHistoryPage(
                        onBack = { showXpHistory = false },
                        onPoiClick = { poi -> selectedPoi = poi },
                        currentLanguage = currentLanguage,
                        paddingValues = innerPadding
                    )
                } else if (showGamificationDetails) {
                    GamificationDetailsPage(
                        onBack = { showGamificationDetails = false },
                        onXpHistoryClick = { showXpHistory = true },
                        onMedalsClick = { showMedals = true },
                        currentLanguage = currentLanguage,
                        paddingValues = innerPadding
                    )
                } else if (showReportProblem) {
                    ReportProblemPage(
                        onBack = { showReportProblem = false },
                        currentLanguage = currentLanguage,
                        paddingValues = innerPadding
                    )
                } else if (showRegister) {
                    RegisterPage(
                        onBack = { showRegister = false },
                        onRegisterSuccess = { showRegister = false; showLogin = true },
                        currentLanguage = currentLanguage,
                        paddingValues = innerPadding
                    )
                } else if (showLogin) {
                    LoginPage(
                        onBack = { showLogin = false },
                        onLoginSuccess = { showLogin = false },
                        currentLanguage = currentLanguage,
                        paddingValues = innerPadding
                    )
                } else if (showEditProfile) {
                    EditProfilePage(
                        onBack = { showEditProfile = false },
                        currentLanguage = currentLanguage,
                        paddingValues = innerPadding
                    )
                } else if (showSavedItineraries) {
                    SavedItinerariesPage(
                        onBack = { showSavedItineraries = false },
                        currentLanguage = currentLanguage,
                        paddingValues = innerPadding
                    )
                } else if (showUserDetails) {
                    UserDetailsPage(
                        onBack = { showUserDetails = false },
                        currentLanguage = currentLanguage,
                        paddingValues = innerPadding
                    )
                } else if (showSettings) {
                    SettingsPage(
                        onBack = { showSettings = false },
                        onEditProfileClick = { showEditProfile = true },
                        onRegisterClick = { showRegister = true },
                        onLogoutClick = { 
                            UserManager.logout(context)
                            showSettings = false 
                        },
                        onDeleteAccountClick = {
                            val user = CurrentUser
                            if (user != null) {
                                UserManager.deleteAccount(context, user)
                                showSettings = false
                            }
                        },
                        onReportProblemClick = { showReportProblem = true },
                        currentLanguage = currentLanguage,
                        paddingValues = innerPadding
                    )
                } else {
                    ProfilePage(
                        onSavedItinerariesClick = { showSavedItineraries = true },
                        onUserProfileClick = { showUserDetails = true },
                        onSettingsClick = { showSettings = true },
                        onLoginClick = { showLogin = true },
                        onGamificationClick = { showGamificationDetails = true },
                        modifier = Modifier.fillMaxSize(),
                        paddingValues = innerPadding,
                        currentLanguage = currentLanguage,
                        onPoiClick = { poi -> selectedPoi = poi }
                    )
                }
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
fun ProfilePage(modifier: Modifier = Modifier, currentLanguage: String) {
    var tProfile by remember { mutableStateOf("Profilo") }
    LaunchedEffect(currentLanguage) {
        tProfile = if (currentLanguage == "it") "Profilo" else TranslationManager.translate("Profilo", currentLanguage)
    }
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = tProfile, style = MaterialTheme.typography.headlineLarge)
    }
}
