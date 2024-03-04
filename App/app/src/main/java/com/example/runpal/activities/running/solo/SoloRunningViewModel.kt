package com.example.runpal.activities.running.solo

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runpal.DEFAULT_ZOOM
import com.example.runpal.activities.running.LocalRunState
import com.example.runpal.MapState
import com.example.runpal.activities.running.LocalRunStateFactory
import com.example.runpal.models.Run
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SoloRunningViewModel @Inject constructor(private val localRunStateFactory: LocalRunStateFactory) : ViewModel() {
    //STATE
    lateinit var runState: LocalRunState
    val mapState: MapState = MapState()

    /**
     * Should be called per activity creation, to configure the viewmodel with a specific run id.
     * The view model will restore previous run state, if it exists.
     */
    fun setRun(run: Run) {
        runState = localRunStateFactory.createLocalRunState(run, viewModelScope)
    }

    fun updateLocation(loc: Location) {
        runState.update(loc)
        mapState.adjustCamera(runState.location)
    }
    fun start() = runState.start()
    fun pause() = runState.pause()
    fun resume() = runState.resume()
    fun end() = runState.stop()
    fun centerSwitch() {
        mapState.centerToggle()
        mapState.adjustCamera(runState.location, DEFAULT_ZOOM)
    }

}