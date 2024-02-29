package com.example.racepal.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.racepal.RUNNER_ICON_SIZE
import com.example.racepal.RunDataPanel
import com.example.racepal.run.Timer
import com.example.racepal.getRunnerBitmap
import com.example.racepal.ui.GoogleMapRun
import com.example.racepal.ui.MapPause
import com.example.racepal.ui.MapResume
import com.example.racepal.ui.MapStart
import com.example.racepal.ui.theme.MediumBlue
import com.example.racepal.ui.theme.RacePalTheme
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SoloRunningActivity : ComponentActivity() {

    val vm: SoloRunningViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) this.finish()

        val provider = LocationServices.getFusedLocationProviderClient(this)
        val req = LocationRequest.Builder(200).setMaxUpdateAgeMillis(0).setPriority(
            Priority.PRIORITY_HIGH_ACCURACY).build()
        provider.requestLocationUpdates(req, {vm.updateLocation(it)}, null)

        setContent {
            RacePalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val cur by vm.runState.curAsState()
                    val state by vm.timer.stateAsState()
                    val time by vm.timer.timeAsState()
                    val runnerBitmap = remember {
                        getRunnerBitmap(RUNNER_ICON_SIZE)
                    }

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        RunDataPanel(distance = cur.distance, kcal = cur.kcal, time = time, speed = cur.speed, modifier = Modifier
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

                            if (state == Timer.State.READY) MapStart(onStart = vm::start)
                            else if (state == Timer.State.RUNNING) MapPause(onPause = vm::pause, onFinish = vm::end)
                            else if (state == Timer.State.PAUSED) MapResume(onResume = vm::resume, onFinish = vm::end)
                            else {
                                //activity ended
                                val intent = Intent(this@SoloRunningActivity, SoloRunningResults::class.java )
                                startActivity(intent)
                            }
                        }

                    }
                }
            }
        }
    }
}