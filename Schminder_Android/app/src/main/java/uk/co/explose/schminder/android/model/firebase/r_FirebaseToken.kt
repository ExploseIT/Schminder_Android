package uk.co.explose.schminder.android.model.firebase

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
)

