

package uk.co.explose.schminder.android.core

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics

object GlobalAnalytics {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    fun init(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
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

