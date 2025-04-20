package uk.co.explose.schminder.android.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import uk.co.explose.schminder.android.core.GlobalAnalytics


@Composable
fun MedicationsScreen(navController: NavHostController) {
    GlobalAnalytics.logEvent("test_event", mapOf("origin" to "Schminder - Medication"))
    Scaffold(
        //bottomBar = { AppBottomBar(currentRoute = "mockup", navController) }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {

        }
    }
}