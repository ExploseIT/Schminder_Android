
package uk.co.explose.schminder.android.ui.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uk.co.explose.schminder.android.model.mpp.Med
import android.content.Context
import androidx.compose.runtime.*
import uk.co.explose.schminder.android.model.mpp.MedIndiv
import uk.co.explose.schminder.android.model.mpp.MedsRepo

class HomeScreenVM(context: Context) : ViewModel() {

    private val medsRepo = MedsRepo(context)

    var parsedMeds by mutableStateOf<List<MedIndiv>>(emptyList())
        private set
    var scheduledMeds by mutableStateOf<List<Med>>(emptyList())
        private set

    var SettingsObj by mutableStateOf<settingsObj>(settingsObj())
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


                val lSettings = medsRepo.loadSettings()
                if (lSettings.count() >= 3) {
                    SettingsObj.soonMinutes = lSettings["setSoonMinutes"]!!.toLong()
                    SettingsObj.missedMinutes = lSettings["setMissedMinutes"]!!.toLong()
                    SettingsObj.windowMinutes = lSettings["setWindowMinutes"]!!.toLong()
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

