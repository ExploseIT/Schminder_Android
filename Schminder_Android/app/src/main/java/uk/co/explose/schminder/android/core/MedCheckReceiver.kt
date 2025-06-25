
package uk.co.explose.schminder.android.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.explose.schminder.android.BuildConfig

import uk.co.explose.schminder.android.model.mpp.MedsRepo
import uk.co.explose.schminder.android.model.settings.SettingsObj
import uk.co.explose.schminder.android.ui.viewmodels.MedScheduleVM
import uk.co.explose.schminder.android.utils.dtObject
import java.time.LocalDateTime
import java.time.Duration
import java.time.LocalDate

class MedCheckReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            val modeDebug: Boolean = BuildConfig.DEBUG

            val settingsObj: SettingsObj? = App_Db(context).getSettingsObj()
            val now = dtObject().dtoNow
            val setNotificationWindowMinutes = settingsObj!!.notificationMinutes
            val today = dtObject().dtoDay

            val medsSchedule = MedsRepo.loadMedsSchedule(context)
            var medsScheduled = MedsRepo.getScheduledForDay(context,medsSchedule, today)

            var count = 0
            var shouldNotify = false
            medsScheduled.forEach { med ->
                if (!shouldNotify) {
                    if (med.medDTTaken == null) {
                        if (settingsObj!!.isWithinWindow(med.medDTDerived, now)) {
                            val last = med.medDTNotifyLast
                            shouldNotify =
                                last == null || modeDebug == true || Duration.between(last, now)
                                    .toMinutes() >= setNotificationWindowMinutes
                        }
                    }
                }
                if (shouldNotify) {
                    showNotification(context, med)
                    med.medDTNotifyLast = now
                    MedsRepo.saveMedLastNotified(context, med)
                }
                count++;
            }
        }
    }




}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleMedCheckAlarm(context)
            Log.d("BootReceiver", "Re-scheduled med check alarm after boot")
        }
    }
}
