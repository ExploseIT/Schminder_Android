
package uk.co.explose.schminder.android

import android.app.Activity
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.co.explose.schminder.android.ui.screens.HomeScreen


import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import uk.co.explose.schminder.android.model.mpp.MedsRepo
import uk.co.explose.schminder.android.ui.components.AppScaffoldWithDrawer
import uk.co.explose.schminder.android.ui.screens.AddMedScreen
import uk.co.explose.schminder.android.ui.screens.AddMedicationScheduleScreen
import uk.co.explose.schminder.android.ui.screens.AppInfoScreen
import uk.co.explose.schminder.android.ui.screens.MedicationTakeScreen
import uk.co.explose.schminder.android.ui.screens.PlanScreen
import uk.co.explose.schminder.android.ui.screens.PrescriptionScanScreen
import uk.co.explose.schminder.android.ui.screens.ScheduleScreen
import uk.co.explose.schminder.android.ui.screens.ScheduleSettingsScreen
import uk.co.explose.schminder.android.ui.screens.SettingsScreen

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchminderMain(initialRoute: String?) {
    val navController = rememberNavController()

    LaunchedEffect(initialRoute) {
        if (!initialRoute.isNullOrEmpty()) {
            navController.navigate(initialRoute)
        }
    }

    AppScaffoldWithDrawer(navController = navController) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("settings") { SettingsScreen(navController) }
            composable("app_info") { AppInfoScreen(navController) }
            composable("schedule_settings") { ScheduleSettingsScreen(navController) }
            composable("add_med") { AddMedScreen(navController) }
            composable("plan") { PlanScreen(navController) }
            composable("schedule") { ScheduleScreen(navController) }
            composable("prescription_scan") { PrescriptionScanScreen(navController) }
            composable("medDetail/{medName}?medId={medId}") { backStackEntry ->
                val context = LocalContext.current
                val scope = rememberCoroutineScope()
                val medName = backStackEntry.arguments?.getString("medName") ?: "Unknown"
                val medId = backStackEntry.arguments?.getString("medId")?.toIntOrNull() ?: 0

                AddMedicationScheduleScreen(
                    navController = navController,
                    medName = medName,
                    medId = medId,
                    onSave = { cMed ->
                        scope.launch {
                            MedsRepo(context).medInsert(cMed)
                        }
                    },
                    onDelete = { id ->
                        navController.popBackStack()
                    }
                )
            }
            // ✅ New MedicationTakeScreen route
            composable("medTake/{medId}") { backStackEntry ->
                val medId = backStackEntry.arguments?.getString("medId")?.toIntOrNull() ?: 0
                MedicationTakeScreen(medId = medId, navController = navController)
            }
        }
    }
}



