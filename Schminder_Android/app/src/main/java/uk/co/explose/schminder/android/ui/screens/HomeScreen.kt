

package uk.co.explose.schminder.android.ui.screens

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import uk.co.explose.schminder.android.core.AppGlobal
import uk.co.explose.schminder.android.helper.MedCard
import uk.co.explose.schminder.android.helper.MedCardParms
import uk.co.explose.schminder.android.helper.getMedListScheduledForToday
import uk.co.explose.schminder.android.model.mpp.Med
import uk.co.explose.schminder.android.model.mpp.MedsRepo
import uk.co.explose.schminder.android.model.mpp.MedIndiv
import uk.co.explose.schminder.android.model.mpp.MedRepeatIntervalEnum
import uk.co.explose.schminder.android.model.mpp.MedRepeatTypeEnum
import uk.co.explose.schminder.android.ui.components.FabItem
import uk.co.explose.schminder.android.ui.viewmodels.HomeScreenVM
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.util.Locale


@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val thisVM: HomeScreenVM = remember { HomeScreenVM(context) }

    val now = LocalDateTime.now()
    val day = LocalDate.now()
    // Get time of day
    val tod: LocalTime = now.toLocalTime()
    val dtNow = LocalDateTime.of(day, tod)

    val parsedMeds = thisVM.parsedMeds
    val scheduledMeds = thisVM.scheduledMeds
    val isLoading = thisVM.isLoading
    val errorMessage = thisVM.errorMessage

    val medsForToday = getMedListScheduledForToday(scheduledMeds, day)

    val today = remember { LocalDate.now() }
    var selectedDay by remember { mutableStateOf(today) }
    var selectedDTNow by remember { mutableStateOf(dtNow)}

    val medsForSelectedDay by remember(scheduledMeds, selectedDay) {
        derivedStateOf {
            getMedListScheduledForToday(scheduledMeds, selectedDay)
        }
    }
    val SettingsObj = thisVM.SettingsObj

    val startOfWeek = today.with(DayOfWeek.MONDAY)
    val daysOfWeek = DayOfWeek.values().toList()
    var fabExpanded by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    AppGlobal.logEvent("test_event", mapOf("origin" to "Schminder - Home"))

    LaunchedEffect(Unit) {
        thisVM.loadVM()
    }

    Scaffold(
        floatingActionButton = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                if (fabExpanded) {
                    if (isLandscape) {
                        Row(
                            modifier = Modifier
                                .padding(bottom = 100.dp, end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            FabItem(icon = Icons.Default.CameraAlt, label = "Scan") {
                                fabExpanded = false
                                navController.navigate("prescription_scan")
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            FabItem(icon = Icons.Default.Add, label = "Add Medication") {
                                fabExpanded = false
                                navController.navigate("add_med")
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            FabItem(icon = Icons.Default.Edit, label = "TBC") {
                                fabExpanded = false
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .padding(bottom = 100.dp, end = 16.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            FabItem(icon = Icons.Default.CameraAlt, label = "Scan") {
                                fabExpanded = false
                                navController.navigate("prescription_scan")
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            FabItem(icon = Icons.Default.Add, label = "Add Medication") {
                                fabExpanded = false
                                navController.navigate("add_med")
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            FabItem(icon = Icons.Default.Edit, label = "TBC") {
                                fabExpanded = false
                            }
                        }
                    }
                }

                FloatingActionButton(onClick = { fabExpanded = !fabExpanded }) {
                    Text(if (fabExpanded) "×" else "+")
                }
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            // Row 1: Day names
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    daysOfWeek.forEach { day ->
                        Text(
                            text = day.getDisplayName(
                                java.time.format.TextStyle.SHORT,
                                Locale.getDefault()
                            ),
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Row 2: Scrollable Dates
            item {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .fillMaxWidth()
                ) {
                    for (i in 0 until 7) {
                        val date = startOfWeek.plusDays(i.toLong())
                        val isSelected = date == selectedDay
                        val isToday = date == today

                        Column(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clickable {
                                    selectedDay = date
                                    selectedDTNow = LocalDateTime.of(date, tod)
                               },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    else -> Color.Transparent
                                },
                                modifier = Modifier.size(36.dp),
                                border = if (isToday && !isSelected)
                                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                else null
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = date.dayOfMonth.toString(),
                                        color = if (isSelected) Color.White else Color.Black,
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Row 3: Selected Date Header
            item {
                Text(
                    text = if (selectedDay == today)
                        "Today, ${selectedDay.dayOfMonth} ${
                            selectedDay.month.name.take(3).capitalize(Locale.ROOT)
                        }"
                    else
                        selectedDay.format(java.time.format.DateTimeFormatter.ofPattern("EEE, d MMM")),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Row 4+: Loading, error, or meds
            when {
                isLoading -> {
                    item {
                        CircularProgressIndicator()
                    }
                }

                errorMessage != null -> {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(errorMessage, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { thisVM.loadVM() }) {
                                Text("Try Again")
                            }
                        }
                    }
                }

                medsForSelectedDay.isEmpty() -> {
                    item {
                        Text("No medications scheduled for this day.")
                    }
                }

                else -> {
                    items(medsForSelectedDay, key = { it.medId }) { med ->
                        MedCard(MedCardParms(med, LocalDateTime.now(), selectedDay, selectedDTNow, SettingsObj))
                    }
                }
            }
        }
    }
}



