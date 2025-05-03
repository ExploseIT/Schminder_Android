
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
            selected = currentRoute == "medications",
            onClick = {
                if (currentRoute != "medications") navController.navigate("medications") {
                    popUpTo("medications") { inclusive = true }
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Medication, contentDescription = "Medications") },
            label = { Text("Medications") }
        )
        NavigationBarItem(
            selected = currentRoute == "mockup" || currentRoute == "manage",
            onClick = {
                if (currentRoute != "mockup") navController.navigate("mockup") {
                    popUpTo("mockup") { inclusive = true }
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Menu, contentDescription = "Manage") },
            label = { Text("Manage") }
        )
    }
}

