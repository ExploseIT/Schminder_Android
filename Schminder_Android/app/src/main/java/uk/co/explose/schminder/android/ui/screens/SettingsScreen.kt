package uk.co.explose.schminder.android.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import uk.co.explose.schminder.android.core.AppRepo
import uk.co.explose.schminder.android.ui.components.ManageItem
import uk.co.explose.schminder.android.ui.components.SectionHeader


@Composable
fun SettingsScreen(navController: NavHostController) {
    AppRepo.logEvent("test_event", mapOf("origin" to "Schminder - Manage"))
    Scaffold(
        //bottomBar = { AppBottomBar(currentRoute = "mockup", navController) }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            item { SectionHeader("Settings") }
            item { ManageItem(icon = Icons.Filled.Info, label = "App Info") { navController.navigate("app_info") } }
            item { ManageItem(icon = Icons.Filled.Info, label = "Schedule Settings") { navController.navigate("schedule_settings") } }
            item { ManageItem(icon = Icons.Filled.Lock, label = "Manage Email & Password") { /* nav */ } }
            item { ManageItem(icon = Icons.Filled.Notifications, label = "Reminders Troubleshooting") { /* nav */ } }
        }
    }
}
