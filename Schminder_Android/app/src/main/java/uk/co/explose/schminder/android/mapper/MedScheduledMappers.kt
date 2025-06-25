
package uk.co.explose.schminder.android.mapper

import uk.co.explose.schminder.android.model.mpp.MedRepeatIntervalEnum
import uk.co.explose.schminder.android.model.mpp.MedRepeatTypeEnum
import uk.co.explose.schminder.android.model.mpp.MedScheduledWithMed
import uk.co.explose.schminder.android.utils.dtObject
import java.time.LocalDateTime
import java.time.LocalTime

fun MedScheduledWithMed.toDisplayItem(): MedScheduledDisplayItem {
    val derivedTod = if (med.medRepeatType == MedRepeatTypeEnum.Now)
        LocalTime.now().withSecond(0).withNano(0)
    else
        med.medTimeofday
    val derivedDT = if (med.medRepeatType == MedRepeatTypeEnum.Now)
        dtObject().dtoNow.withSecond(0).withNano(0)
    else
        medScheduled.medDTSchedule

    return MedScheduledDisplayItem(
        medId = medScheduled.medId,
        medPid =  medScheduled.medPid,
        medName = medScheduled.medName,
        medInfo = medScheduled.medInfo,
        medDTSchedule = medScheduled.medDTSchedule,
        medDTTaken = medScheduled.medDTTaken,
        medRepeatType = med.medRepeatType,
        medRepeatCount = med.medRepeatCount,
        medRepeatInterval = med.medRepeatInterval,
        medTimeOfDay = med.medTimeofday,
        medDTNotifyLast = medScheduled.medDTNotifyLast,
        medDTDerived = derivedDT,
        medTodDerived = derivedTod
    )
}

data class MedScheduledDisplayItem(
    val medId: Int,
    val medPid: Long,
    val medName: String,
    val medInfo: String,
    val medDTSchedule: LocalDateTime,
    val medDTTaken: LocalDateTime?,
    val medRepeatType: MedRepeatTypeEnum,
    val medRepeatCount: Int,
    val medRepeatInterval: MedRepeatIntervalEnum,
    val medTimeOfDay: LocalTime,
    var medDTNotifyLast: LocalDateTime?,
    val medDTDerived: LocalDateTime,
    val medTodDerived: LocalTime // derived field for UI
)
