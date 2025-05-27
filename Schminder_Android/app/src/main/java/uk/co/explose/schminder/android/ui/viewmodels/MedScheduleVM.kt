
package uk.co.explose.schminder.android.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uk.co.explose.schminder.android.core.AppDb
import uk.co.explose.schminder.android.model.mpp.Med
import uk.co.explose.schminder.android.model.mpp.MedScheduled
import java.time.LocalDate

class MedScheduleVM () : ViewModel() {

    private val _scheduledMeds = MutableStateFlow<List<MedScheduled>>(emptyList())
    val scheduledMeds: StateFlow<List<MedScheduled>> = _scheduledMeds
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadScheduleForDay(context: Context, day: LocalDate, meds: List<Med>) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = getMedListScheduledForDay(context, meds, day)
            _scheduledMeds.value = result
            _isLoading.value = false
        }
    }

    // -- include the logic you defined earlier as a private helper
    private suspend fun getMedListScheduledForDay(
        context: Context,
        medsForDay: List<Med>,
        day: LocalDate
    ): List<MedScheduled> {
        val dao = AppDb.getInstance(context).medsScheduledDao()
        val today = LocalDate.now()
        val result = mutableListOf<MedScheduled>()

        for (med in medsForDay.sortedBy { it.medTimeofday }) {
            val medDTSchedule = day.atTime(med.medTimeofday)

            if (day <= today) {
                var scheduled = dao.exists(med.medId, medDTSchedule)
                if (scheduled == null) {
                    val toInsert = MedScheduled(
                        medPid = med.medPid,
                        medIdSchedule = med.medId,
                        medName = med.medName,
                        medInfo = med.medInfo,
                        medDTSchedule = medDTSchedule,
                        medDTTaken = null,
                        medDTNotifyLast = null
                    )
                    scheduled = dao.insertAndReturn(toInsert)
                }
                result.add(scheduled)
            } else {
                result.add(
                    MedScheduled(
                        medId = 0,
                        medPid = med.medPid,
                        medIdSchedule = med.medId,
                        medName = med.medName,
                        medInfo = med.medInfo,
                        medDTSchedule = medDTSchedule,
                        medDTTaken = null,
                        medDTNotifyLast = null
                    )
                )
            }
        }

        return result
    }
}
