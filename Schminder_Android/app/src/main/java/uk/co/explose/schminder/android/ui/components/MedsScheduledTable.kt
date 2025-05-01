
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.co.explose.schminder.android.model.mpp.MedIndivMed

@Composable
fun MedsScheduledTable(medGroups: List<MedIndivMed>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {

        Spacer(modifier = Modifier.weight(0.15f))

        // Global Header Row — shown only once
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Medication",
                tint = Color.Gray,
                modifier = Modifier
                    .weight(0.35f)
                    .padding(start = 4.dp)
            )
            Icon(
                imageVector = Icons.Filled.AccessTime,
                contentDescription = "Time",
                tint = Color.Gray,
                modifier = Modifier.weight(0.15f)
            )
            Icon(
                imageVector = Icons.Filled.SyncAlt,
                contentDescription = "Frequency",
                tint = Color.Gray,
                modifier = Modifier.weight(0.15f)
            )
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Duration",
                tint = Color.Gray,
                modifier = Modifier.weight(0.2f)
            )
            // Actions column, maybe leave blank
            Spacer(modifier = Modifier.weight(0.15f))
        }



        // All group rows
        medGroups.forEach { group ->
            group.schedules.forEach { med ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(med.medName, Modifier.weight(0.35f), fontSize = 14.sp)
                    Text(med.medTimeofday.toString(), Modifier.weight(0.15f), fontSize = 13.sp)
                    Text(if (med.medRecurring) "DAILY" else "ONCE", Modifier.weight(0.15f), fontSize = 13.sp)
                    Text("${med.medRecurCount} ${med.medRecurInterval.name.uppercase()}", Modifier.weight(0.2f), fontSize = 13.sp)
                    Row(Modifier.weight(0.15f), horizontalArrangement = Arrangement.End) {
                        IconButton(onClick = { /* Edit */ }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = { /* Delete */ }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", modifier = Modifier.size(18.dp))

                        }
                    }
                }
            }
        }
    }

}

