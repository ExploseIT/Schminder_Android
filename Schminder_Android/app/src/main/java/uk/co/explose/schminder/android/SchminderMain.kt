
package uk.co.explose.schminder.android

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchminderMain () {
    val navController = rememberNavController()
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        GlobalAnalytics.logEvent("test_event", mapOf("origin" to "SchminderMain"))
    }
    Scaffold(
        topBar = { AppTopBar(currentRoute = "mockup", navController) },
        bottomBar = { AppBottomBar(currentRoute = "mockup", navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
            },
                //modifier = Modifier.navigationBarsPadding()
                modifier = Modifier.offset(y = (-100).dp)
                ) {
                Text("+")
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
            // 👇 New composable for the mockup screen
            composable("mockup") { ManageScreen(navController) }
        }
    }
}

