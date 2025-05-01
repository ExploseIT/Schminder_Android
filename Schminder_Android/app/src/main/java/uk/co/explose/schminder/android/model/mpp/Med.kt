

package uk.co.explose.schminder.android.model.mpp

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.LocalTime



data class MedIndivInfo (
    var medIndivName: String,
    var medIndivList: List<MedIndiv>
)

@Entity(tableName = "MedsIndivTbl")
data class MedIndiv (
    @PrimaryKey
    val medName: String
)


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
    val medRecurring: Boolean = false,
    val medRecurCount: Int = 0,
    val medRecurInterval: MedRecurIntervalEnum = MedRecurIntervalEnum.Days,
    val medName: String
) {
    companion object {
        fun createScheduledMed(
            name: String,
            time: LocalTime,
            frequency: String,
            count: Int,
            unit: String
        ): Med {
            return Med(
                medName = name,
                medTimeofday = time,
                medRecurring = frequency.lowercase() != "once",
                medRecurCount = count,
                medRecurInterval = MedRecurIntervalEnum.valueOf(
                    unit.replaceFirstChar { it.uppercase() }
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

enum class MedRecurIntervalEnum(val label: String) {
    Days("mriDays"),
    Weeks("mriWeeks"),
    Months("mriMonths")
}
