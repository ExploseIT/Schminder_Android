
package uk.co.explose.schminder.android.ui.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uk.co.explose.schminder.android.model.mpp.Med
import android.content.Context
import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import uk.co.explose.schminder.android.core.AppDb
import uk.co.explose.schminder.android.core.AppRepo
import uk.co.explose.schminder.android.core.MedsIndivDao
import uk.co.explose.schminder.android.model.firebase.FirebaseData
import uk.co.explose.schminder.android.model.firebase.FirebaseTokenRx
import uk.co.explose.schminder.android.model.mpp.MedIndiv
import uk.co.explose.schminder.android.model.mpp.MedIndivInfo
import uk.co.explose.schminder.android.model.mpp.MedIndivInfoResponse
import uk.co.explose.schminder.android.model.mpp.MedsRepo
import uk.co.explose.schminder.android.model.server.ServerRepo
import uk.co.explose.schminder.android.model.server_version.ServerInfo
import uk.co.explose.schminder.android.model.server_version.ServerInfoResponse
import uk.co.explose.schminder.android.model.settings.AppSetting
import uk.co.explose.schminder.android.model.settings.SettingsObj
import uk.co.explose.schminder.android.model.settings.SettingsRepo
import uk.co.explose.schminder.android.repo.Resource
import kotlin.coroutines.resumeWithException

class HomeScreenVM(context: Context) : ViewModel() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val medsRepo = MedsRepo

    var lSettings by mutableStateOf<List<AppSetting>>(emptyList())
        private set

    var context by mutableStateOf(context)

    var parsedMeds by mutableStateOf<List<MedIndiv>>(emptyList())
        private set
    var scheduledMeds by mutableStateOf<List<Med>>(emptyList())
        private set

    var settingsObj by mutableStateOf<SettingsObj>(SettingsObj())
        private set

    var serverInfo by mutableStateOf<ServerInfo?>(null)

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadVM(bFirstTime : Boolean) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            var allCount = 0
            var appResp: Resource<FirebaseData> = Resource.Empty<FirebaseData>()
            var medsResp: Resource<MedIndivInfoResponse> =
                Resource.Empty<MedIndivInfoResponse>()
            var serverResp: Resource<ServerInfoResponse> =
                Resource.Empty<ServerInfoResponse>()
            var appData: FirebaseData? = null

            try {
                if (!bFirstTime) {
                    allCount = 2
                    val fbData = suspendCancellableCoroutine<FirebaseData> { cont ->
                        AppRepo.doFirebaseInit(
                            context,
                            onSuccess = { cont.resume(it) {} },
                            onError = {
                                cont.resumeWithException(Exception("Firebase Init Failed"))
                            }
                        )
                    }

                    appResp = AppRepo.loadData(fbData, context)
                    appData = if (appResp is Resource.Success) {
                        allCount--
                        appResp.data
                    } else {
                        errorMessage = (appResp as? Resource.Error)?.message
                        null
                    }

                    serverResp = ServerRepo.loadData(context)
                    serverInfo = when (serverResp) {
                        is Resource.Success -> {
                            allCount--
                            serverResp.data.apiData
                        }

                        is Resource.Error -> {
                            val errMsg = serverResp.message
                            null
                        }

                        else -> {
                            null
                        }
                    }
                    medsResp = MedsRepo.loadData(context)
                    val medData = when (medsResp) {
                        is Resource.Success -> {
                            medsResp.data.apiData
                        }

                        is Resource.Error -> {
                            val errMsg = medsResp.message
                            MedIndivInfo()
                        }

                        else -> {
                            MedIndivInfo()
                        }
                    }

                }
                else {
                    allCount = 1
                    serverResp = ServerRepo.getCachedData()
                    val serverData = when (serverResp) {
                        is Resource.Success -> {
                            allCount--
                            serverResp.data.apiData
                        }

                        is Resource.Error -> {
                            val errMsg = serverResp.message
                            ServerInfo()
                        }

                        else -> {
                            ServerInfo()
                        }
                    }
                }

                if (allCount <= 0) {
                    scheduledMeds = medsRepo.medListAll(context)
                    parsedMeds = medsRepo.medIndivListAll(context)
                    lSettings = SettingsRepo.loadData(context)
                    isLoading = false
                }
                else {
                    errorMessage = "Data not loaded - Unable to connect to server"
                    isLoading = false
                }

            } catch (e: Exception) {
                errorMessage = "Unable to connect to server"
            } finally {
                //isLoading = false
            }
        }
    }

}

