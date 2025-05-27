package uk.co.explose.schminder.android.model.settings

import java.time.LocalDateTime

data class SettingsObj (
    var soonMinutes: Long = 30,
    var missedMinutes: Long = 120,
    var windowMinutes: Long = 60,
    var notificationMinutes: Long = 10
) {
    fun isWithinWindow(target: LocalDateTime, center: LocalDateTime): Boolean {
        val pre = center.minusMinutes(windowMinutes)
        val post = center.plusMinutes(windowMinutes)
        return target.isAfter(pre) && !target.isAfter(post)
    }
}

