
package uk.co.explose.schminder.android.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import uk.co.explose.schminder.android.MainActivity
import uk.co.explose.schminder.android.ui.screens.SplashScreen
import uk.co.explose.schminder.android.ui.theme.SchminderTheme


class SplashActivity : ComponentActivity() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SchminderTheme {
                SplashScreen(appScope,
                    onComplete = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    // Correctly complete retry attempt
                    onRetry =  {

                    }
                )
            }
        }
    }
}

