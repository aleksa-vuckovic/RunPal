package com.example.racepal

import android.location.Location
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunningViewModel @Inject constructor(val timer: Timer) : ViewModel() {
    val userWeight = 80.0
    //STATE
    val runState: LocalRunState = LocalRunState()
    val mapState: MapState = MapState()

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