
package uk.co.explose.schminder.android.model.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import uk.co.explose.schminder.android.core.ApiResponse
import uk.co.explose.schminder.android.repo.Resource
import java.time.OffsetDateTime
import java.util.UUID


data class FirebaseTokenTx (
    var fbtDevice: String = "Android",
    var fbtDeviceId: String = "",
    var fbtToken: String = "",
    var fbtVersion: String = ""
) {
    companion object {
        fun from(fbtDeviceId: String) : FirebaseTokenTx {
            val fbt = FirebaseTokenTx()
            return FirebaseTokenTx (
                fbtDeviceId = fbtDeviceId,
                fbtDevice = fbt.fbtDevice,
                fbtToken = fbt.fbtToken,
                fbtVersion = fbt.fbtVersion
            )
        }
    }
}

data class FirebaseData (
    var mFirebaseTokenRxResponse: Resource<FirebaseTokenRxResponse> = Resource.Empty(),
    var mFirebaseTokenTx: FirebaseTokenTx = FirebaseTokenTx(),
    var mFirebaseAuth: FirebaseAuth? = null
) {
    companion object {
        fun from(fbtDeviceId: String): FirebaseData {
            val fbtTx = FirebaseTokenTx.from(fbtDeviceId)
            return FirebaseData (
                mFirebaseTokenRxResponse = Resource.Empty(),
                mFirebaseTokenTx = fbtTx,
                mFirebaseAuth = null
            )
        }
    }
}


typealias FirebaseTokenRxResponse = ApiResponse<FirebaseTokenRx>

data class FirebaseTokenRx (
    var fbtId: UUID,
    var fbtVersion: String,
    var fbtUid: String,
    var fbtToken: String,
    var fbtDeviceId: String, //var fbtDeviceId: UUID,
    var fbtExpiresAt: OffsetDateTime?,
    var fbtIssuedAt: OffsetDateTime?,
    var fbtCreatedAt: OffsetDateTime?,
    var fbtLastUsedAt: OffsetDateTime?,
    var fbtIsAnonymous: Boolean,
    var fbtStatus: String,
    var fbtError: String,
    var fbtUserName: String = "Guest"
) {
    companion object {
        fun from(fbtToken: String, fbtVersion: String, fbtDeviceId: String, firebaseUser: FirebaseUser): FirebaseTokenRx {
            return FirebaseTokenRx(
                fbtId = UUID.randomUUID(),
                fbtVersion = fbtVersion,
                fbtUid = firebaseUser.uid,
                fbtToken = fbtToken,
                fbtDeviceId = fbtDeviceId,
                fbtExpiresAt = null,
                fbtIssuedAt = null,
                fbtCreatedAt = null,
                fbtLastUsedAt = null,
                fbtIsAnonymous = firebaseUser.isAnonymous,
                fbtStatus = "pending",
                fbtError = ""
            )
        }
    }
}


