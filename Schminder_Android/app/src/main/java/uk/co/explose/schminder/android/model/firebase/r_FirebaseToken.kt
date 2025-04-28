package uk.co.explose.schminder.android.model.firebase

import com.google.firebase.auth.FirebaseUser
import java.time.OffsetDateTime
import java.util.UUID

data class r_FirebaseToken (
    var fbt_id: UUID,
    var fbt_uid: String,
    var fbt_token: String,
    var fbt_expiresat: OffsetDateTime?,
    var fbt_issuedat: OffsetDateTime?,
    var fbt_createdat: OffsetDateTime?,
    var fbt_lastusedat: OffsetDateTime?,
    var fbt_isanonymous: Boolean,
    var fbt_status: String,
    var fbt_error: String
) {
    companion object {
        fun from(idToken: String, firebaseUser: FirebaseUser): r_FirebaseToken {
            return r_FirebaseToken(
                fbt_id = UUID.randomUUID(),
                fbt_uid = firebaseUser.uid,
                fbt_token = idToken,
                fbt_expiresat = null,
                fbt_issuedat = null,
                fbt_createdat = null,
                fbt_lastusedat = null,
                fbt_isanonymous = firebaseUser.isAnonymous,
                fbt_status = "pending",
                fbt_error = ""
            )
        }
    }
}
