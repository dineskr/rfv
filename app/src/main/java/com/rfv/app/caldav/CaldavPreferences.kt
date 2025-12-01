package com.rfv.app.caldav

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Secure storage for CalDAV credentials and calendar selection.
 */
class CaldavPreferences(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        PREF_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun save(credentials: CaldavCredentials) {
        prefs.edit()
            .putString(KEY_SERVER_URL, credentials.serverUrl)
            .putString(KEY_USERNAME, credentials.username)
            .putString(KEY_PASSWORD, credentials.password)
            .putString(KEY_CALENDAR_ID, credentials.calendarId)
            .apply()
    }

    fun load(): CaldavCredentials? {
        val url = prefs.getString(KEY_SERVER_URL, null) ?: return null
        val username = prefs.getString(KEY_USERNAME, null) ?: return null
        val password = prefs.getString(KEY_PASSWORD, null) ?: return null
        val calendarId = prefs.getString(KEY_CALENDAR_ID, null)
        return CaldavCredentials(url, username, password, calendarId)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREF_NAME = "caldav_credentials"
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_CALENDAR_ID = "calendar_id"
    }
}

data class CaldavCredentials(
    val serverUrl: String,
    val username: String,
    val password: String,
    val calendarId: String?
)
