
package uk.co.explose.schminder.android.network

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import uk.co.explose.schminder.android.BuildConfig
import uk.co.explose.schminder.android.core.AppGlobal
import uk.co.explose.schminder.android.model.firebase.FirebaseTokenRx
import uk.co.explose.schminder.android.model.firebase.FirebaseTokenTx
import uk.co.explose.schminder.android.model.login.LoginRequest
import uk.co.explose.schminder.android.model.login.LoginResponse
import uk.co.explose.schminder.android.model.mpp.MedIndivActionRx
import uk.co.explose.schminder.android.model.mpp.MedIndivActionTx
import uk.co.explose.schminder.android.model.mpp.MedIndivInfo
import uk.co.explose.schminder.android.model.mpp.medInfo
import uk.co.explose.schminder.android.model.mpp.med_search_tx
import uk.co.explose.schminder.android.model.server_version.c_ServerVersion
import uk.co.explose.schminder.android.model.user.UserProfile
import uk.co.explose.schminder.android.utils.OffsetDateTimeAdapter
import java.time.OffsetDateTime


interface ApiService {
    @GET("api/users/profile")
    suspend fun getUserProfile(@Header("Authorization") token: String): Response<UserProfile>

    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("api/api_ServerVersion2_0")
    suspend fun getServerVersion(): Response<c_ServerVersion>

    @POST("api/api_FirebaseToken2_0")
    suspend fun postFirebaseToken(@Body token: FirebaseTokenTx): Response<FirebaseTokenRx>

    @GET("api/api_medindivlistall2_0")
    suspend fun getMedsIndivList(): Response<MedIndivInfo>

    @GET("api/api_medfinddetail")
    suspend fun doMedsSearch(@Body med_search: med_search_tx ): Response<medInfo>

    @POST("api/api_MedIndivActionTx")
    suspend fun doMedIndivActionTx(@Body medInfo: MedIndivActionTx ): Response<MedIndivActionRx>
}

object RetrofitClient {
    private const val BASE_URL = BuildConfig.BASE_URL

    val instance: ApiService by lazy {
        val tokenInfo = AppGlobal.doAPGDataRead().mFirebaseTokenInfo
        var token = ""
        if (tokenInfo != null) {
            token = tokenInfo.fbtToken
        }

        val gson = GsonBuilder()
            .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeAdapter())
            .create()

        // 👇 Interceptor to add Authorization Header
        val authInterceptor = Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            if (token.isNotEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // 👈 Attach the client with the header
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        retrofit.create(ApiService::class.java)
    }
}

