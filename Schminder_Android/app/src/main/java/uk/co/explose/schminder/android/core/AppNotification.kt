
package uk.co.explose.schminder.android.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import uk.co.explose.schminder.android.MainActivity
import uk.co.explose.schminder.android.model.mpp.Med
import uk.co.explose.schminder.android.model.mpp.MedScheduled
import uk.co.explose.schminder.android.R
import uk.co.explose.schminder.android.mapper.MedScheduledDisplayItem
import uk.co.explose.schminder.android.model.mpp.MedScheduledWithMed


fun showNotification(context: Context, med: MedScheduledDisplayItem) {
    // Intent to open your MainActivity or another detail screen
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra("medRoute", "medTake/${med.medId}")
    }

    val pendingIntent = PendingIntent.getActivity(
        context,
        med.medId, // Unique request code
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(context, "med_channel")
        .setSmallIcon(R.drawable.ic_pill)
        .setContentTitle("Schminder Reminder")
        .setContentText("Time to take: ${med.medName}")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent) // ✅ attach click action
        .setAutoCancel(true) // Dismiss notification when clicked

    with(NotificationManagerCompat.from(context)) {
        notify(med.medId, builder.build())
    }
}

fun createNotificationChannel(context: Context) {
    val channel = NotificationChannel(
        "med_channel",
        "Medication Reminders",
        NotificationManager.IMPORTANCE_HIGH
    )
    val manager = context.getSystemService(NotificationManager::class.java)
    manager.createNotificationChannel(channel)
}

