
package uk.co.explose.schminder.android.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController

@Composable
fun SchminderBottomBar(currentRoute: String, navController: NavHostController) {
    val items = listOf("home", "updates", "medications", "mockup")
    val icons = listOf(Icons.Default.Home, Icons.Default.List, Icons.Default.Medication, Icons.Default.Menu)
    val labels = listOf("Home", "Updates", "Medications", "Manage")

    NavigationBar {
        items.forEachIndexed { index, route ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = labels[index]) },
                label = { Text(labels[index]) },
                selected = currentRoute == route,
                onClick = { navController.navigate(route) }
            )
        }
    }
}
