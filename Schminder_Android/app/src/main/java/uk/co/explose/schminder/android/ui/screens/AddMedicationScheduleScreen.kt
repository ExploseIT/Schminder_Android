

package uk.co.explose.schminder.android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.time.LocalTime

import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import uk.co.explose.schminder.android.BuildConfig
import uk.co.explose.schminder.android.model.mpp.Med
import uk.co.explose.schminder.android.model.mpp.MedRepeatIntervalEnum
import uk.co.explose.schminder.android.model.mpp.MedRepeatTypeEnum
import uk.co.explose.schminder.android.model.mpp.MedsRepo
import uk.co.explose.schminder.android.utils.setTime
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationScheduleScreen(
    navController: NavHostController,
    medName: String = "Metformin",
    medId: Int = 0,
    onSave: (Med) -> Unit,
    onDelete: (Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(initialHour = 12, initialMinute = 0, is24Hour = true)
    var showTimePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var repeatType by remember { mutableStateOf(MedRepeatTypeEnum.Ongoing) }
    var durationCount by remember { mutableStateOf("1") }
    var durationUnit by remember { mutableStateOf("Days") }

    val repeatTypeOptions = MedRepeatTypeEnum.entries.sortedBy { it.sortOrder }

    val modeDebug: Boolean = BuildConfig.DEBUG

    val durationUnits = MedRepeatIntervalEnum.entries.sortedBy { it.sortOrder }
    //listOf(MedRepeatIntervalEnum.Days, MedRepeatIntervalEnum.Weeks, MedRepeatIntervalEnum.Months)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val medsRepo = remember { MedsRepo(context) }

    LaunchedEffect(medId) {
        if (medId > 0) {
            val med = medsRepo.medReadById(medId)
            med?.let {
                repeatType = it.medRepeatType
                durationCount = it.medRepeatCount.toString()
                durationUnit = it.medRepeatInterval.name
                timePickerState.setTime(it.medTimeofday.hour, it.medTimeofday.minute)
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (medId > 0) "Edit $medName" else medName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Cancel")
                    }
                },
                actions = {
                    if (medId > 0) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                    IconButton(onClick = {
                        val time = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        val med = Med.createScheduledMed(medId, medName, time,
                            repeatType.toString(), durationCount.toIntOrNull() ?: 1, durationUnit, LocalDate.now())
                        onSave(med)
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
            .fillMaxWidth(),   // <--- Make sure Column is full width
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { showTimePicker = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Time: %02d:%02d".format(timePickerState.hour, timePickerState.minute))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Repeat")
            Row {
                repeatTypeOptions.forEach { option ->
                    if (modeDebug || option.sortOrder < 100)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            RadioButton(
                                selected = repeatType == option,
                                onClick = { repeatType = option }
                            )
                            Text(
                                text = option.name, // use label instead of toString() for clean display
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                }
            }

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(0.dp)
                    .fillMaxWidth(),   // <--- Make sure Column is full width
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (repeatType == MedRepeatTypeEnum.Count) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("For", modifier = Modifier.padding(end = 8.dp))
                        OutlinedTextField(
                            value = durationCount,
                            onValueChange = { durationCount = it },
                            label = { Text("Count") },
                            modifier = Modifier.width(100.dp)
                        )
                    }
                }

                if (repeatType == MedRepeatTypeEnum.Count) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        durationUnits.forEach { unit ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (unit.sortOrder < 100)
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        RadioButton(
                                            selected = durationUnit == unit.toString(),
                                            onClick = { durationUnit = unit.toString() })

                                        Text(unit.toString())
                                    }
                            }
                        }
                    }
                }
            }
        }
        if (showTimePicker) {
            TimePickerDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = { showTimePicker = false }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
                },
                title = { Text("Select time") },
                content = { TimePicker(state = timePickerState) }
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Confirm Deletion") },
                text = { Text("Are you sure you want to delete this medication schedule?") },
                confirmButton = {
                    TextButton(onClick = {
                        onDelete(medId)
                        showDeleteDialog = false
                        navController.popBackStack()
                    }) {
                        Text("Proceed")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

    }
}


