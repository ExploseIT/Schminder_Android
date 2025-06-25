

package uk.co.explose.schminder.android.model.server_version

import android.content.Context
import uk.co.explose.schminder.android.core.ApiResponse

typealias ServerInfoResponse = ApiResponse<ServerInfo>

data class ServerInfo (
    var svVersionApp: String = "",
    var svVersionServer: String = "",
    var svVersionDb: String = "",
    var svAppDb: Int = DbConstants.ROOM_DB_VERSION // To hold the Rooms db version number
) {
    companion object {
        fun fromContext(context: Context): ServerInfo {
            return ServerInfo (
            svVersionApp = context.packageManager
                .getPackageInfo(context.packageName, 0).versionName ?: "Not found"
            )
        }
    }
}


object DbConstants {
    const val ROOM_DB_VERSION = 23
}
