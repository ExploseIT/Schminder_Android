

package uk.co.explose.schminder.android.helper

import android.content.Context
import uk.co.explose.schminder.android.core.AppDb
import uk.co.explose.schminder.android.model.mpp.Med
import uk.co.explose.schminder.android.model.mpp.MedRepeatIntervalEnum
import uk.co.explose.schminder.android.model.mpp.MedRepeatTypeEnum
import uk.co.explose.schminder.android.model.mpp.MedScheduled

import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit


suspend fun getMedListScheduledForDay(
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
            // Check if it already exists in DB
            var scheduled = dao.exists(med.medId, medDTSchedule)
            if (scheduled == null) {
                // Insert and assign new medId
                val toInsert = MedScheduled(
                    medPid = med.medPid,
                    medIdSchedule = med.medId,
                    medName = med.medName,
                    medInfo = med.medInfo,
                    medDTSchedule = medDTSchedule,
                    medDTTaken = null,
                    medDTNotifyLast = null
                )
                scheduled = dao.insertAndReturn(toInsert) // insertAndReturn returns MedScheduled with medId
            }
            result.add(scheduled)
        } else {
            // Future date — do not persist to DB, just generate in-memory MedScheduled
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



fun isMedScheduledOnDate(med: Med, day: LocalDate): Boolean {
    if (!med.medScheduled) return false

    return when (med.medRepeatType) {
        MedRepeatTypeEnum.Once -> med.medDateStart == day
        MedRepeatTypeEnum.Ongoing -> !day.isBefore(med.medDateStart)
        else -> {
            val daysBetween = ChronoUnit.DAYS.between(med.medDateStart, day).toInt()
            if (daysBetween < 0) return false

            when (med.medRepeatInterval) {
                MedRepeatIntervalEnum.Days -> daysBetween % med.medRepeatCount == 0
                MedRepeatIntervalEnum.Weeks -> (daysBetween / 7) % med.medRepeatCount == 0
                MedRepeatIntervalEnum.Months -> {
                    val monthsBetween = ChronoUnit.MONTHS.between(
                        YearMonth.from(med.medDateStart),
                        YearMonth.from(day)
                    ).toInt()
                    monthsBetween % med.medRepeatCount == 0
                }
                else -> false
            }
        }
    }
}