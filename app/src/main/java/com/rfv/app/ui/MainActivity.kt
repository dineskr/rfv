package com.rfv.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rfv.app.caldav.CaldavCredentials
import com.rfv.app.caldav.CaldavPreferences
import com.rfv.app.ui.theme.RfvTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val caldavPrefs = CaldavPreferences(this)
        setContent { RfvApp(caldavPrefs) }
    }
}

@Composable
private fun RfvApp(caldavPreferences: CaldavPreferences) {
    RfvTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            MainScreen(caldavPreferences)
        }
    }
}

@Composable
private fun MainScreen(caldavPreferences: CaldavPreferences) {
    var statusText by remember { mutableStateOf("Ready to log activity") }
    var credentials by remember { mutableStateOf(caldavPreferences.load()) }

    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = "RFV personal logger",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Scaffold with placeholders for call logging, screen time tracking, manual logs, and CalDAV sync.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = { statusText = "Tracking components not yet implemented in this stub." }) {
            Text("Start tracking")
        }
        Spacer(Modifier.height(12.dp))
        Text(text = statusText)
        Spacer(Modifier.height(24.dp))
        CaldavSettingsScreen(credentials) { updated ->
            credentials = updated
            caldavPreferences.save(updated)
            statusText = "Saved CalDAV credentials"
        }
    }
}
