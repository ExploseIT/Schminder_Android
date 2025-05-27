
package uk.co.explose.schminder.android.model.mpp

import android.content.Context
import uk.co.explose.schminder.android.core.AppDb
import uk.co.explose.schminder.android.mapper.MedScheduledDisplayItem
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class MedScheduleService(private val context: Context) {
    /*
    suspend fun getScheduledForDay(meds: List<Med>, day: LocalDate): List<MedScheduledDisplayItem> {
        val dao = AppDb.getInstance(context).medsScheduledDao()
        val today = LocalDate.now()
        val result = mutableListOf<MedScheduledDisplayItem>()

        for (med in meds.sortedBy { it.getTod() }) {
            val dt = day.atTime(med.medTimeofday)
            val derivedTod = if (med.medRepeatType == MedRepeatTypeEnum.Now)
                LocalTime.now().withSecond(0).withNano(0)
            else
                med.medTimeofday

            val derivedDT = if (med.medRepeatType == MedRepeatTypeEnum.Now)
                LocalDateTime.now().withSecond(0).withNano(0)
            else
                dt

            if ((day <= today && med.medRepeatType != MedRepeatTypeEnum.Now) || day == today && med.medRepeatType == MedRepeatTypeEnum.Now) {
                var existing = dao.exists(med.medId, dt)
                if (existing == null) {
                    val inserted = MedScheduled(
                        medPid = med.medPid,
                        medIdSchedule = med.medId,
                        medName = med.medName,
                        medInfo = med.medInfo,
                        medDTSchedule = dt,
                        medDTTaken = null,
                        medDTNotifyLast = null
                    )
                    existing = dao.insertAndReturn(inserted)
                }

                result.add(
                    MedScheduledDisplayItem(
                        medId = existing.medId,
                        medPid = med.medPid,
                        medName = existing.medName,
                        medInfo = existing.medInfo,
                        medDTSchedule = existing.medDTSchedule,
                        medDTTaken = existing.medDTTaken,
                        medRepeatType = med.medRepeatType,
                        medRepeatCount = med.medRepeatCount,
                        medRepeatInterval = med.medRepeatInterval,
                        medTimeOfDay = med.medTimeofday,
                        medDTNotifyLast = existing.medDTNotifyLast,
                        medDTDerived = derivedDT,
                        medTodDerived = derivedTod
                    )
                )
            } else {
                result.add(
                    MedScheduledDisplayItem(
                        medId = 0,
                        medPid = med.medPid,
                        medName = med.medName,
                        medInfo = med.medInfo,
                        medDTSchedule = dt,
                        medDTTaken = null,
                        medRepeatType = med.medRepeatType,
                        medRepeatCount = med.medRepeatCount,
                        medRepeatInterval = med.medRepeatInterval,
                        medTimeOfDay = med.medTimeofday,
                        medDTNotifyLast = null,
                        medDTDerived = derivedDT,
                        medTodDerived = derivedTod
                    )
                )
            }
        }

        return result
    }

    suspend fun getNextMedScheduledDisplayItemFrom(
        allMeds: List<Med>,
        currentMed: Med,
        day: LocalDate
    ): MedScheduledDisplayItem? {
        val dao = AppDb.getInstance(context).medsScheduledDao()
        val today = LocalDate.now()

        // 1. Sort all meds by time of day
        val sortedMeds = allMeds.sortedBy { it.medTimeofday }

        // 2. Try to find the next one later *today*
        val nextToday = sortedMeds
            .firstOrNull { it.medTimeofday > currentMed.medTimeofday }

        // 3. If none found, grab the earliest one *tomorrow*
        val nextMed = nextToday ?: sortedMeds.firstOrNull()
        val targetDay = if (nextToday != null) day else day.plusDays(1)

        if (nextMed == null) return null // no meds at all

        val dt = targetDay.atTime(nextMed.medTimeofday)
        val derivedTod = if (nextMed.medRepeatType == MedRepeatTypeEnum.Now)
            LocalTime.now().withSecond(0).withNano(0)
        else
            nextMed.medTimeofday

        val derivedDT = if (nextMed.medRepeatType == MedRepeatTypeEnum.Now)
            LocalDateTime.now().withSecond(0).withNano(0)
        else
            dt

        // 4. Insert MedScheduled if needed
        var existing = dao.exists(nextMed.medId, dt)
        if (existing == null) {
            val inserted = MedScheduled(
                medPid = nextMed.medPid,
                medIdSchedule = nextMed.medId,
                medName = nextMed.medName,
                medInfo = nextMed.medInfo,
                medDTSchedule = dt,
                medDTTaken = null,
                medDTNotifyLast = null
            )
            existing = dao.insertAndReturn(inserted)
        }

        // 5. Return Display Item
        return MedScheduledDisplayItem(
            medId = existing.medId,
            medPid = nextMed.medPid,
            medName = existing.medName,
            medInfo = existing.medInfo,
            medDTSchedule = existing.medDTSchedule,
            medDTTaken = existing.medDTTaken,
            medRepeatType = nextMed.medRepeatType,
            medRepeatCount = nextMed.medRepeatCount,
            medRepeatInterval = nextMed.medRepeatInterval,
            medTimeOfDay = nextMed.medTimeofday,
            medDTNotifyLast = existing.medDTNotifyLast,
            medDTDerived = derivedDT,
            medTodDerived = derivedTod
        )
    }


     */
}
