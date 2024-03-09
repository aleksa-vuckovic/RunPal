package com.example.runpal.activities.running.event

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.runpal.EVENT_ID_KEY
import com.example.runpal.LoadingScreen
import com.example.runpal.R
import com.example.runpal.RUN_MARKER_COLORS
import com.example.runpal.activities.results.event.EventRunResultsActivity
import com.example.runpal.activities.running.RunCountown
import com.example.runpal.activities.running.RunDataPanel
import com.example.runpal.activities.running.RunPause
import com.example.runpal.activities.running.RunResume
import com.example.runpal.hasLocationPermission
import com.example.runpal.models.Run
import com.example.runpal.repositories.SettingsManager
import com.example.runpal.ui.GoogleMapRun
import com.example.runpal.ui.theme.RunPalTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class EventRunActivity : ComponentActivity() {

    private val vm: EventRunViewModel by viewModels()
    val locationListener: LocationListener = object: LocationListener {
        override fun onLocationChanged(p0: Location) {
            vm.updateLocation(p0)
        }
    }
    lateinit var provider: FusedLocationProviderClient
    var shortbeep: MediaPlayer? = null
    var longbeep: MediaPlayer? = null

    @Inject
    lateinit var settingsManager: SettingsManager

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        shortbeep = MediaPlayer.create(this, R.raw.shortbeep)
        longbeep = MediaPlayer.create(this, R.raw.longbeep)

        if (!this.hasLocationPermission()) this.finish()
        provider = LocationServices.getFusedLocationProviderClient(this)
        val req = LocationRequest.Builder(200).setMaxUpdateAgeMillis(0).setPriority(
            Priority.PRIORITY_HIGH_ACCURACY).build()
        provider.requestLocationUpdates(req, locationListener, null)

        setContent {
            RunPalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LaunchedEffect(key1 = vm.state) {
                        if (vm.state == EventRunViewModel.State.FAILED) finish()
                    }

                    var units by remember {
                        mutableStateOf(settingsManager.units)
                    }
                    var pace by remember {
                        mutableStateOf(false)
                    }

                    if (vm.state == EventRunViewModel.State.LOADING) LoadingScreen()
                    else if (vm.state == EventRunViewModel.State.LOADED) Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        RunDataPanel(runState = vm.runState,
                            units = units, onChangeUnits = {units = units.next},
                            pace = pace, onChangePace = {pace = !pace},
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp))
                        Box(modifier = Modifier.fillMaxSize()) {

                            GoogleMapRun(
                                runStates = listOf(vm.runState),
                                markers = listOf(vm.marker),
                                colors = RUN_MARKER_COLORS,
                                mapState = vm.mapState,
                                onCenterSwitch = vm::centerSwitch,
                                modifier = Modifier.fillMaxSize()
                            )

                            val state = vm.runState.run.state
                            if (state == Run.State.LOADING) LoadingScreen(dotSize = 15.dp)
                            else if (state == Run.State.READY) RunCountown(till = vm.event.time, onStart = {
                                vm.start()
                                longbeep?.start()
                            }, sound = shortbeep)
                            else if (state == Run.State.RUNNING) RunPause(onPause = vm::pause, onFinish = vm::end)
                            else if (state == Run.State.PAUSED) RunResume(onResume = vm::resume, onFinish = vm::end)
                            LaunchedEffect(key1 = state) {
                                if (state == Run.State.ENDED) {
                                    delay(200) //giving time for the server update
                                    val eventID = intent.getStringExtra(EVENT_ID_KEY)
                                    this@EventRunActivity.finish()
                                    val intent = Intent(this@EventRunActivity, EventRunResultsActivity::class.java)
                                    intent.putExtra(EVENT_ID_KEY, eventID)
                                    startActivity(intent)
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        provider.removeLocationUpdates(locationListener)
        shortbeep?.release()
        longbeep?.release()
        shortbeep = null
        longbeep = null
    }
}