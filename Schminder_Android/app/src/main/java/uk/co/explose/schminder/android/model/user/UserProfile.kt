
package uk.co.explose.schminder.android.model.profile

import uk.co.explose.schminder.android.core.ApiResponse
import uk.co.explose.schminder.android.model.firebase.FirebaseTokenRx
import java.util.UUID

data class UserProfileRequest(
    val up: UserProfile
)

typealias UserProfileResponse = ApiResponse<UserProfile>

data class UserProfile(
    val user_id: UUID = UUID(0, 0),
    val user_username: String = "",
    val user_firebase: String = "",
    //val user_deviceid: UUID = UUID(0,0),
    val user_deviceid: String = "",
    val user_enabled: Boolean = false,
    val user_alreadyexists: Boolean = false

) {
    companion object {
        fun fromFirebase(fb: FirebaseTokenRx): UserProfile {
            return UserProfile (
                user_firebase = fb.fbtUid,
                user_deviceid = fb.fbtDeviceId
            )
        }
        fun fromUsername(username: String): UserProfile {
            return UserProfile (
                user_username = username
            )
        }
    }

    fun userIsLoggedIn(): Boolean {
        val ret: Boolean = user_id != UUID(0, 0)
        return ret
    }

    fun getUserName(): String {
        var ret = ""
        if (user_id == UUID(0, 0) || user_username.isEmpty()) {
            ret = "Guest"
        }
        else {
            ret = this.user_username
        }
        return ret
    }
}

enum class enUserStatus(val value: Int)
{
    enUserDefault(0),
    enUserActive(1),
    enUserInactive(2),
    enUserLocked(3),
    enUserDeleted(4),
    enAlreadyExists(5),
    enUserNotFound(6)
}
