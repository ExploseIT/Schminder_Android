
package uk.co.explose.schminder.android

import android.app.Activity
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


import uk.co.explose.schminder.android.core.AppGlobal
import uk.co.explose.schminder.android.model.mpp.MedsRepo
import uk.co.explose.schminder.android.ui.components.AppBottomBar
import uk.co.explose.schminder.android.ui.components.AppTopBar
import uk.co.explose.schminder.android.ui.screens.AddMedScreen
import uk.co.explose.schminder.android.ui.screens.AddMedicationScheduleScreen
import uk.co.explose.schminder.android.ui.screens.ManageScreen
import uk.co.explose.schminder.android.ui.screens.MedicationsScreen
import uk.co.explose.schminder.android.ui.screens.PlanScreen
import uk.co.explose.schminder.android.ui.screens.PrescriptionScanScreen

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchminderMain() {
    val navController = rememberNavController()
    var error by remember { mutableStateOf<String?>(null) }

    val configuration = LocalConfiguration.current

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        //activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        AppGlobal.logEvent("test_event", mapOf("origin" to "SchminderMain"))
    }

    Scaffold(
        topBar = { AppTopBar(currentRoute = "mockup", navController) },
        bottomBar = { AppBottomBar(currentRoute = "mockup", navController) },

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
            composable("medDetail/{medName}") { backStackEntry ->
                val medName = backStackEntry.arguments?.getString("medName")
                AddMedicationScheduleScreen(
                    navController = navController,
                    medName = medName ?: "Unknown",
                    onAdd = { cMed ->
                        println("Add clicked for $cMed->name at $cMed->time - $cMed->frequency for $cMed->count $cMed->unit")
                        scope.launch {
                            val iCount = MedsRepo(context).medInsert(cMed)
                        }
                    }
                )
            }
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

