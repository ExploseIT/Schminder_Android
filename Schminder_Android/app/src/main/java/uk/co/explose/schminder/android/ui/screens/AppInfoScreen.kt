
package uk.co.explose.schminder.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.runtime.rememberCoroutineScope
import uk.co.explose.schminder.android.core.AppRepo
import uk.co.explose.schminder.android.ui.viewmodels.SettingsScreenVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfoScreen(navController: NavHostController) {
    val context = LocalContext.current
    // ✅ Instantiate ViewModel with context (use remember if you're constructing manually)
    val thisVM: SettingsScreenVM = remember { SettingsScreenVM(context) }

    val coroutineScope = rememberCoroutineScope()

    val error = thisVM.errorMessage

    val isLoading = thisVM.isLoading

    AppRepo.logEvent("test_event", mapOf("origin" to "Schminder - Settings"))

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
            } else if (true) {
                Text("App Version: ${thisVM.serverInfo!!.svVersionApp}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text("App Db Version: ${thisVM.serverInfo!!.svAppDb}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(20.dp))
                Text("Meds Loaded: ${thisVM.medIndivInfo!!.medIndivList.count()}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(20.dp))
                Text("Server version: ${thisVM.serverInfo!!.svVersionServer ?: "Loading..."}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Server db version: ${thisVM.serverInfo!!.svVersionDb ?: "Loading..."}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(24.dp))

                if (error != null) {
                    Text(": $error"
                        , style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.error)
)
                }
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

