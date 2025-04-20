
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
import androidx.compose.runtime.setValue
import uk.co.explose.schminder.android.core.GlobalAnalytics

@Composable
fun SettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val versionName = context.packageManager
        .getPackageInfo(context.packageName, 0).versionName
    var serverVersion by remember { mutableStateOf<c_ServerVersion?>(null) }

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    GlobalAnalytics.logEvent("test_event", mapOf("origin" to "Schminder - Settings"))

    LaunchedEffect(Unit) {
        try {
            val result = fetchServerVersion()
            serverVersion = result
        } catch (e: Exception) {
            error = "Failed to fetch server version"
        } finally {
            isLoading = false
        }
    }
    /*
    when {
        isLoading -> Text("Server Version: Loading...")
        error != null -> Text("Server Version: ${error}")
        serverVersion != null -> Text("Server Version: ${serverVersion!!.sv_version}")
    }
*/

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("Settings")
        Spacer(modifier = Modifier.height(16.dp))
        Text("App Version: $versionName", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Server Version: ${serverVersion?.sv_version ?: "Loading..."}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.navigateUp() }) {
            Text("Back")
        }
    }
}

suspend fun fetchServerVersion(): c_ServerVersion? {
    return try {
        val response = RetrofitClient.instance.getServerVersion()
        if (response.isSuccessful) {
            response.body()
        } else {
            null // handle error (optional: log or throw)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}