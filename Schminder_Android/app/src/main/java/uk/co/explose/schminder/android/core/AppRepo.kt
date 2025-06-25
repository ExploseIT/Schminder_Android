

package uk.co.explose.schminder.android.core

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import uk.co.explose.schminder.android.model.firebase.FirebaseData
import uk.co.explose.schminder.android.model.firebase.FirebaseRepo

import uk.co.explose.schminder.android.model.firebase.FirebaseTokenRx
import uk.co.explose.schminder.android.model.firebase.FirebaseTokenTx
import uk.co.explose.schminder.android.model.mpp.MedIndivInfoResponse
import uk.co.explose.schminder.android.model.mpp.MedsRepo
import uk.co.explose.schminder.android.model.profile.UserProfile
import uk.co.explose.schminder.android.model.profile.UserProfileResponse
import uk.co.explose.schminder.android.model.server.ServerRepo
import uk.co.explose.schminder.android.model.server_version.ServerInfoResponse
import uk.co.explose.schminder.android.model.settings.SettingsRepo
import uk.co.explose.schminder.android.model.user.UserRepo

import uk.co.explose.schminder.android.preferences.PrefRepo
import uk.co.explose.schminder.android.repo.RepositoryBase
import uk.co.explose.schminder.android.repo.Resource
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


object AppRepo : RepositoryBase<FirebaseData> {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var mFirebaseAuth: FirebaseAuth

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _resp = MutableStateFlow<Resource<FirebaseData>>(Resource.Empty())
    override val data: StateFlow<Resource<FirebaseData>> = _resp

    fun init(context: Context, onError: (Exception) -> Unit = {}) {

    }

    fun getFirebaseAuth(): FirebaseAuth {
        return mFirebaseAuth
    }

    fun doFirebaseInit(
        context: Context,
        onSuccess: (FirebaseData) -> Unit,
        onError: (Exception) -> Unit = {}
    ) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        mFirebaseAuth = FirebaseAuth.getInstance()

        applicationScope.launch {
            try {
                val authResult = signInAnonymouslySuspend(mFirebaseAuth)
                val firebaseUser = authResult.user ?: throw Exception("No Firebase user")

                val fbtDeviceId = PrefRepo.getCachedOrAndroidId(context) // getOrCreateDeviceId(context) // now coroutine-safe

                //val fbtDeviceId = FirebaseRepo.getFirebaseInstallId();

                val fbData = FirebaseData.from(fbtDeviceId)
                fbData.mFirebaseAuth = mFirebaseAuth

                val idToken = getIdTokenSuspend(firebaseUser)

                val m_versionName = context.packageManager
                    .getPackageInfo(context.packageName, 0).versionName

                fbData.mFirebaseTokenTx = FirebaseTokenTx(
                    fbtToken = idToken ?: "",
                    fbtVersion = m_versionName ?: "",
                    fbtDeviceId = fbtDeviceId
                )

                onSuccess(fbData)

            } catch (e: Exception) {
                onError(e)
            }
        }
    }
    fun loadAppData(
        activity: Activity,
        appScope: CoroutineScope,
        onComplete: () -> Unit,
        onError: () -> Unit
    ) {
        doFirebaseInit(
            context = activity,
            onSuccess = { fbData ->
                appScope.launch {
                    val result = loadData(fbData, activity)
                    if (result is Resource.Success) {
                        onComplete()
                    } else {
                        onError()
                    }
                }
            },
            onError = {
                onError()
            }
        )
    }

    suspend fun loadAppData(context: Context): FirebaseData {
        val fbData = suspendCoroutine<FirebaseData> { continuation ->
            doFirebaseInit(context, onSuccess = {
                continuation.resume(it)
            }, onError = { ex ->
                continuation.resumeWithException(ex)
            })
        }

        val result = loadData(fbData, context)
        if (result is Resource.Success) {
            return result.data
        } else {
            throw Exception((result as? Resource.Error)?.message ?: "Data load failed")
        }
    }

    fun loadAppDataFlow(context: Context): Flow<Resource<FirebaseData>> = flow {
        emit(Resource.Loading())

        val fbData = suspendCancellableCoroutine<FirebaseData> { cont ->
            doFirebaseInit(
                context = context,
                onSuccess = { data -> cont.resume(data) },
                onError = { e -> cont.resumeWithException(e) }
            )
        }

        val loadResult = loadData(fbData, context)
        emit(loadResult)
    }.flowOn(Dispatchers.IO)


    suspend fun loadData(fbData: FirebaseData, context: Context): Resource<FirebaseData> {
        val firebaseRepo = FirebaseRepo
        _resp.value = Resource.Loading()

        val response = firebaseRepo.loadData(fbData.mFirebaseTokenTx)
        fbData.mFirebaseTokenRxResponse = response

        if (response is Resource.Success) {
            val fbtRx: FirebaseTokenRx = response.data.apiData
            val up = UserProfile.fromFirebase(fbtRx)
            UserRepo.loadDataByFirebase(up)
            MedsRepo.loadData(context)
            ServerRepo.loadData(context)
            _resp.value = Resource.Success(fbData)
        } else {
            val errMsg = "Error connecting to network\nClick Reload Data button in Settings->App Info"
            UserRepo.setCachedData(Resource.Error<UserProfileResponse>(message = errMsg))
            MedsRepo.setCachedData(Resource.Error<MedIndivInfoResponse>(message = errMsg))
            ServerRepo.setCachedData(Resource.Error<ServerInfoResponse>(message = errMsg))
            _resp.value = Resource.Error(message = errMsg)
        }

        SettingsRepo.initData(context)

        return _resp.value
    }


    suspend fun signInAnonymouslySuspend(auth: FirebaseAuth): AuthResult =
        suspendCoroutine { cont ->
            auth.signInAnonymously()
                .addOnSuccessListener { result -> cont.resume(result) }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }

    suspend fun getIdTokenSuspend(user: FirebaseUser): String? =
        suspendCoroutine { cont ->
            user.getIdToken(true)
                .addOnSuccessListener { result -> cont.resume(result.token) }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }

    fun signInAnonymously(onResult: (String) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user != null) {
            onResult(user.uid)
        } else {
            auth.signInAnonymously()
                .addOnSuccessListener {
                    val uid = it.user?.uid
                    if (uid != null) {
                        onResult(uid)
                    }
                }
                .addOnFailureListener {
                    Log.e("Auth", "Anonymous sign-in failed: ${it.message}")
                }
        }
    }


    fun logEvent(name: String, params: Map<String, String>? = null) {
        val bundle = android.os.Bundle().apply {
            params?.forEach { (key, value) ->
                putString(key, value)
            }
        }
        firebaseAnalytics.logEvent(name, bundle)
    }


    fun getInstance(): FirebaseAnalytics = firebaseAnalytics




    override suspend fun loadData(context: Context): Resource<FirebaseData> {
        TODO("Not yet implemented")
    }

    override fun getCachedData(): Resource<FirebaseData>? {
        return _resp.value
    }

    override fun setCachedData(newRes: Resource<FirebaseData>): Resource<FirebaseData>? {
        TODO("Not yet implemented")
    }

    override suspend fun refreshData(context: Context): Resource<FirebaseData> {
        TODO("Not yet implemented")
    }


}


