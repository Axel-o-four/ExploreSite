package com.hciproject.exploresite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hciproject.exploresite.ui.theme.ExploreSiteTheme
import org.osmdroid.config.Configuration
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    private val permissionState =
        mutableStateOf(false)

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

// @PreviewScreenSizes
@SuppressLint("MissingPermission")
@Composable
fun ExploreSiteApp(localPermission: Boolean) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.MAP) }

    Scaffold(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        containerColor = Color.White,
        bottomBar = {
            Box(
                modifier = Modifier.fillMaxWidth().padding(8.dp, 4.dp),
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
                            NavigationBarItem(
                                icon = {
                                    Row(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .background(
                                                color = if (destinations == currentDestination) Color.Black else Color.Transparent,
                                                shape = AbsoluteRoundedCornerShape(
                                                    48.dp,
                                                    48.dp,
                                                    48.dp,
                                                    48.dp
                                                )
                                            )
                                            .padding(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(destinations.iconRes),
                                            contentDescription = destinations.label,
                                            tint = if (destinations == currentDestination) Color.White else Color.Black,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            destinations.label,
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
    ) { innerPadding ->
        when (currentDestination) {
            AppDestinations.MAP -> MapPage(modifier = Modifier.padding(innerPadding), localPermission = localPermission)
            AppDestinations.EXPLORE -> ExplorePage(modifier = Modifier.padding(innerPadding))
            AppDestinations.PROFILE -> ProfilePage(modifier = Modifier.padding(innerPadding))
        }
    }
}

enum class AppDestinations(
    val label: String,
    val iconRes: Int,
) {
    EXPLORE("Esplora", R.drawable.explore),
    MAP("Mappa", R.drawable.map),
    PROFILE("Profilo", R.drawable.profile);
}

/*@Composable
fun MapPage(modifier: Modifier = Modifier, localPermission: Boolean) {

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            MapView(context).apply {

                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)

                controller.setZoom(15.0)
                controller.setCenter(
                    GeoPoint(
                        41.9028,
                        12.4964
                    )
                )

                if(localPermission) {
                    val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                    fusedClient.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener { location ->
                            location?.let {
                                controller.setCenter(GeoPoint(it.latitude, it.longitude))

                                val marker = Marker(this)
                                marker.position = GeoPoint(it.latitude, it.longitude)
                                marker.title = "La tua posizione"
                                overlays.add(marker)

                                invalidate()
                            }
                        }
                }
            }
        }
    )
}*/

@Composable
fun ExplorePage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Esplora",
            fontSize = 32.sp,
            color = Color.Black
        )
    }
}

@Composable
fun ProfilePage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Profilo",
            fontSize = 32.sp,
            color = Color.Black
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExploreSiteTheme {
        Greeting("Android")
    }
}