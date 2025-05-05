
package uk.co.explose.schminder.android.core

import uk.co.explose.schminder.android.model.firebase.FirebaseTokenRx
import uk.co.explose.schminder.android.model.mpp.MedIndivInfo

import uk.co.explose.schminder.android.model.server_version.c_ServerVersion

data class m_apg_data (
    var mFirebaseTokenInfo: FirebaseTokenRx? = null,
    var m_medIndivInfo: MedIndivInfo? = null,
    var m_serverVersion: c_ServerVersion? = null,
    var m_versionName: String? = null
)
