
package uk.co.explose.schminder.android.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.SyncAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun AppBottomBar(currentRoute: String, navController: NavHostController) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = {
                if (currentRoute != "home") navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentRoute == "plan",
            onClick = {
                if (currentRoute != "plan") navController.navigate("plan") {
                    popUpTo("plan") { inclusive = true }
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.SwapHoriz, contentDescription = "Plan") },
            label = { Text("Plan") }
        )
        NavigationBarItem(
            selected = currentRoute == "schedule",
            onClick = {
                if (currentRoute != "schedule") navController.navigate("schedule") {
                    popUpTo("schedule") { inclusive = true }
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Schedule, contentDescription = "Schedule") },
            label = { Text("Schedule") }
        )
        NavigationBarItem(
            selected = currentRoute == "settings",
            onClick = {
                if (currentRoute != "settings") navController.navigate("settings") {
                    popUpTo("settings") { inclusive = true }
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") }
        )
    }
}

