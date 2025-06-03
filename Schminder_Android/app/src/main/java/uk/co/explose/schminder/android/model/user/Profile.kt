
package uk.co.explose.schminder.android.model.profile

data class UserProfileRequest(
    val profUserFirebase: String,
    val profUsername: String
)

data class UserProfileResponse(
    val profSuccess: Boolean,
    val profMessage: String
)

