
package uk.co.explose.schminder.android.model.mpp

import uk.co.explose.schminder.android.model.firebase.FirebaseTokenRx
import java.time.LocalDate


data class MedIndivAction (
    val medIndivName: String,
    val medIndivAction: Char,
    val medIndivUserUid: String,
)

data class MedIndivActionTx (
    val medIndivInfo: String = "Med Individual Info - Action List",
    val medIndivActionList: List<MedIndivAction>
) {
    companion object {
        fun from(medIndivList: List<MedIndivDto>, action: Char, userUid: String): MedIndivActionTx {
            val actions = medIndivList.map { dto ->
                MedIndivAction(
                    medIndivName = dto.medName,
                    medIndivAction = action,
                    medIndivUserUid = userUid
                )
            }
            return MedIndivActionTx(
                medIndivActionList = actions
            )
        }

        fun one(medIndivName: String, action: Char, userUid: String): MedIndivActionTx {
            val actions = listOf(
                MedIndivAction(
                    medIndivName = medIndivName,
                    medIndivAction = action,
                    medIndivUserUid = userUid
                )
            )
            return MedIndivActionTx(
                medIndivActionList = actions
            )
        }
    }

}





data class MedIndivActionRx (
    val medSuccess: Boolean,
    val medMessage: String? = null,
    val medCount: Int
)

