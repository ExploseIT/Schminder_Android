package uk.co.explose.schminder.android.model.firebase

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

import uk.co.explose.schminder.android.network.RetrofitClient
import uk.co.explose.schminder.android.repo.RepositoryBase
import uk.co.explose.schminder.android.repo.Resource
import java.io.IOException

object FirebaseRepo : RepositoryBase<FirebaseTokenRxResponse> {

    private val _resp = MutableStateFlow<Resource<FirebaseTokenRxResponse>>(Resource.Empty())
    override val data: StateFlow<Resource<FirebaseTokenRxResponse>> = _resp

        suspend fun loadData(fbtTx: FirebaseTokenTx): Resource<FirebaseTokenRxResponse> {
            _resp.value = Resource.Loading()

        return runCatching {
            Log.d("TRACE", Log.getStackTraceString(Throwable("postFirebaseToken invoked")))
            val response = RetrofitClient.api.postFirebaseToken(fbtTx)

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

    fun getData(): Resource<FirebaseTokenRxResponse> {
        return _resp.value
    }

    suspend fun getFirebaseInstallId(): String {
        return FirebaseInstallations.getInstance().id.await()
    }

    override suspend fun loadData(context: Context): Resource<FirebaseTokenRxResponse> {
        TODO("Not yet implemented")
    }

    override fun getCachedData(): Resource<FirebaseTokenRxResponse>? {
        return _resp.value
    }

    override fun setCachedData(newRes: Resource<FirebaseTokenRxResponse>): Resource<FirebaseTokenRxResponse>? {
        TODO("Not yet implemented")
    }

    override suspend fun refreshData(context: Context): Resource<FirebaseTokenRxResponse> {
        TODO("Not yet implemented")
    }

}

