
package uk.co.explose.schminder.android.core

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

fun scheduleMedCheckAlarm(context: Context, force: Boolean = false): Boolean {
    if (!force && isMedCheckAlarmScheduled(context)) return false

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, MedCheckReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val intervalMillis = 60 * 1000L // every 1 minute

    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        System.currentTimeMillis(),
        intervalMillis,
        pendingIntent
    )

    // 🔔 Trigger it immediately now
    context.sendBroadcast(intent)

    return true
}

fun forceRescheduleMedCheckAlarm(context: Context): Boolean {
    cancelMedCheckAlarm(context)
    return scheduleMedCheckAlarm(context, force = true)
}

fun cancelMedCheckAlarm(context: Context) {
    val intent = Intent(context, MedCheckReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
    )
    if (pendingIntent != null) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}

fun isMedCheckAlarmScheduled(context: Context): Boolean {
    val intent = Intent(context, MedCheckReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
    )
    return pendingIntent != null
}
