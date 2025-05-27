
package uk.co.explose.schminder.android.ui.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uk.co.explose.schminder.android.model.mpp.Med
import android.content.Context
import androidx.compose.runtime.*
import uk.co.explose.schminder.android.core.AppDb
import uk.co.explose.schminder.android.model.mpp.MedIndiv
import uk.co.explose.schminder.android.model.mpp.MedsRepo
import uk.co.explose.schminder.android.model.settings.SettingsObj
import uk.co.explose.schminder.android.model.settings.SettingsRepo

class HomeScreenVM(context: Context) : ViewModel() {

    private val medsRepo = MedsRepo(context)

    private val settingsRepo = SettingsRepo(context)

    var parsedMeds by mutableStateOf<List<MedIndiv>>(emptyList())
        private set
    var scheduledMeds by mutableStateOf<List<Med>>(emptyList())
        private set

    var settingsObj by mutableStateOf<SettingsObj>(SettingsObj())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadVM() {
        viewModelScope.launch {
            isLoading = true
            try {
                scheduledMeds = medsRepo.medListAll()
                parsedMeds = medsRepo.medIndivListAll()


                val lSettings = settingsRepo.loadSettings()
                if (lSettings.size >= 3) {
                    settingsObj.soonMinutes = lSettings.first { it.setKey == "setSoonMinutes" }.setValue.toLong()
                    settingsObj.missedMinutes = lSettings.first { it.setKey == "setMissedMinutes" }.setValue.toLong()
                    settingsObj.windowMinutes = lSettings.first { it.setKey == "setWindowMinutes" }.setValue.toLong()
                }

                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Unable to connect to server"
            } finally {
                isLoading = false
            }
        }
    }

}

