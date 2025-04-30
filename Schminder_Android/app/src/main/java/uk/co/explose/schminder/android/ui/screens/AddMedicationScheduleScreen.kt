

package uk.co.explose.schminder.android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.time.LocalTime

import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationScheduleScreen(
    navController: NavHostController,
    medName: String = "Metformin",
    onAdd: (String, LocalTime, String, Int, String) -> Unit
) {
    val timePickerState = rememberTimePickerState(initialHour = 12, initialMinute = 0, is24Hour = true)
    var showTimePicker by remember { mutableStateOf(false) }

    var frequency by remember { mutableStateOf("Once") }
    var durationCount by remember { mutableStateOf("1") }
    var durationUnit by remember { mutableStateOf("Days") }

    val frequencyOptions = listOf("Once", "Daily")
    val durationUnits = listOf("Days", "Weeks", "Months")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = medName) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { showTimePicker = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Time: %02d:%02d".format(timePickerState.hour, timePickerState.minute))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Frequency")
            Row {
                frequencyOptions.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        RadioButton(
                            selected = frequency == option,
                            onClick = { frequency = option }
                        )
                        Text(option)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("For", modifier = Modifier.padding(end = 8.dp))
                OutlinedTextField(
                    value = durationCount,
                    onValueChange = { durationCount = it },
                    label = { Text("Count") },
                    modifier = Modifier.width(100.dp)
                )
            }

// ROW: Duration units ("Days", "Weeks", "Months")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                durationUnits.forEach { unit ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = durationUnit == unit,
                            onClick = { durationUnit = unit }
                        )
                        Text(unit)
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val time = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    onAdd(medName, time, frequency, durationCount.toIntOrNull() ?: 1, durationUnit)
                    navController.popBackStack()
                }
            ) {
                Text("Add")
            }
        }

        if (showTimePicker) {
            TimePickerDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Select time") },
                content = {
                    TimePicker(state = timePickerState)
                }
            )
        }
    }
}


