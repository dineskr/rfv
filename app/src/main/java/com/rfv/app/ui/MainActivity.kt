package com.rfv.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.geojson.Point
import com.rfv.app.data.EventRepository
import com.rfv.app.data.RfvDatabase
import com.rfv.app.tracking.ForegroundUsageService
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    private val repo by lazy { EventRepository(RfvDatabase.build(this).eventDao()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val snackbarHostState = remember { SnackbarHostState() }
                    val scope = rememberCoroutineScope()

                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(vertical = 24.dp)
                        ) {
                            ManualLogScreen(
                                onSave = { entry ->
                                    if (entry.end.isBefore(entry.start)) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "End time must be after start time",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                        return@ManualLogScreen
                                    }
                                    repo.logManual(
                                        title = entry.title.ifBlank { null },
                                        notes = entry.notes.ifBlank { null },
                                        start = entry.start,
                                        end = entry.end,
                                        locationLat = entry.location?.latitude(),
                                        locationLng = entry.location?.longitude(),
                                        locationName = entry.locationName.ifBlank { null }
                                    )
                                    setTitle("")
                                    setNotes("")
                                    setLocationName("")
                                    setLocation(null)
                                    setStart(Instant.now())
                                    setEnd(Instant.now())
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Manual log saved",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                },
                                onStartTracking = {
                                    startService(Intent(this, ForegroundUsageService::class.java))
                                }
                            )
                            Divider(modifier = Modifier.padding(vertical = 16.dp))
                            CaldavSettingsScreen()
                            Spacer(modifier = Modifier.height(32.dp))
                        }

                        SnackbarHost(hostState = snackbarHostState, modifier = Modifier
                            .align(androidx.compose.ui.Alignment.BottomCenter)
                            .padding(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ManualLogScreen(onSave: (ManualLogEntry) -> Unit, onStartTracking: () -> Unit) {
    val (title, setTitle) = remember { mutableStateOf("") }
    val (notes, setNotes) = remember { mutableStateOf("") }
    val (locationName, setLocationName) = remember { mutableStateOf("") }
    val (location, setLocation) = remember { mutableStateOf<Point?>(null) }
    val (start, setStart) = remember { mutableStateOf(Instant.now()) }
    val (end, setEnd) = remember { mutableStateOf(Instant.now()) }

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(text = "Manual log", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = title, onValueChange = setTitle, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = notes, onValueChange = setNotes, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        DateTimeRow("Start", start, onUpdate = setStart)
        Spacer(modifier = Modifier.height(8.dp))
        DateTimeRow("End", end, onUpdate = setEnd)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Location", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        MapPicker(selected = location, onSelected = setLocation)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = locationName,
            onValueChange = setLocationName,
            label = { Text("Location label (optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            onSave(
                ManualLogEntry(
                    title = title,
                    notes = notes,
                    start = start,
                    end = end,
                    location = location,
                    locationName = locationName
                )
            )
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Save manual log")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onStartTracking, modifier = Modifier.fillMaxWidth()) {
            Text("Start screen-time tracking")
        }
    }
}

private val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")

@Composable
private fun DateTimeRow(label: String, instant: Instant, onUpdate: (Instant) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val zone = ZoneId.systemDefault()
    val formatted = remember(instant) { dateFormatter.format(instant.atZone(zone)) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = formatted, style = MaterialTheme.typography.bodyMedium)
            Row {
                Button(onClick = { onUpdate(Instant.now()) }) { Text("Now") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    val ldt = LocalDateTime.ofInstant(instant, zone)
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    val picked = LocalDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute)
                                    onUpdate(picked.atZone(zone).toInstant())
                                },
                                ldt.hour,
                                ldt.minute,
                                false
                            ).show()
                        },
                        ldt.year,
                        ldt.monthValue - 1,
                        ldt.dayOfMonth
                    ).show()
                }) { Text("Edit") }
            }
        }
    }
}

@Composable
private fun MapPicker(selected: Point?, onSelected: (Point) -> Unit) {
    val mapView = rememberMapViewWithLifecycle()
    val mapboxMap = mapView.getMapboxMap()

    LaunchedEffect(mapView) {
        mapboxMap.loadStyleUri(Style.MAPBOX_STREETS)
    }

    AndroidView(
        factory = {
            mapView.apply {
                mapboxMap.addOnMapClickListener { point ->
                    onSelected(point)
                    mapboxMap.setCamera(
                        CameraOptions.Builder()
                            .center(point)
                            .zoom(14.0)
                            .build()
                    )
                    true
                }
            }
        },
        update = {
            selected?.let {
                mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(it)
                        .zoom(14.0)
                        .build()
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    )

    if (selected != null) {
        Text(
            text = "Selected: ${selected.latitude()}, ${selected.longitude()}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    } else {
        Text(
            text = "Tap on the map to set a location",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

private data class ManualLogEntry(
    val title: String,
    val notes: String,
    val start: Instant,
    val end: Instant,
    val location: Point?,
    val locationName: String
)

@Composable
private fun rememberMapViewWithLifecycle(): MapView {
    val context = androidx.compose.ui.platform.LocalContext.current
    val lifecycle = androidx.compose.ui.platform.LocalLifecycleOwner.current.lifecycle
    val mapView = remember { MapView(context) }

    DisposableEffect(lifecycle, mapView) {
        val observer = object : androidx.lifecycle.DefaultLifecycleObserver {
            override fun onStart(owner: androidx.lifecycle.LifecycleOwner) {
                mapView.onStart()
            }

            override fun onResume(owner: androidx.lifecycle.LifecycleOwner) {
                mapView.onResume()
            }

            override fun onPause(owner: androidx.lifecycle.LifecycleOwner) {
                mapView.onPause()
            }

            override fun onStop(owner: androidx.lifecycle.LifecycleOwner) {
                mapView.onStop()
            }

            override fun onDestroy(owner: androidx.lifecycle.LifecycleOwner) {
                mapView.onLowMemory()
                mapView.onDestroy()
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    return mapView
}
