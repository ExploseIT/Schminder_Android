
package uk.co.explose.schminder.android.network

import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import uk.co.explose.schminder.android.BuildConfig
import uk.co.explose.schminder.android.model.firebase.r_FirebaseToken
import uk.co.explose.schminder.android.model.firebase.s_FirebaseToken
import uk.co.explose.schminder.android.model.login.LoginRequest
import uk.co.explose.schminder.android.model.login.LoginResponse
import uk.co.explose.schminder.android.model.mpp.m_med_indiv
import uk.co.explose.schminder.android.model.mpp.m_med_indiv_info
import uk.co.explose.schminder.android.model.server_version.c_ServerVersion
import uk.co.explose.schminder.android.model.user.UserProfile
import uk.co.explose.schminder.android.utils.OffsetDateTimeAdapter
import java.time.OffsetDateTime


interface ApiService {
    @GET("api/users/profile")
    suspend fun getUserProfile(@Header("Authorization") token: String): Response<UserProfile>

    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("api/api_ServerVersion")
    suspend fun getServerVersion(): Response<c_ServerVersion>

    @POST("api/api_FirebaseToken")
    suspend fun postFirebaseToken(@Body token: s_FirebaseToken): Response<r_FirebaseToken>

    @GET("api/api_medlistall")
    suspend fun getMedsIndivList(): Response<m_med_indiv_info>
}

object RetrofitClient {
    private const val BASE_URL = BuildConfig.BASE_URL

    val instance: ApiService by lazy {
        val gson = GsonBuilder()
            .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeAdapter())
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        retrofit.create(ApiService::class.java)
    }
}

