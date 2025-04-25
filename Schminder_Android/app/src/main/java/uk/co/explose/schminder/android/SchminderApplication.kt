package uk.co.explose.schminder.android

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import android.os.Process
import uk.co.explose.schminder.android.core.AppGlobal

class SchminderApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        AppGlobal.init(this)

        AppGlobal.doFirebaseInit(this)
    }
}




