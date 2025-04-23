

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


object GlobalAnalytics {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mFirebaseTokenInfo: r_FirebaseToken

    fun init(context: Context) {
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
                           val r_val = e_Firebase().pFirebaseToken(_s_fbtoken)!!
                            mFirebaseTokenInfo = r_val
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

    fun getInstance(): FirebaseAnalytics = firebaseAnalytics
}

