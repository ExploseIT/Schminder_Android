

package uk.co.explose.schminder.android.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import uk.co.explose.schminder.android.SchminderApplication
import uk.co.explose.schminder.android.core.AppDb
import uk.co.explose.schminder.android.core.AppRepo
import uk.co.explose.schminder.android.core.App_Db
import uk.co.explose.schminder.android.model.firebase.FirebaseData
import uk.co.explose.schminder.android.model.mpp.MedIndivInfo
import uk.co.explose.schminder.android.model.mpp.MedsRepo
import uk.co.explose.schminder.android.model.server.ServerRepo
import uk.co.explose.schminder.android.model.server_version.ServerInfo
import uk.co.explose.schminder.android.model.settings.AppSetting
import uk.co.explose.schminder.android.model.settings.SettingsObj
import uk.co.explose.schminder.android.model.settings.SettingsRepo
import uk.co.explose.schminder.android.repo.Resource

class SettingsScreenVM(private val context: Context) : ViewModel() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val medsRepo = MedsRepo

    var settingsList by mutableStateOf<List<AppSetting>?>(emptyList())
        private set

    var settingsObj by mutableStateOf<SettingsObj>(SettingsObj())
        internal

    var isLoading by mutableStateOf<Boolean>(true)

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var serverInfo by mutableStateOf<ServerInfo?>(null)

    var medIndivInfo by mutableStateOf<MedIndivInfo>(MedIndivInfo())

    var appData by mutableStateOf<FirebaseData?>(null)
        private set

    init {
        // Automatically try to load data on init
        loadVM()
    }

    fun loadVM() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val data = AppRepo.loadAppData(context) // handles init + load
                appData = data

        ServerRepo.loadData(context)
                serverInfo = when (val data = ServerRepo.getCachedData()) {
                    is Resource.Success -> {
                        var errorMessage = null
                        var isLoading = false
                        data.data.apiData
                    }
                    is Resource.Error -> {
                        val errMsgRaw = data.message
                        val errMsg = "Error loading data"
                        errorMessage = errMsg
                        ServerInfo.fromContext(context)
                    }
                    else -> {
                        ServerInfo.fromContext(context)
                    }
                }
                medsRepo.loadData(context)
                medIndivInfo = when (val data = medsRepo.getCachedData()) {
                    is Resource.Success -> {
                        val md = data.data.apiData
                        isLoading = false
                        md
                    }
                    is Resource.Error -> {
                        val errorMessageRaw = data.message
                        val errMsg = "Error loading data"
                        errorMessage = errMsg
                        isLoading = false
                        MedIndivInfo()
                    }
                    else -> {
                        MedIndivInfo()
                    }
                }

                settingsList = SettingsRepo.loadData(context)

                settingsObj = SettingsRepo.loadSettingsObj(context)

            } catch (e: Exception) {
                errorMessage = "Failed to load settings: ${e.localizedMessage}"
            } finally {
                //isLoading = false
            }
        }
    }


    fun initCheckSetting() {
        viewModelScope.launch {
            val descSoon = "If within this number of minutes then a 'Medication due soon' notification is presented"
            val descMissed = "If gone beyond this number of minutes then a 'Medication has been missed' notification is presented"
            val descWindow = "If within this number of minutes then the 'Take medication' message is shown"
            val descNotification = "The number of minutes between medication notification reminders"

            if (!doesKeyExist("setSoonMinutes")) {
                insertSetting(
                    AppSetting(
                        setKey = "setSoonMinutes",
                        setName = "Soon minutes",
                        setType = "scheduleType",
                        setValue = "30",
                        setDesc = descSoon
                    )
                )
            }

            if (!doesKeyExist("setMissedMinutes")) {
                insertSetting(
                    AppSetting(
                        setKey = "setMissedMinutes",
                        setName = "Missed minutes",
                        setType = "scheduleType",
                        setValue = "120",
                        setDesc = descMissed
                    )
                )
            }
            if (!doesKeyExist("setWindowMinutes")) {
                insertSetting(
                    AppSetting(
                        setKey = "setWindowMinutes",
                        setName = "Window minutes",
                        setType = "scheduleType",
                        setValue = "30",
                        setDesc = descWindow
                    )
                )
            }
            if (!doesKeyExist("setNotificationMinutes")) {
                insertSetting(
                    AppSetting(
                        setKey = "setNotificationMinutes",
                        setName = "Notification minutes",
                        setType = "scheduleType",
                        setValue = "10",
                        setDesc = descNotification
                    )
                )
            }
            doNameTypeDescUpdate("setSoonMinutes","Soon minutes", "scheduleType", descSoon)
            doNameTypeDescUpdate("setMissedMinutes","Missed minutes", "scheduleType", descMissed)
            doNameTypeDescUpdate("setWindowMinutes","Window minutes", "scheduleType", descWindow)
        }

    }



    suspend fun doesDescExist(key: String, desc: String) : Boolean {
        val dao = AppDb.getInstance(context).settingsDao()
        val thisDesc = dao.getDesc(key)
        var ret = false
        if (thisDesc == desc) {
            ret =true
        }
        return ret
    }

    suspend fun doesKeyExist(key: String) : Boolean {
        val dao = AppDb.getInstance(context).settingsDao()
        val value = dao.readValueByKey(key)
        var ret = false
        if (value != null) {
            if (value.isNotBlank() && value.isNotEmpty()) {
                ret = true
            }
        }
        return ret
    }

    suspend fun doNameTypeDescUpdate(key: String, name: String, type: String, desc: String) {
        val dao = AppDb.getInstance(context).settingsDao()
        val set = dao.readByKey(key)
        if (name != set.setName || type != set.setType || desc != set.setDesc) {
            dao.updateNameTypeDescByKey(key, name, type, desc)
        }
    }
    suspend fun loadSettings() : List<AppSetting> {
        val dao = AppDb.getInstance(context).settingsDao()
        val ret = dao.readAll()
        return ret
    }
    suspend fun insertSetting(setting: AppSetting) {
        val dao = AppDb.getInstance(context).settingsDao()
        dao.insert(setting)
    }

    suspend fun updateSettingValue(setId: Int, newValue: String) {
        val dao = AppDb.getInstance(context).settingsDao()
        dao.updateValue(setId, newValue)
    }

    suspend fun updateSettingDesc(setKey: String, newDesc: String) {
        val dao = AppDb.getInstance(context).settingsDao()
        dao.updateDescFromKey(setKey, newDesc)
    }
}




