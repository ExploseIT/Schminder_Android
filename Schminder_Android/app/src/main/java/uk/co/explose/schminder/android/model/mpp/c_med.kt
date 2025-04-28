package uk.co.explose.schminder.android.model.mpp

import androidx.room.Entity
import androidx.room.PrimaryKey


data class m_med_indiv_info (
    var med_indiv_name: String,
    var med_indiv_list: List<c_med_indiv>
)

data class c_med_indiv (
    @PrimaryKey
//    val med_id: Long,
//    val med_pid: Long,
    val med_name: String
)
@Entity(tableName = "meds_tbl")
data class c_med (
    val med_id: Long = 0,
    val med_pid: Long = 0,
    val med_scheduled: Boolean = false,
    val med_info: String = "",
    @PrimaryKey
    val med_name: String
)
