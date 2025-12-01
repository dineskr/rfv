package com.rfv.app.caldav

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

data class CaldavCredentials(
    val serverUrl: String = "",
    val username: String = "",
    val appPassword: String = "",
    val calendarId: String = ""
)

class CaldavPreferences(private val context: Context) {
    private val prefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "caldav_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun load(): CaldavCredentials = CaldavCredentials(
        serverUrl = prefs.getString(KEY_SERVER, "") ?: "",
        username = prefs.getString(KEY_USERNAME, "") ?: "",
        appPassword = prefs.getString(KEY_PASSWORD, "") ?: "",
        calendarId = prefs.getString(KEY_CALENDAR, "") ?: ""
    )

    fun save(credentials: CaldavCredentials) {
        prefs.edit()
            .putString(KEY_SERVER, credentials.serverUrl)
            .putString(KEY_USERNAME, credentials.username)
            .putString(KEY_PASSWORD, credentials.appPassword)
            .putString(KEY_CALENDAR, credentials.calendarId)
            .apply()
    }

    companion object {
        private const val KEY_SERVER = "server"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_CALENDAR = "calendar"
    }
}
