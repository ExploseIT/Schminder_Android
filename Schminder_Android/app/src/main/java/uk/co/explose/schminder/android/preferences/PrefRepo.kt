package uk.co.explose.schminder.android.preferences

import android.content.Context
import android.provider.Settings
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import uk.co.explose.schminder.android.core.PreferencesKeys
import uk.co.explose.schminder.android.core.PreferencesKeys.PREF_DEVICE_ID_KEY
import uk.co.explose.schminder.android.ui.components.dataStore
import java.io.File
import java.util.UUID
import java.security.MessageDigest
import java.util.Locale

object PrefRepo {


    suspend fun getOrCreateDeviceId(context: Context): String {
        val noBackupFile = File(context.noBackupFilesDir, "schminder_device_id.txt")

        // Step 1: Check DataStore
        val existingId = context.dataStore.data.first()[PreferencesKeys.PREF_DEVICE_ID_KEY]
        if (!existingId.isNullOrBlank()) {
            return existingId
        }

        // Step 2: Fallback to file if present
        val idFromFile: String? = if (noBackupFile.exists()) {
            noBackupFile.readText().takeIf { it.isNotBlank() }
        } else null

        // Step 3: If none exists, generate new UUID
        val finalId = idFromFile ?: UUID.randomUUID().toString()

        // Step 4: Save to DataStore
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.PREF_DEVICE_ID_KEY] = finalId
        }

        // Step 5: Also persist to file for uninstall-proofing
        noBackupFile.writeText(finalId)

        return finalId
    }

    suspend fun getCachedOrAndroidId(context: Context): String {
        // 1. Check DataStore first
        val prefs = context.dataStore.data.first()
        val cachedId = prefs[PreferencesKeys.PREF_DEVICE_ID_KEY]
        if (!cachedId.isNullOrBlank()) return cachedId

        // 2. Fallback to ANDROID_ID
        @Suppress("HardwareIds")
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "UNKNOWN_DEVICE_ID"

        val hashedId = androidId.sha256()
        // 3. Save to DataStore
        context.dataStore.edit { it[PreferencesKeys.PREF_DEVICE_ID_KEY] = hashedId }

        return hashedId
    }

    fun String.sha256(): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(this.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }.lowercase(Locale.ROOT)
    }

    suspend fun saveUsername(context: Context, username: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.PREF_USERNAME] = username
        }
    }

    suspend fun getUsername(context: Context): String {
        return context.dataStore.data
            .map { prefs -> prefs[PreferencesKeys.PREF_USERNAME] ?: "" }
            .first()
    }
}

