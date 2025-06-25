
package uk.co.explose.schminder.android.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.co.explose.schminder.android.core.AppDb
import uk.co.explose.schminder.android.core.AppRepo
import uk.co.explose.schminder.android.mapper.MedScheduledDisplayItem
import uk.co.explose.schminder.android.model.mpp.Med
import uk.co.explose.schminder.android.model.mpp.MedScheduled
import uk.co.explose.schminder.android.model.mpp.MedsRepo
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun MedicationTakeScreen(medId: Int, navController: NavHostController) {
    val context = LocalContext.current
    val medsRepo = remember { MedsRepo }
    var medCurrent by remember { mutableStateOf<MedScheduledDisplayItem?>(null) }
    var medNext by remember { mutableStateOf<MedScheduledDisplayItem?>(null) }
    val refreshTrigger = remember { mutableStateOf(false) }
    val dateNow = LocalDate.now()

    LaunchedEffect(medId, refreshTrigger.value) {
        if (medId > 0) {
            medCurrent = medsRepo.readMedScheduledDisplayItemById(context, medId)
            medCurrent?.let {
                val medsAll = medsRepo.medListAll(context)
                medNext = medsRepo.getNextMedScheduledDisplayItemFrom(context, medsAll, medCurrent!!, dateNow)
            }
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("Take Medication", style = MaterialTheme.typography.headlineSmall)

            // ✅ If medication not taken yet
            medCurrent?.let { med ->
                if (med.medDTTaken == null) {
                    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Take Medication", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            Text(text = med.medName)
                            Text(text = "Time: ${med.medTodDerived}")
                            Button(onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    medsRepo.markMedicationAsTaken(context, med.medId)
                                    withContext(Dispatchers.Main) {
                                        refreshTrigger.value = !refreshTrigger.value // toggle to retrigger LaunchedEffect
                                    }
                                }
                            }) {
                                Text("Mark as Taken")
                            }
                        }
                    }
                } else {
                    MedicationDetailCard(
                        title = "Medication Taken",
                        label = med.medName,
                        scheduledTime = med.medTodDerived.toString(),
                        takenTime = med.medDTTaken?.toLocalTime()?.toString() ?: "—"
                    )
                }
            }

            // ✅ Show next medication if available
            medNext?.let { next ->
                MedicationDetailCard(
                    title = "Next medication",
                    label = next.medName,
                    scheduledTime = next.medTodDerived.toString(),
                    takenTime = null
                )
            }
        }
    }
}

@Composable
fun MedicationDetailCard(title: String, label: String, scheduledTime: String, takenTime: String?) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(text = label, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            /* Scheduled Time */
            Text("Time: $scheduledTime")
            if (takenTime != null) {
                Text("Taken Time: $takenTime")
            }
        }
    }
}
