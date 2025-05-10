
package uk.co.explose.schminder.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import uk.co.explose.schminder.android.model.server_version.c_ServerVersion
import uk.co.explose.schminder.android.network.RetrofitClient
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import uk.co.explose.schminder.android.core.AppGlobal
import uk.co.explose.schminder.android.core.m_apg_data
import uk.co.explose.schminder.android.ui.components.AppTopBar
import uk.co.explose.schminder.android.ui.viewmodels.HomeScreenVM
import uk.co.explose.schminder.android.ui.viewmodels.SettingsScreenVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    // ✅ Instantiate ViewModel with context (use remember if you're constructing manually)
    val thisVM: SettingsScreenVM = remember { SettingsScreenVM(context) }

    val coroutineScope = rememberCoroutineScope()

    val apg_data = thisVM.apg_data
    val isLoading = thisVM.isLoading
    val error = thisVM.errorMessage

    AppGlobal.logEvent("test_event", mapOf("origin" to "Schminder - Settings"))

    LaunchedEffect(Unit) {
        thisVM.loadVM()
    }

    Scaffold(
        /*topBar = {
            AppTopBar(currentRoute = "settings", navController = navController)
        }*/
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (isLoading) {
                Text("Loading...", style = MaterialTheme.typography.bodyLarge)
            } else if (error != null) {
                Text("Error loading data: $error", style = MaterialTheme.typography.bodyLarge)
            } else if (apg_data!!.isLoaded()) {
                Text("App Version: ${apg_data!!.m_versionName}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Meds Loaded: ${apg_data!!.m_medIndivInfo!!.medIndivList.count()}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Server Version: ${apg_data!!.m_serverVersion?.svVersion ?: "Loading..."}", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                thisVM.loadVM()
            }) {
                Text("Reload data")
            }

        }
    }
}

