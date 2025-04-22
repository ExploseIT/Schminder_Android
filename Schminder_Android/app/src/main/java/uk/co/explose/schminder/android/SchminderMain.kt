
package uk.co.explose.schminder.android

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.co.explose.schminder.android.ui.screens.HomeScreen
import uk.co.explose.schminder.android.ui.screens.SettingsScreen
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.Firebase


import kotlinx.coroutines.delay
import uk.co.explose.schminder.android.core.GlobalAnalytics
import uk.co.explose.schminder.android.ui.components.AppBottomBar
import uk.co.explose.schminder.android.ui.components.AppTopBar
import uk.co.explose.schminder.android.ui.screens.AddMedScreen
import uk.co.explose.schminder.android.ui.screens.ManageScreen
import uk.co.explose.schminder.android.ui.screens.MedicationsScreen
import uk.co.explose.schminder.android.ui.screens.PlanScreen
import uk.co.explose.schminder.android.ui.screens.PrescriptionScanScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchminderMain() {
    val navController = rememberNavController()
    var error by remember { mutableStateOf<String?>(null) }
    var fabExpanded by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val activity = LocalContext.current as? Activity

    LaunchedEffect(Unit) {
        //activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        GlobalAnalytics.logEvent("test_event", mapOf("origin" to "SchminderMain"))
    }

    Scaffold(
        topBar = { AppTopBar(currentRoute = "mockup", navController) },
        bottomBar = { AppBottomBar(currentRoute = "mockup", navController) },
        floatingActionButton = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                if (fabExpanded) {
                    if (isLandscape) {
                        Row(
                            modifier = Modifier
                                .padding(bottom = 100.dp, end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            FabItem(icon = Icons.Default.CameraAlt, label = "Scan") {
                                fabExpanded = false
                                navController.navigate("prescription_scan")
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            FabItem(icon = Icons.Default.Add, label = "Add Medication") {
                                fabExpanded = false
                                navController.navigate("add_med")
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            FabItem(icon = Icons.Default.Edit, label = "TBC") {
                                fabExpanded = false
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .padding(bottom = 100.dp, end = 16.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            FabItem(icon = Icons.Default.CameraAlt, label = "Scan") {
                                fabExpanded = false
                                navController.navigate("prescription_scan")
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            FabItem(icon = Icons.Default.Add, label = "Add Medication") {
                                fabExpanded = false
                                navController.navigate("add_med")
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            FabItem(icon = Icons.Default.Edit, label = "TBC") {
                                fabExpanded = false
                            }
                        }
                    }
                }

                FloatingActionButton(onClick = {
                    fabExpanded = !fabExpanded
                }) {
                    Text(if (fabExpanded) "×" else "+")
                }
            }
        }

    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("settings") { SettingsScreen(navController) }
            composable("add_med") { AddMedScreen(navController) }
            composable("plan") { PlanScreen(navController) }
            composable("medications") { MedicationsScreen(navController) }
            composable("mockup") { ManageScreen(navController) }
            // Add this screen in the future for scan
            composable("prescription_scan") { PrescriptionScanScreen(navController)  }
        }
    }
}

@Composable
fun FabItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        SmallFloatingActionButton(onClick = onClick) {
            Icon(imageVector = icon, contentDescription = label)
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

