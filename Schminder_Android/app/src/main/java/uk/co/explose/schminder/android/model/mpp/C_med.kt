package uk.co.explose.schminder.android.model.mpp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime


data class m_medIndivInfo (
    var medIndivName: String,
    var medIndivList: List<C_med_indiv>
)

data class C_med_indiv (
    @PrimaryKey
//    val med_id: Long,
//    val med_pid: Long,
    val medName: String
)

enum class MedRecurIntervalEnum(val label: String) {
    Days("mriDays"),
    Weeks("mriWeeks"),
    Months("mriMonths")
}

@Entity(tableName = "meds_tbl")
data class C_med (
    val medId: Long = 0,
    val medPid: Long = 0,
    val medScheduled: Boolean = false,
    val medInfo: String = "",
    val medTimeOfDay: LocalTime = LocalTime.of(0, 0),
    val medRecurring: Boolean = false,
    val medRecurCount: Int = 0,
    val medRecurInterval: MedRecurIntervalEnum = MedRecurIntervalEnum.Days,
    @PrimaryKey
    val medName: String
)
