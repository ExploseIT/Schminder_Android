package uk.co.explose.schminder.android.model.server

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import uk.co.explose.schminder.android.model.server_version.DbConstants
import uk.co.explose.schminder.android.model.server_version.ServerInfo

import uk.co.explose.schminder.android.model.server_version.ServerInfoResponse
import uk.co.explose.schminder.android.model.user.UserRepo

import uk.co.explose.schminder.android.network.RetrofitClient
import uk.co.explose.schminder.android.repo.RepositoryBase
import uk.co.explose.schminder.android.repo.Resource

object ServerRepo : RepositoryBase<ServerInfoResponse>  {
    private val _resp = MutableStateFlow<Resource<ServerInfoResponse>>(Resource.Empty())
    override val data: StateFlow<Resource<ServerInfoResponse>> = _resp

    override suspend fun loadData(context: Context): Resource<ServerInfoResponse> {

        _resp.value = Resource.Loading()

        return runCatching {
            val response = RetrofitClient.api.getServerVersion()

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null && responseBody.apiSuccess) {
                    val si = ServerInfo.fromContext(context)
                    responseBody.apiData.svVersionApp = si.svVersionApp
                    responseBody.apiData.svAppDb = si.svAppDb

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

    override fun getCachedData(): Resource<ServerInfoResponse> {
        return _resp.value
    }

    override fun setCachedData(newRes: Resource<ServerInfoResponse>): Resource<ServerInfoResponse>? {
        _resp.value = newRes
        return _resp.value
    }

    override suspend fun refreshData(context: Context): Resource<ServerInfoResponse> {
        TODO("Not yet implemented")
    }

}



