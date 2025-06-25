
package uk.co.explose.schminder.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
import uk.co.explose.schminder.android.core.AppRepo
import uk.co.explose.schminder.android.model.settings.AppSetting
import uk.co.explose.schminder.android.ui.components.ManageItem
import uk.co.explose.schminder.android.ui.viewmodels.SettingsScreenVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleSettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    // ✅ Instantiate ViewModel with context (use remember if you're constructing manually)
    val thisVM: SettingsScreenVM = remember { SettingsScreenVM(context) }

    val coroutineScope = rememberCoroutineScope()

    val settingsList:List<AppSetting>? = thisVM.settingsList

    val isLoading = thisVM.isLoading
    val error = thisVM.errorMessage

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
            } else if (error != null) {
                Text("Error loading data: $error", style = MaterialTheme.typography.bodyLarge)
            } else {
                Text(
                    text = "Schedule Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                LazyColumn(
                    modifier = Modifier.padding(
                        start = 0.dp,
                        top = 0.dp,
                        end = 0.dp,
                        bottom = 0.dp
                    )
                ) {
                    items(settingsList ?: emptyList()) { setting ->
                        var currentValue by remember { mutableStateOf(setting.setValue) }
                        var showDescription by remember { mutableStateOf(false) }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = setting.setName,
                                modifier = Modifier.weight(2f),
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            TextField(
                                value = currentValue,
                                onValueChange = {
                                    currentValue = it
                                    coroutineScope.launch {
                                        thisVM.updateSettingValue(setting.setId, it)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            IconButton(onClick = { showDescription = !showDescription }) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = "Description")
                            }
                        }

                        if (showDescription) {
                            Text(
                                text = setting.setDesc,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                            )
                        }

                        Divider()
                    }
                }
            }
        }
    }
}
