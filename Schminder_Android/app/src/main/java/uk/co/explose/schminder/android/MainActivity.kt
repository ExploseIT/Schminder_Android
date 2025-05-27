
package uk.co.explose.schminder.android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.*
import com.google.firebase.FirebaseApp
import uk.co.explose.schminder.android.core.createNotificationChannel
import uk.co.explose.schminder.android.core.forceRescheduleMedCheckAlarm
import uk.co.explose.schminder.android.core.scheduleMedCheckAlarm
import uk.co.explose.schminder.android.ui.theme.SchminderTheme
import androidx.appcompat.app.AlertDialog
import android.provider.Settings


class MainActivity : ComponentActivity() {

    private lateinit var requestNotificationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialRoute = intent?.getStringExtra("medRoute")

        if (initialRoute != null) {
            Log.d("Notification", "Launched from notification for initial route = $initialRoute")
            // You could navigate to a detail screen or highlight the scheduled item
        }

        // Setup the modern permission launcher
        requestNotificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (!isGranted) {
                showPermissionDeniedDialog()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        createNotificationChannel(this)

        // Other setup...
        val isScheduled = scheduleMedCheckAlarm(this)
        if (!isScheduled) {
            Log.i("Notifications", "AlarmManager was already scheduled — not rescheduling.")
            val reScheduled = forceRescheduleMedCheckAlarm(this)
            Log.i("Notifications", "AlarmManager rescheduled = $reScheduled")

        } else {
            Log.i("Notifications", "AlarmManager scheduled successfully.")
        }

        setContent {
            SchminderTheme {
                SchminderMain(initialRoute)
            }
        }
        intent?.removeExtra("medId")

    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notifications Disabled")
            .setMessage("To receive medication reminders, please enable notifications in system settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)
                )
                startActivity(intent)
            }
            .setNegativeButton("Dismiss", null)
            .show()
    }
}

