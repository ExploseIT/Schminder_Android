

package uk.co.explose.schminder.android.core

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.explose.schminder.android.model.firebase.e_Firebase
import uk.co.explose.schminder.android.model.firebase.r_FirebaseToken
import uk.co.explose.schminder.android.model.firebase.s_FirebaseToken
import uk.co.explose.schminder.android.model.mpp.e_meds
import uk.co.explose.schminder.android.model.mpp.m_med_indiv_info



object AppGlobal {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var mFirebaseAuth: FirebaseAuth
    private var mFirebaseTokenInfo: r_FirebaseToken? = null
    private var _m_med_indiv_info: m_med_indiv_info? = null

    fun init(context: Context, onError: (Exception) -> Unit = {}) {

    }

    fun doFirebaseInit(context: Context, onError: (Exception) -> Unit = {}) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)

        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseAuth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = task.result?.user
                    firebaseUser?.getIdToken(true)?.addOnSuccessListener { result ->
                        val idToken = result.token
                        val _s_fbtoken = s_FirebaseToken(s_fbtoken = idToken.toString())
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                mFirebaseTokenInfo  = e_Firebase().pFirebaseToken(_s_fbtoken)
                                _m_med_indiv_info =  e_meds().doMedIndivLoad()
                            } catch (ex: Exception) {
                                onError(ex)
                            }
                        }
                    }
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

    fun medsGetCount() : Int {
        var ret: Int = 0

        if (_m_med_indiv_info != null && _m_med_indiv_info?.med_indiv_list != null) {
            ret = _m_med_indiv_info?.med_indiv_list?.count()!!
        }
        return ret
    }

    fun doMedsIndivInfoRead() : m_med_indiv_info? {
        return _m_med_indiv_info;
    }

    fun getInstance(): FirebaseAnalytics = firebaseAnalytics
}

