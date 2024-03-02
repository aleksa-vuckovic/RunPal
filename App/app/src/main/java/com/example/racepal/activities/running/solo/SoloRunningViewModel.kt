package com.example.racepal.activities.running.solo

import android.location.Location
import androidx.lifecycle.ViewModel
import com.example.racepal.run.LocalRunState
import com.example.racepal.MapState
import com.example.racepal.run.Timer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SoloRunningViewModel @Inject constructor(val timer: Timer) : ViewModel() {
    val userWeight = 80.0
    //STATE
    val runState: LocalRunState = LocalRunState()
    val mapState: MapState = MapState()

    init {
        timer.reset()
    }

    fun updateLocation(loc: Location) {
        runState.update(loc, userWeight, timer.state == Timer.State.RUNNING)
        mapState.adjustCamera(runState.cur)
    }
    fun start() {
        timer.start()
    }
    fun pause() {
        timer.pause()
        runState.endSegment()
    }
    fun resume() {
        timer.resume()
    }
    fun end() {
        timer.stop()
        runState.endSegment()
    }
    fun centerSwitch() {
        mapState.centerToggle()
        mapState.adjustCamera(runState.cur, 15f)
    }

}