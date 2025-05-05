

package uk.co.explose.schminder.android.core

import android.content.Context
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.explose.schminder.android.core.AppGlobal.m_apg
import uk.co.explose.schminder.android.model.firebase.FirebaseTokenRx
import uk.co.explose.schminder.android.model.firebase.FirebaseTokenTx
import uk.co.explose.schminder.android.model.firebase.e_Firebase
import uk.co.explose.schminder.android.model.mpp.MedsRepo
import uk.co.explose.schminder.android.model.mpp.MedIndivInfo
import uk.co.explose.schminder.android.model.server_version.c_ServerVersion
import uk.co.explose.schminder.android.network.RetrofitClient


object AppGlobal {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var m_apg: m_apg_data


    fun init(context: Context, onError: (Exception) -> Unit = {}) {

    }

    fun doFirebaseInit(context: Context, onError: (Exception) -> Unit = {}) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)

        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseAuth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = FirebaseAuth.getInstance().currentUser

                    firebaseUser?.getIdToken(true)?.addOnSuccessListener { result ->
                        val idToken = result.token
                        val fbtUserUid = firebaseUser.uid
                        m_apg = m_apg_data()
                        m_apg.m_versionName = context.packageManager
                            .getPackageInfo(context.packageName, 0).versionName
                        val fbtokenTx = FirebaseTokenTx(fbtToken = idToken.toString(),
                            fbtVersion = m_apg.m_versionName ?: ""
                        )
                        m_apg.mFirebaseTokenInfo = FirebaseTokenRx.from(fbtokenTx.fbtToken,
                            m_apg.m_versionName ?: "", firebaseUser)
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                m_apg.mFirebaseTokenInfo  = e_Firebase().pFirebaseToken(fbtokenTx)

                                m_apg.m_medIndivInfo =  MedsRepo(context).doMedIndivLoad()
                                m_apg.m_serverVersion = fetchServerVersion()

                            } catch (ex: Exception) {
                                onError(ex)
                            }
                        }
                    }
                }
            }
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
    suspend fun fetchServerVersion(): c_ServerVersion? {
        return try {
            val response = RetrofitClient.instance.getServerVersion()
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


    fun logEvent(name: String, params: Map<String, String>? = null) {
        val bundle = android.os.Bundle().apply {
            params?.forEach { (key, value) ->
                putString(key, value)
            }
        }
        firebaseAnalytics.logEvent(name, bundle)
    }

    fun medsGetCount() : Int {
        var ret: Int = 0

        if (m_apg.m_medIndivInfo != null && m_apg.m_medIndivInfo?.medIndivList != null) {
            ret = m_apg.m_medIndivInfo?.medIndivList?.count()!!
        }
        return ret
    }

    fun doAPGDataRead() : m_apg_data {
        return m_apg
    }


    fun getInstance(): FirebaseAnalytics = firebaseAnalytics
}


