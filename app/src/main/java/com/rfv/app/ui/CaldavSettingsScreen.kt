package com.rfv.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CaldavSettingsScreen(viewModel: CaldavCredentialsViewModel = viewModel()) {
    CaldavSettingsContent(
        state = viewModel.uiState,
        onServerUrlChange = viewModel::onServerUrlChanged,
        onUsernameChange = viewModel::onUsernameChanged,
        onPasswordChange = viewModel::onPasswordChanged,
        onCalendarIdChange = viewModel::onCalendarIdChanged,
        onSave = viewModel::save,
        onClear = viewModel::clear
    )
}

@Composable
fun CaldavSettingsContent(
    state: CaldavUiState,
    onServerUrlChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onCalendarIdChange: (String) -> Unit,
    onSave: () -> Unit,
    onClear: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "CalDAV account",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = state.serverUrl,
            onValueChange = onServerUrlChange,
            label = { Text("Server URL") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = { Text("App password / token") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.calendarId,
            onValueChange = onCalendarIdChange,
            label = { Text("Calendar ID (optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
            Text("Save & validate")
        }
        if (state.hasExisting) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onClear, modifier = Modifier.fillMaxWidth()) {
                Text("Clear stored credentials")
            }
        }
        state.statusMessage?.let { message ->
            Spacer(modifier = Modifier.height(12.dp))
            val color = when (state.statusLevel) {
                StatusLevel.ERROR -> MaterialTheme.colorScheme.error
                StatusLevel.SUCCESS -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            Text(text = message, color = color)
        }
    }
}
