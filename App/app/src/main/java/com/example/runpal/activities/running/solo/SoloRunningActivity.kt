package com.example.runpal.activities.running.solo

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.runpal.RUNNER_ICON_SIZE
import com.example.runpal.RUN_ID_KEY
import com.example.runpal.activities.results.SoloRunningResults
import com.example.runpal.activities.running.RunDataPanel
import com.example.runpal.activities.running.RunPause
import com.example.runpal.activities.running.RunResume
import com.example.runpal.activities.running.RunStart
import com.example.runpal.getRunnerBitmap
import com.example.runpal.hasLocationPermission
import com.example.runpal.models.Run
import com.example.runpal.repositories.LoginManager
import com.example.runpal.ui.GoogleMapRun
import com.example.runpal.ui.theme.MediumBlue
import com.example.runpal.ui.theme.RunPalTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SoloRunningActivity : ComponentActivity() {

    @Inject
    lateinit var loginManager: LoginManager

    val vm: SoloRunningViewModel by viewModels()
    val locationListener: LocationListener = object: LocationListener {
        override fun onLocationChanged(p0: Location) {
            vm.updateLocation(p0)
        }
    }
    lateinit var provider: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = loginManager.currentUser()
        val runId = intent.getLongExtra(RUN_ID_KEY, 0L)
        assert(runId != 0L)
        vm.setRun(Run(user = user!!, id = runId))

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

                    val location = vm.runState.location
                    val run = vm.runState.run
                    val state = run.state
                    val runnerBitmap = remember {
                        getRunnerBitmap(RUNNER_ICON_SIZE)
                    }

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        RunDataPanel(distance = location.distance, kcal = location.kcal, time = run.running, speed = location.speed, modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp))
                        Box(modifier = Modifier.fillMaxSize()) {

                            GoogleMapRun(
                                runStates = listOf(vm.runState),
                                icons = listOf(runnerBitmap),
                                colors = listOf(MediumBlue),
                                mapState = vm.mapState,
                                onCenterSwitch = vm::centerSwitch,
                                modifier = Modifier.fillMaxSize())

                            if (state == Run.State.READY) RunStart(onStart = vm::start)
                            else if (state == Run.State.RUNNING) RunPause(onPause = vm::pause, onFinish = vm::end)
                            else if (state == Run.State.PAUSED) RunResume(onResume = vm::resume, onFinish = vm::end)
                            else {
                                lifecycleScope.launch {
                                    delay(200) //giving time for the server update
                                    this@SoloRunningActivity.finish()
                                    val intent = Intent(this@SoloRunningActivity, SoloRunningResults::class.java )
                                    startActivity(intent)
                                }
                                //activity ended

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
    }
}