
package uk.co.explose.schminder.android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import uk.co.explose.schminder.android.model.mpp.MedIndivMed

@Composable
fun MedsScheduledTable(navController: NavHostController, medGroups: List<MedIndivMed>) {
    val nameWeight = 0.4f
    val timeWeight = 0.2f
    val freqWeight = 0.15f
    val durWeight = 0.15f
    val actionWeight = 0.1f


    Column(modifier = Modifier.padding(horizontal = 16.dp)) {

        Spacer(modifier = Modifier.weight(0.15f))

// Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.weight(nameWeight), contentAlignment = Alignment.CenterStart) {
                Icon(Icons.Filled.Person, contentDescription = "Name", tint = Color.Gray)
            }
            Box(Modifier.weight(timeWeight), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.AccessTime, contentDescription = "Time", tint = Color.Gray)
            }
            Box(Modifier.weight(freqWeight), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.SyncAlt, contentDescription = "Freq", tint = Color.Gray)
            }
            Box(Modifier.weight(durWeight), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.DateRange, contentDescription = "Duration", tint = Color.Gray)
            }
            Box(Modifier.weight(actionWeight), contentAlignment = Alignment.Center) {
                //Icon(Icons.Filled.Delete, contentDescription = "Actions", tint = Color.Gray)
            }
        }

        // All group rows
        medGroups.forEach { group ->
            group.schedules.forEach { med ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(med.medName, Modifier.weight(nameWeight), fontSize = 13.sp)
                    Text(
                        med.medTimeofday.toString(),
                        Modifier.weight(timeWeight),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        med.medRepeatType.toString(),
                        Modifier.weight(freqWeight),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "${med.medRepeatCount} ${med.medRepeatInterval.name.uppercase()}",
                        Modifier.weight(durWeight),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Row(
                        Modifier.weight(actionWeight),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(onClick = {
                            navController.navigate("medDetail/${med.medName}?medId=${med.medId}")
                        }, modifier = Modifier.size(20.dp)) {
                            Icon(
                                Icons.Filled.ChevronRight,
                                contentDescription = "Edit",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

            }
        }
    }

}

