package com.rfv.app.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rfv.app.caldav.CaldavCredentials
import com.rfv.app.caldav.CaldavPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CaldavCredentialsViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = CaldavPreferences(application)

    var uiState by mutableStateOf(CaldavUiState())
        private set

    init {
        load()
    }

    fun onServerUrlChanged(value: String) {
        uiState = uiState.copy(serverUrl = value)
    }

    fun onUsernameChanged(value: String) {
        uiState = uiState.copy(username = value)
    }

    fun onPasswordChanged(value: String) {
        uiState = uiState.copy(password = value)
    }

    fun onCalendarIdChanged(value: String) {
        uiState = uiState.copy(calendarId = value)
    }

    fun save() {
        val serverUrl = uiState.serverUrl.trim()
        val username = uiState.username.trim()
        val password = uiState.password

        if (serverUrl.isEmpty() || username.isEmpty() || password.isEmpty()) {
            uiState = uiState.copy(statusMessage = "Server URL, username, and password are required", statusLevel = StatusLevel.ERROR)
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(statusMessage = "Validating…", statusLevel = StatusLevel.INFO)
            simulateValidation()
            prefs.save(
                CaldavCredentials(
                    serverUrl = serverUrl,
                    username = username,
                    password = password,
                    calendarId = uiState.calendarId.ifBlank { null }
                )
            )
            uiState = uiState.copy(
                statusMessage = "Credentials saved securely",
                statusLevel = StatusLevel.SUCCESS,
                hasExisting = true
            )
        }
    }

    fun clear() {
        prefs.clear()
        uiState = CaldavUiState(statusMessage = "Credentials removed", statusLevel = StatusLevel.INFO)
    }

    private fun load() {
        val saved = prefs.load()
        if (saved != null) {
            uiState = uiState.copy(
                serverUrl = saved.serverUrl,
                username = saved.username,
                password = saved.password,
                calendarId = saved.calendarId.orEmpty(),
                hasExisting = true,
                statusMessage = "Loaded saved credentials",
                statusLevel = StatusLevel.INFO
            )
        }
    }

    private suspend fun simulateValidation() {
        // Placeholder for a connectivity check against the CalDAV endpoint.
        delay(400)
    }
}

data class CaldavUiState(
    val serverUrl: String = "",
    val username: String = "",
    val password: String = "",
    val calendarId: String = "",
    val hasExisting: Boolean = false,
    val statusMessage: String? = null,
    val statusLevel: StatusLevel = StatusLevel.NONE
)

enum class StatusLevel { NONE, INFO, ERROR, SUCCESS }
