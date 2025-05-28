
package uk.co.explose.schminder.android.helper


import java.time.LocalDateTime
import uk.co.explose.schminder.android.model.mpp.Med
import androidx.compose.ui.graphics.Color
import uk.co.explose.schminder.android.mapper.MedScheduledDisplayItem
import uk.co.explose.schminder.android.model.mpp.MedScheduled
import uk.co.explose.schminder.android.model.settings.SettingsObj
import java.time.LocalDate

fun getMedStatus(
    med: MedScheduledDisplayItem,
    dtNow: LocalDateTime,
    dayRel: LocalDate,
    dtRel: LocalDateTime,
    objSettings: SettingsObj,
): medStatus {
    val medDateTime = LocalDateTime.of(dayRel, med.medTodDerived)

    return when {
        med.medDTTaken != null -> medStatus.from(MedStatusName.MedSTaken, med, true)

        // "Take now" if within ±marginMinutes
        dtNow.isAfter(medDateTime.minusMinutes(objSettings.windowMinutes)) &&
                dtNow.isBefore(medDateTime.plusMinutes(objSettings.windowMinutes)) -> medStatus.from(MedStatusName.MedSTakeNow,med )

        // "Soon" if within the `soonMinutes` before med time (but outside margin window)
        dtNow.isAfter(medDateTime.minusMinutes(objSettings.soonMinutes)) && dtNow.isBefore(medDateTime) -> medStatus.from(MedStatusName.MedSSoon, med)

        // "Missed" if well past time
        dtNow.isAfter(medDateTime.plusMinutes(objSettings.missedMinutes)) -> medStatus.from(MedStatusName.MedSMissed, med)

        // Too early to show anything
        dtNow.isBefore(medDateTime.minusMinutes(objSettings.missedMinutes)) -> medStatus.from(MedStatusName.MedSDefault, med)

        else -> medStatus.from(MedStatusName.MedSDefault, med)
    }
}


enum class MedStatusName (val status: String) {
    MedSTaken("Taken"),       // Green
    MedSTakeNow("Take now"),     // Blue
    MedSSoon("Soon"),        // Orange
    MedSMissed("Missed"),      // Red
    MedSDefault("Default")
}

enum class MedStatusColor (val colour: Color) {
    MedSTaken(Color(0xFF4CAF50)),       // Green
    MedSTakeNow(Color(0xFF2196F3)),     // Blue
    MedSSoon(Color(0xFFFFA500)),        // Orange
    MedSMissed(Color(0xFFF44336)),      // Red
    MedSDefault(Color.Gray)
}

data class medStatus (
    var medsText: String,
    var medsStatus: MedStatusName,
    var medsColour: MedStatusColor,
    var medsTaken: Boolean = false,
    var medsItem: MedScheduledDisplayItem
) {
    fun isNotBlank(): Boolean = medsText.isNotBlank()
    fun isBlank(): Boolean = medsText.isBlank()

    companion object {
        fun from(status: MedStatusName, medsItem: MedScheduledDisplayItem, taken: Boolean = false): medStatus {
            val colour = when (status) {
                MedStatusName.MedSTaken -> MedStatusColor.MedSTaken
                MedStatusName.MedSTakeNow -> MedStatusColor.MedSTakeNow
                MedStatusName.MedSSoon -> MedStatusColor.MedSSoon
                MedStatusName.MedSMissed -> MedStatusColor.MedSMissed
                MedStatusName.MedSDefault -> MedStatusColor.MedSDefault
            }

            val text = if (status == MedStatusName.MedSDefault) "" else status.status

            return medStatus(text, status, colour, taken, medsItem)
        }
    }
}






