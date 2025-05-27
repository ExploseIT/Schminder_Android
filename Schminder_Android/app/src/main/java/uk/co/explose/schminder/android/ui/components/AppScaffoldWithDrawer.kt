

package uk.co.explose.schminder.android.ui.components

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import uk.co.explose.schminder.android.model.mpp.MedsRepo
import uk.co.explose.schminder.android.ui.components.AppBottomBar
import uk.co.explose.schminder.android.ui.components.SchminderTopBar
import uk.co.explose.schminder.android.ui.screens.AddMedScreen
import uk.co.explose.schminder.android.ui.screens.AddMedicationScheduleScreen
import uk.co.explose.schminder.android.ui.screens.AppInfoScreen
import uk.co.explose.schminder.android.ui.screens.HomeScreen
import uk.co.explose.schminder.android.ui.screens.ScheduleScreen
import uk.co.explose.schminder.android.ui.screens.PlanScreen
import uk.co.explose.schminder.android.ui.screens.PrescriptionScanScreen
import uk.co.explose.schminder.android.ui.screens.ProfileDrawer
import uk.co.explose.schminder.android.ui.screens.ScheduleSettingsScreen
import uk.co.explose.schminder.android.ui.screens.SettingsScreen

@OptIn(ExperimentalGetImage::class)
@Composable
fun AppScaffoldWithDrawer(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val authMode = remember { mutableStateOf("Guest") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Signed in as: ${authMode.value}", modifier = Modifier.padding(16.dp))
                Divider()
                NavigationDrawerItem(
                    label = { Text("Create Profile") },
                    selected = false,
                    onClick = { /* TODO */ }
                )
                NavigationDrawerItem(
                    label = { Text("Edit Profile") },
                    selected = false,
                    onClick = { /* TODO */ }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                SchminderTopBar(
                    userName = authMode.value,
                    showBackButton = currentRoute !in listOf("home", "plan", "medications", "mockup"),
                    onBackClick = { navController.popBackStack() },
                    onNotificationClick = { /* TODO */ },
                    onProfileClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            },
            bottomBar = {
                AppBottomBar(currentRoute = currentRoute, navController = navController)
            }
        ) { innerPadding ->
            content(innerPadding) // Pass padding to actual screen content
        }
    }
}

