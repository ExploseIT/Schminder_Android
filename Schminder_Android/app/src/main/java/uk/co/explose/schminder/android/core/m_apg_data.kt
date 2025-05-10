
package uk.co.explose.schminder.android.core

import uk.co.explose.schminder.android.model.firebase.FirebaseTokenRx
import uk.co.explose.schminder.android.model.firebase.FirebaseTokenTx
import uk.co.explose.schminder.android.model.mpp.MedIndivInfo

import uk.co.explose.schminder.android.model.server_version.c_ServerVersion

data class m_apg_data (
    var mFirebaseToken: FirebaseTokenTx? = null,
    var mFirebaseTokenInfo: FirebaseTokenRx? = null,
    var m_medIndivInfo: MedIndivInfo? = null,
    var m_serverVersion: c_ServerVersion? = null,
    var m_versionName: String? = null
 ) {
    fun isLoaded() : Boolean {
        var ret: Boolean = false
        ret = mFirebaseTokenInfo != null &&
                m_serverVersion != null &&
                m_medIndivInfo != null
        return ret;
    }
}
