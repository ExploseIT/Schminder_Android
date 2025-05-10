

package uk.co.explose.schminder.android.helper

import uk.co.explose.schminder.android.model.mpp.Med
import uk.co.explose.schminder.android.model.mpp.MedRepeatIntervalEnum
import uk.co.explose.schminder.android.model.mpp.MedRepeatTypeEnum

import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit


fun getMedListScheduledForToday(scheduledMeds: List<Med>, day: LocalDate): List<Med> {
    return scheduledMeds
        .filter { med -> isMedScheduledOnDate(med, day) }
        .sortedBy { it.medTimeofday }
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