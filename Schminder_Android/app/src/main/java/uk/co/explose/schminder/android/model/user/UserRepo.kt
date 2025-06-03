

package uk.co.explose.schminder.android.model.user

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import uk.co.explose.schminder.android.core.AppGlobal
import uk.co.explose.schminder.android.model.profile.UserProfileRequest
import uk.co.explose.schminder.android.network.ApiService
import uk.co.explose.schminder.android.network.RetrofitClient


class UserRepo (private val context: Context) {
    suspend fun createProfile(username: String): Result<String> {
        val firebaseUser = AppGlobal.doAPGDataRead().mFirebaseUser
            ?: return Result.failure(Exception("User not authenticated"))
        val firebaseUsername = firebaseUser.uid

        val request =
            UserProfileRequest(profUserFirebase = firebaseUsername, profUsername = username)

        return try {
            val response = RetrofitClient.api.createProfile(request)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null && responseBody.profSuccess) {
                    Result.success(responseBody.profMessage)
                } else {
                    Result.failure(Exception(responseBody?.profMessage ?: "Unknown server error"))
                }
            } else {
                // Server returned HTTP error like 400, 500 etc.
                val errorMessage = response.errorBody()?.string() ?: "Server Error"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
