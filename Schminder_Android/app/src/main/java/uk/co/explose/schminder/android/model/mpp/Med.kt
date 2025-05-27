

package uk.co.explose.schminder.android.model.mpp

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import uk.co.explose.schminder.android.core.HasIntId
import uk.co.explose.schminder.android.mapper.MedScheduledDisplayItem
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Date

data class MedIndivDto(
    val medName: String
)


data class MedIndivInfo (
    var medIndivName: String,
    var medIndivList: List<MedIndivDto>
)

@Entity(tableName = "MedsIndivTbl")
data class MedIndiv(
    @PrimaryKey val medName: String,
    val medDTEntered: LocalDateTime
) {
    companion object {
        fun fromDto(dto: MedIndivDto): MedIndiv {
            return MedIndiv(
                medName = dto.medName,
                medDTEntered = LocalDateTime.now()
            )
        }
    }
}

data class MedScheduledWithMed(
    @Embedded val medScheduled: MedScheduled,

    @Relation(
        parentColumn = "medIdSchedule",
        entityColumn = "medId"
    )
    val med: Med
)

@Entity(
    tableName = "MedsScheduledTbl",
    foreignKeys = [
        ForeignKey(
            entity = Med::class,
            parentColumns = ["medId"],
            childColumns = ["medIdSchedule"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class MedScheduled(
    @PrimaryKey (autoGenerate = true)
    var medId: Int = 0,
    var medPid: Long = 0,
    var medIdSchedule: Int = 0,
    var medName: String = "",
    var medInfo: String = "",
    var medDTSchedule: LocalDateTime = LocalDateTime.now(),
    var medDTTaken: LocalDateTime? = null,
    var medDTNotifyLast: LocalDateTime? = null
) : HasIntId {
    override val id: Int
        get() = medId

    override fun copyWithId(newId: Int): MedScheduled = this.copy(medId = newId)

    fun toMedScheduledDisplayItem(med: Med): MedScheduledDisplayItem {
        val derivedTod = if (med.medRepeatType == MedRepeatTypeEnum.Now)
            LocalTime.now().withSecond(0).withNano(0)
        else
            med.medTimeofday

        val derivedDT = if (med.medRepeatType == MedRepeatTypeEnum.Now)
            LocalDateTime.now().withSecond(0).withNano(0)
        else
            this.medDTSchedule

        return MedScheduledDisplayItem(
            medId = this.medId,
            medPid = this.medPid,
            medName = this.medName,
            medInfo = this.medInfo,
            medDTSchedule = this.medDTSchedule,
            medDTTaken = this.medDTTaken,
            medRepeatType = med.medRepeatType,
            medRepeatCount = med.medRepeatCount,
            medRepeatInterval = med.medRepeatInterval,
            medTimeOfDay = med.medTimeofday,
            medDTNotifyLast = this.medDTNotifyLast,
            medDTDerived = derivedDT,
            medTodDerived = derivedTod
        )
    }

}



@Entity(
    tableName = "MedsTbl",
    foreignKeys = [
        ForeignKey(
            entity = MedIndiv::class,
            parentColumns = ["medName"],
            childColumns = ["medName"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Med(
    @PrimaryKey (autoGenerate = true)
    val medId: Int = 0,
    val medPid: Long = 0,
    val medScheduled: Boolean = false,
    val medInfo: String = "",
    val medTimeofday: LocalTime = LocalTime.of(0, 0),
    val medRepeatType: MedRepeatTypeEnum = MedRepeatTypeEnum.Ongoing,
    val medRepeatCount: Int = 0,
    val medRepeatInterval: MedRepeatIntervalEnum = MedRepeatIntervalEnum.Days,
    val medDateStart: LocalDate,
    val medName: String,
    val medDTTaken: LocalDateTime? = null
) {
    fun getTod() : LocalTime {
        val todCurrent = if (this.medRepeatType == MedRepeatTypeEnum.Now)
            LocalTime.now().withSecond(0).withNano(0)
        else
            this.medTimeofday
        return todCurrent
    }

    companion object {
        fun createScheduledMed(
            id: Int,
            name: String,
            time: LocalTime,
            repeatType: String,
            repeatCount: Int,
            repeatInterval: String,
            startDate: LocalDate,
        ): Med {
            return Med(
                medId = id,
                medName = name,
                medTimeofday = time,
                medRepeatType = MedRepeatTypeEnum.valueOf(
                    repeatType.replaceFirstChar { it.uppercase() }
                ),
                medRepeatCount = repeatCount,
                medRepeatInterval = MedRepeatIntervalEnum.valueOf(
                    repeatInterval.replaceFirstChar { it.uppercase() }
                ),
                medDateStart = startDate,
                medScheduled = true
            )
        }
    }
}

data class MedIndivMed(
    @Embedded val medIndiv: MedIndiv,

    @Relation(
        parentColumn = "medName",
        entityColumn = "medName"
    )
    val schedules: List<Med>
)


enum class MedRepeatIntervalEnum(val label: String, val sortOrder: Int) {
    Days("mriDays", 1),
    Weeks("mriWeeks", 2),
    Months("mriMonths", 3),
    Day("mriDay", 101),
}

enum class MedRepeatTypeEnum(val label: String, val sortOrder: Int) {
    Once("mrtOnce", 1),
    Count("mrtRepeat", 2),
    Ongoing("mrtOngoing", 3),
    Now("mrtNow", 101)
}
