package uk.co.explose.schminder.android.model.firebase

import uk.co.explose.schminder.android.network.RetrofitClient

class e_Firebase {

    suspend fun pFirebaseToken(s_fbtoken: s_FirebaseToken): r_FirebaseToken? {
        return try {
            val response = RetrofitClient.instance.postFirebaseToken(s_fbtoken)
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