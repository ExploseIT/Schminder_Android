

package uk.co.explose.schminder.android.model.user

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
<<<<<<< HEAD
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
=======
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import uk.co.explose.schminder.android.model.firebase.FirebaseTokenRx
import uk.co.explose.schminder.android.model.profile.UserProfile
import uk.co.explose.schminder.android.model.profile.UserProfileRequest
import uk.co.explose.schminder.android.model.profile.UserProfileResponse
import uk.co.explose.schminder.android.network.ApiService
import uk.co.explose.schminder.android.network.RetrofitClient
import uk.co.explose.schminder.android.repo.RepositoryBase
import uk.co.explose.schminder.android.repo.Resource
import java.io.IOException


object UserRepo : RepositoryBase<UserProfileResponse> {
    private val _resp = MutableStateFlow<Resource<UserProfileResponse>>(Resource.Empty())
    override val data: StateFlow<Resource<UserProfileResponse>> = _resp

    suspend fun loadDataByFirebase(up: UserProfile): Resource<UserProfileResponse> {
        _resp.value = Resource.Loading()

        return runCatching {
            val response = RetrofitClient.api.userProfileReadByFirebase(up)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null && responseBody.apiSuccess) {
                    Resource.Success(responseBody)
                } else {
                    Resource.Error(responseBody?.apiMessage ?: "Unknown server error")
                }
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Server Error"
                Resource.Error(errorMessage)
            }
        }.getOrElse { e ->
            val exception = if (e is Exception) e else Exception(e.message ?: "Unknown error")
            Resource.Error("Network error: Please check your internet connection.", exception)
        }.also {
            _resp.value = it
        }
    }


    suspend fun userProfileCreate(up: UserProfile): Resource<UserProfileResponse> {
        return runCatching {
            val response = RetrofitClient.api.userProfileCreate(up)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null && responseBody.apiSuccess) {
                    Resource.Success(responseBody)
                } else {
                    Resource.Error(responseBody?.apiMessage ?: "Unknown server error")
                }
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Server Error"
                Resource.Error(errorMessage)
            }
        }.getOrElse { e ->
            val exception = if (e is Exception) e else Exception(e.message ?: "Unknown error")
            Resource.Error("Network error: Please check your internet connection.", exception)
        }.also {
            _resp.value = it
        }
    }

    fun updateUsername(newUsername: String) : Resource<UserProfileResponse> {
        val current = _resp.value
        if (current is Resource.Success) {
            val existingProfile = current.data.apiData
            if (existingProfile != null) {
                val updatedProfile = existingProfile.copy(user_username = newUsername)
                val updatedApiResponse = current.data.copy(apiData = updatedProfile)
                _resp.value = Resource.Success(updatedApiResponse)
            }
        }
        return _resp.value
    }

    override fun setCachedData(newRes: Resource<UserProfileResponse>): Resource<UserProfileResponse>? {
        _resp.value = newRes
        return _resp.value
    }

    override suspend fun loadData(context: Context): Resource<UserProfileResponse> {
        TODO("Not yet implemented")
    }

    override fun getCachedData(): Resource<UserProfileResponse>? {
        return _resp.value
    }

    override suspend fun refreshData(context: Context): Resource<UserProfileResponse> {
        TODO("Not yet implemented")
    }

>>>>>>> dfd0eec (v1.0.1.16)
}
