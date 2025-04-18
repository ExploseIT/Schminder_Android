
package uk.co.explose.schminder.android

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.co.explose.schminder.android.ui.screens.HomeScreen
import uk.co.explose.schminder.android.ui.screens.SettingsScreen
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import uk.co.explose.schminder.android.ui.components.AppBottomBar
import uk.co.explose.schminder.android.ui.components.AppTopBar
import uk.co.explose.schminder.android.ui.screens.AddMedScreen
import uk.co.explose.schminder.android.ui.screens.ManageScreen
import uk.co.explose.schminder.android.ui.screens.MedicationsScreen
import uk.co.explose.schminder.android.ui.screens.UpdatesScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchminderApp() {
    val navController = rememberNavController()

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
            composable("updates") { UpdatesScreen(navController) }
            composable("medications") { MedicationsScreen(navController) }
            // 👇 New composable for the mockup screen
            composable("mockup") { ManageScreen(navController) }
        }
    }
}

