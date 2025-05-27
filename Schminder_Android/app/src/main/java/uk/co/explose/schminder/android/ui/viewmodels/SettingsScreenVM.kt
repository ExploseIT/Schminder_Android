

package uk.co.explose.schminder.android.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uk.co.explose.schminder.android.core.AppDb
import uk.co.explose.schminder.android.core.AppGlobal
import uk.co.explose.schminder.android.core.App_Db
import uk.co.explose.schminder.android.core.m_apg_data
import uk.co.explose.schminder.android.model.mpp.MedsRepo
import uk.co.explose.schminder.android.model.settings.AppSetting
import uk.co.explose.schminder.android.model.settings.SettingsObj
import uk.co.explose.schminder.android.model.settings.SettingsRepo

class SettingsScreenVM(private val context: Context) : ViewModel() {

    val settingsRepo = SettingsRepo(context)

    var settingsList by mutableStateOf<List<AppSetting>?>(null)
        private set

    var apg_data by mutableStateOf<m_apg_data?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        // Automatically try to load data on init
        loadVM()
    }

    fun loadVM() {
        viewModelScope.launch {
            isLoading = true
            try {
                settingsList = loadSettings()

                // Step 1: Read current in-memory apg_data
                var currentData = AppGlobal.doAPGDataRead()

                // Step 2: If it's not fully loaded, try loading it
                if (!currentData.isLoaded()) {
                    val fbToken = currentData.mFirebaseToken
                    if (fbToken != null) {
                        AppGlobal.doAPGDataLoad(context, fbToken)
                        currentData = AppGlobal.doAPGDataRead() // reload after populate
                    } else {
                        throw Exception("Missing Firebase token. Sign-in might have failed.")
                    }
                }

                // Step 3: Store result in state
                apg_data = currentData
                errorMessage = null

            } catch (e: Exception) {
                errorMessage = "Failed to load settings: ${e.localizedMessage}"
            } finally {
                isLoading = false
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

    fun getSettingsObj() : SettingsObj {
        var ret = SettingsObj()
        viewModelScope.launch {
            val ret = App_Db(context).getSettingsObj()
        }
        return ret
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




