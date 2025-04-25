
package uk.co.explose.schminder.android.model.mpp

import uk.co.explose.schminder.android.network.RetrofitClient

class e_meds {

    suspend fun doMedIndivLoad(): m_med_indiv_info? {
        return try {
            val response = RetrofitClient.instance.getMedsIndivList()
            if (response.isSuccessful) {
                response.body()
            } else {
                null // handle error (optional: log or throw)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}


