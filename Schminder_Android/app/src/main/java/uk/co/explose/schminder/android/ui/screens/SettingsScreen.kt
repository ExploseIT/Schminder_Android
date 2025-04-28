
package uk.co.explose.schminder.android.ui.screens

import androidx.compose.foundation.layout.*
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

@Composable
fun SettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var apg_data by remember { mutableStateOf<m_apg_data?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    AppGlobal.logEvent("test_event", mapOf("origin" to "Schminder - Settings"))

    // ✅ Load data once when screen opens
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            apg_data = AppGlobal.doAPGDataRead()
        } catch (e: Exception) {
            error = e.localizedMessage
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Settings")
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Text("Loading...", style = MaterialTheme.typography.bodyLarge)
        } else if (error != null) {
            Text("Error loading data: $error", style = MaterialTheme.typography.bodyLarge)
        } else if (apg_data != null) {
            Text("App Version: ${apg_data!!.m_versionName}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Meds Loaded: ${apg_data!!.m_med_indiv_info!!.med_indiv_list.count()}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Server Version: ${apg_data!!.m_server_version?.sv_version ?: "Loading..."}",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            coroutineScope.launch {
                try {
                    isLoading = true
                    AppGlobal.doFirebaseInit(context)
                    apg_data = AppGlobal.doAPGDataRead()
                } catch (e: Exception) {
                    error = e.localizedMessage
                } finally {
                    isLoading = false
                }
            }
        }) {
            Text("Reload data")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { navController.navigateUp() }) {
            Text("Back")
        }
    }
}


