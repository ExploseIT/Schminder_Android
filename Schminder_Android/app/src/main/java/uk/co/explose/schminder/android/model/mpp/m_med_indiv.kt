package uk.co.explose.schminder.android.model.mpp

data class m_med_indiv_info (
    var med_indiv_name: String,
    var med_indiv_list: List<m_med_indiv>
)

data class m_med_indiv (
    val med_id: Long,
    val med_pid: Long,
    val med_name: String
)
