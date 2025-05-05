package uk.co.explose.schminder.android.model.firebase

import android.util.Log
import retrofit2.HttpException
import uk.co.explose.schminder.android.network.RetrofitClient
import java.io.IOException

class e_Firebase {

    suspend fun pFirebaseToken(fbtokenTx: FirebaseTokenTx): FirebaseTokenRx? {
        return try {
            val response = RetrofitClient.instance.postFirebaseToken(fbtokenTx)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e(
                    "FirebaseToken",
                    "Server error: ${response.code()} - ${response.errorBody()?.string()}"
                )
                null
            }
        } catch (e: IOException) {
            // Network issue: no internet, timeout, etc.
            Log.e("FirebaseToken", "Network error: ${e.localizedMessage}")
            null
        } catch (e: HttpException) {
            // Non-2xx HTTP status that wasn't caught by isSuccessful
            Log.e("FirebaseToken", "HTTP exception: ${e.localizedMessage}")
            null
        } catch (e: Exception) {
            // Other unexpected errors
            Log.e("FirebaseToken", "Unexpected error: ${e.localizedMessage}")
            null
        }
    }
}