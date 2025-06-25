package uk.co.explose.schminder.android.model.settings

import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uk.co.explose.schminder.android.core.ApiResponse
import uk.co.explose.schminder.android.core.AppDb
import uk.co.explose.schminder.android.model.server_version.ServerInfoResponse
import uk.co.explose.schminder.android.repo.RepositoryBase
import uk.co.explose.schminder.android.repo.Resource

object SettingsRepo  {

    suspend fun insertSettings(context: Context, key: String, name: String, type: String, value: String, desc: String) {
        val setting = AppSetting(setKey = key, setValue = value, setName = name, setType = type, setDesc = desc)
        AppDb.getInstance(context).settingsDao().insert(setting)
    }


     suspend fun loadData(context: Context): List<AppSetting> {
        val ret = AppDb.getInstance(context).settingsDao().readAll()
        return ret
    }

    suspend fun initData(context: Context) : Boolean {
        initCheckSetting(context)
        return true
    }

    suspend fun loadSettingsObj(context: Context) : SettingsObj {
        val lSettings = loadData(context)
        var ret = SettingsObj()
            ret.soonMinutes = lSettings.first { it.setKey == "setSoonMinutes" }.setValue.toLong()
            ret.missedMinutes = lSettings.first { it.setKey == "setMissedMinutes" }.setValue.toLong()
            ret.windowMinutes = lSettings.first { it.setKey == "setWindowMinutes" }.setValue.toLong()

        return ret
        }


    suspend fun initCheckSetting(context: Context) {

        val descSoon =
            "If within this number of minutes then a 'Medication due soon' notification is presented"
        val descMissed =
            "If gone beyond this number of minutes then a 'Medication has been missed' notification is presented"
        val descWindow =
            "If within this number of minutes then the 'Take medication' message is shown"
        val descNotification = "The number of minutes between medication notification reminders"

        if (!doesKeyExist("setSoonMinutes", context)) {
            insertSetting(
                AppSetting(
                    setKey = "setSoonMinutes",
                    setName = "Soon minutes",
                    setType = "scheduleType",
                    setValue = "30",
                    setDesc = descSoon
                ),
                context
            )
        }

        if (!doesKeyExist("setMissedMinutes", context)) {
            insertSetting(
                AppSetting(
                    setKey = "setMissedMinutes",
                    setName = "Missed minutes",
                    setType = "scheduleType",
                    setValue = "120",
                    setDesc = descMissed
                )
                , context
            )
        }
        if (!doesKeyExist("setWindowMinutes", context)) {
            insertSetting(
                AppSetting(
                    setKey = "setWindowMinutes",
                    setName = "Window minutes",
                    setType = "scheduleType",
                    setValue = "30",
                    setDesc = descWindow
                )
                , context
            )
        }
        if (!doesKeyExist("setNotificationMinutes", context)) {
            insertSetting(
                AppSetting(
                    setKey = "setNotificationMinutes",
                    setName = "Notification minutes",
                    setType = "scheduleType",
                    setValue = "10",
                    setDesc = descNotification
                )
                , context
            )
        }
    }
    suspend fun doesKeyExist(key: String, context: Context) : Boolean {
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

    suspend fun insertSetting(setting: AppSetting, context: Context) {
        val dao = AppDb.getInstance(context).settingsDao()
        dao.insert(setting)
    }
}