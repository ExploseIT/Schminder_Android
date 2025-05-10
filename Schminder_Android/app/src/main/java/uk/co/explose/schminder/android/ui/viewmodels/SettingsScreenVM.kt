

package uk.co.explose.schminder.android.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uk.co.explose.schminder.android.core.AppGlobal
import uk.co.explose.schminder.android.core.m_apg_data
import uk.co.explose.schminder.android.model.mpp.MedsRepo

class SettingsScreenVM(private val context: Context) : ViewModel() {

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
            val medsRepo = MedsRepo(context)
            val lSettings = medsRepo.loadSettings()
            if (lSettings.count() == 0) {
                medsRepo.insertSettings("setSoonMinutes","30")
                medsRepo.insertSettings("setMissedMinutes","120")
                medsRepo.insertSettings("setWindowMinutes","30")
            }
        }
    }

    fun getSettingsObj() : settingsObj {
        var ret = settingsObj()
        viewModelScope.launch {
            val medsRepo = MedsRepo(context)
            val lSettings = medsRepo.loadSettings()
            if (lSettings.count() >= 3) {
                ret.soonMinutes = lSettings["setSoonMinutes"]!!.toLong()
                ret.missedMinutes = lSettings["setMissedMinutes"]!!.toLong()
                ret.windowMinutes = lSettings["setWindowMinutes"]!!.toLong()
            }
        }
        return ret
    }

    fun updateSetting(key: String, newValue: String) {
        val setValue:String = newValue
        viewModelScope.launch {
            val medsRepo = MedsRepo(context)
            medsRepo.insertSettings(key, setValue)
        }
    }

}

data class settingsObj (
    var soonMinutes: Long = 30,
    var missedMinutes: Long = 120,
    var windowMinutes: Long = 60,
)


