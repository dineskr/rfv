package com.rfv.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rfv.app.caldav.CaldavCredentials

@Composable
fun CaldavSettingsScreen(
    credentials: CaldavCredentials,
    onSave: (CaldavCredentials) -> Unit
) {
    val server = remember { mutableStateOf(credentials.serverUrl) }
    val user = remember { mutableStateOf(credentials.username) }
    val password = remember { mutableStateOf(credentials.appPassword) }
    val calendar = remember { mutableStateOf(credentials.calendarId) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("CalDAV credentials")
        Spacer(Modifier.height(8.dp))
        CredentialField("Server URL", server)
        CredentialField("Username", user)
        CredentialField("App password", password)
        CredentialField("Calendar ID", calendar)
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            onSave(
                CaldavCredentials(
                    serverUrl = server.value,
                    username = user.value,
                    appPassword = password.value,
                    calendarId = calendar.value
                )
            )
        }) {
            Text("Save")
        }
    }
}

@Composable
private fun CredentialField(label: String, state: MutableState<String>) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = state.value,
        onValueChange = { state.value = it },
        label = { Text(label) }
    )
}
