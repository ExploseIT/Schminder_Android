package uk.co.explose.schminder.android

import android.app.ActivityManager
import android.app.Application
import androidx.compose.ui.platform.LocalContext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import uk.co.explose.schminder.android.core.AppRepo


class SchminderApplication : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        AppRepo.init(this)

        /*
        AppRepo.doFirebaseInit(this, onSuccess = { fbData ->
            appScope.launch {
                AppRepo.loadData(fbData, this@SchminderApplication)
            }
        })

         */
    }
}




