

package uk.co.explose.schminder.android.model.mpp

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.LocalDate
import java.time.LocalTime

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
    val medDateEntered: LocalDate
) {
    companion object {
        fun fromDto(dto: MedIndivDto): MedIndiv {
            return MedIndiv(
                medName = dto.medName,
                medDateEntered = LocalDate.now()
            )
        }
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
    val medName: String
) {
    companion object {
        fun createScheduledMed(
            id: Int,
            name: String,
            time: LocalTime,
            repeatType: String,
            repeatCount: Int,
            repeatInterval: String
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

enum class MedRepeatIntervalEnum(val label: String) {
    Days("mriDays"),
    Weeks("mriWeeks"),
    Months("mriMonths")
}

enum class MedRepeatTypeEnum(val label: String) {
    Once("mrtOnce"),
    Count("mrtRepeat"),
    Ongoing("mrtOngoing")
}
