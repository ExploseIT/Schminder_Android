package uk.co.explose.schminder.android.model.settings

import android.content.Context
import uk.co.explose.schminder.android.core.AppDb

class SettingsRepo(private val context: Context)  {

    suspend fun insertSettings(context: Context, key: String, name: String, type: String, value: String, desc: String) {
        val setting = AppSetting(setKey = key, setValue = value, setName = name, setType = type, setDesc = desc)
        AppDb.getInstance(context).settingsDao().insert(setting)
    }

    suspend fun loadSettings(): List<AppSetting> {
        val list = AppDb.getInstance(context).settingsDao().readAll()
        return list
    }
}