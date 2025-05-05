
package uk.co.explose.schminder.android.model.firebase

import com.google.firebase.auth.FirebaseUser
import java.time.OffsetDateTime
import java.util.UUID

data class FirebaseTokenRx (
    var fbtId: UUID,
    var fbtVersion: String?,
    var fbtUid: String,
    var fbtToken: String,
    var fbtExpiresAt: OffsetDateTime?,
    var fbtIssuedAt: OffsetDateTime?,
    var fbtCreatedAt: OffsetDateTime?,
    var fbtLastUsedAt: OffsetDateTime?,
    var fbtIsAnonymous: Boolean,
    var fbtStatus: String,
    var fbtError: String
) {
    companion object {
        fun from(fbtToken: String, fbtVersion: String?, firebaseUser: FirebaseUser): FirebaseTokenRx {
            return FirebaseTokenRx(
                fbtId = UUID.randomUUID(),
                fbtVersion = fbtVersion,
                fbtUid = firebaseUser.uid,
                fbtToken = fbtToken,
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
