package uk.co.explose.schminder.android.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import uk.co.explose.schminder.android.core.AppGlobal
import uk.co.explose.schminder.android.model.mpp.C_med
import uk.co.explose.schminder.android.model.mpp.E_meds
import uk.co.explose.schminder.android.ui.components.ManageItem
import uk.co.explose.schminder.android.ui.components.MedItem

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val activity = LocalContext.current as? Activity
    val _E_meds = E_meds(context)
    val coroutineScope = rememberCoroutineScope()

    var meds by remember { mutableStateOf<List<C_med>>(emptyList()) }

    AppGlobal.logEvent("test_event", mapOf("origin" to "Schminder - Medication"))

    LaunchedEffect(Unit) {
        meds = _E_meds.getAllMeds()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medications") },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("addMedSchedule")
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Medication")
                    }
                }
            )
        },
        content = { paddingValues ->
            if (meds.isEmpty()) {
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
                LazyColumn(
                    contentPadding = paddingValues
                ) {
                    items(items = meds) { med ->
                        MedItem(
                            icon = Icons.Filled.Person,
                            label = med.medName,
                            onClick = {
                                navController.navigate("medDetail/${med.medName}")
                            }
                        )

                    }
                }
            }
        }
    )
}



