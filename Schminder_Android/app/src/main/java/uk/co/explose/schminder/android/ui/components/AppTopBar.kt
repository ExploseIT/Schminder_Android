
package uk.co.explose.schminder.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    currentRoute: String,
    navController: NavHostController,
    authMode: String,
    onProfileClick: () -> Unit
) {
    val topLevelRoutes = listOf("home", "plan", "medications", "manage")

    val resolvedTitle = when {
        currentRoute.startsWith("settings") -> "Settings"
        currentRoute.startsWith("add_med") -> "Add Medication"
        currentRoute.startsWith("medDetail") -> "Medication Details"
        currentRoute.startsWith("prescription_scan") -> "Scan Prescription"
        else -> ""
    }

    TopAppBar(
        navigationIcon = {
            if (currentRoute !in topLevelRoutes) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = resolvedTitle,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
                Text(
                    text = authMode,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .clickable { onProfileClick() }
                )
            }
        }
    )
}
