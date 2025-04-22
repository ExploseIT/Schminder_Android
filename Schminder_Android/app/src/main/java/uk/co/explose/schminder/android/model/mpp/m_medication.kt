package uk.co.explose.schminder.android.model.mpp

data class m_medication (
    val med_name: String,
    val med_route: String,
    val med_dosage: String,
    val med_frequency: String,
    val med_startDate: String,
    val med_stopAfter: String? = null
)

