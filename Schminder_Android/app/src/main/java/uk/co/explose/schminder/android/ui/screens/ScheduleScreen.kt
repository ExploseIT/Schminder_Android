
//@file:JvmName("SettingsScreenKt")

package uk.co.explose.schminder.android.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import uk.co.explose.schminder.android.core.AppGlobal
import uk.co.explose.schminder.android.model.mpp.MedIndivMed
import uk.co.explose.schminder.android.model.mpp.MedsRepo
import uk.co.explose.schminder.android.ui.components.MedItem
import uk.co.explose.schminder.android.ui.components.MedsScheduledTable

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(navController: NavHostController) {
    val context = LocalContext.current
    val activity = LocalContext.current as? Activity
    val medsRepo = MedsRepo(context)
    val coroutineScope = rememberCoroutineScope()

    var medGroups by remember { mutableStateOf<List<MedIndivMed>>(emptyList()) }

    AppGlobal.logEvent("test_event", mapOf("origin" to "Schminder - Medication"))

    LaunchedEffect(Unit) {
        medGroups = medsRepo.medIndivMedListAll()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medications") },
                /*
                actions = {
                    IconButton(onClick = {
                        navController.navigate("addMedSchedule")
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Medication")
                    }
                }
                 */
            )
        },
        content = { paddingValues ->
            if (medGroups.isEmpty()) {
                // 🟢 No meds found
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No medications added yet",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(contentPadding = paddingValues) {
                    items(medGroups) { group ->
                        // Group header (med name)
                        MedItem(
                            icon = Icons.Filled.Person,
                            label = group.medIndiv.medName,
                            onClick = {
                                navController.navigate("medDetail/${group.medIndiv.medName}")
                            }
                        )

                    }
                    item {
                        MedsScheduledTable(navController, medGroups)
                    }
                }

            }
        }
    )
}



