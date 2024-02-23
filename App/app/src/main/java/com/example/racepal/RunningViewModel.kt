package com.example.racepal

import android.location.Location
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RunningViewModel : ViewModel() {
    val userWeight = 80.0
    //STATE
    private val _started = MutableStateFlow(false)
    private val _paused = MutableStateFlow(false)
    private val _ended = MutableStateFlow(false)
    private val _centered = MutableStateFlow(false)
    private val _cur = MutableStateFlow(PathPoint())
    private val _startTime = MutableStateFlow(0L)
    private val _runTime = MutableStateFlow(0L)

    /**
     * List of path points representing the runner's path.
     * When paused, the last path point will be marked as "end".
     */
    val path = SnapshotStateList<PathPoint>()
    val cameraPositionState = CameraPositionState()

    val started = _started.asStateFlow()
    val paused = _paused.asStateFlow()
    val ended = _ended.asStateFlow()
    val centered = _centered.asStateFlow()
    val cur = _cur.asStateFlow()
    val startTime = _startTime.asStateFlow()
    val runTime = _runTime.asStateFlow()

    //Filters
    val altFilter: MovingAverageFilter = MovingAverageFilter(10)
    val speedFilter: MovingAverageFilter = MovingAverageFilter(3)
    val positionFilter: PositionFilter = PositionFilter(3)

    init {
        viewModelScope.launch {
            var prev = System.currentTimeMillis()
            while(true) {
                val cur = System.currentTimeMillis()
                if (_running()) _runTime.update { it + cur - prev }
                prev = cur
                delay(500)
            }
        }
    }

    private fun _running(): Boolean = _started.value && !_paused.value && !_ended.value
    private fun adjustLocation(zoom: Float? = null) {
        if (_centered.value)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(_cur.value.toLatLng(), if (zoom == null) cameraPositionState.position.zoom else zoom)
    }
    fun updateLocation(loc: Location) {
        val cur = positionFilter.filter(loc.toPathPoint())
        val prev = _cur.value
        cur.altitude = altFilter.filter(cur.altitude)
        cur.distance = prev.distance
        cur.kcal = prev.kcal

        val distanceDifference = prev.distance(cur)
        val timeDifference = (cur.time - prev.time) / 1000
        cur.speed = if (timeDifference != 0L) distanceDifference / timeDifference else 0.0
        cur.speed = speedFilter.filter(cur.speed)

        if (_running()) {
            cur.distance += distanceDifference
            val slope = if (distanceDifference != 0.0) (cur.altitude - prev.altitude)/distanceDifference else 0.0
            val expenditure = kcalExpenditure(cur.speed, slope, userWeight)
            cur.kcal += expenditure*timeDifference
            path.add(cur)
        }
        _cur.update { cur }
        adjustLocation()
    }
    fun start() {
        _startTime.update { System.currentTimeMillis() }
       _started.update { true }
    }
    fun pause() {
        _paused.update { true }
        val lastPathPoint = path.lastOrNull()
        if (lastPathPoint != null) lastPathPoint.end = true
    }
    fun resume() {
        _paused.update { false }
    }
    fun end() {
        _ended.update{true}
    }
    fun centerSwitch() {
        _centered.update { !it }
        if (_centered.value) adjustLocation(15f)
    }

}


/**
 * @param speed Running speed in m/s.
 * @param slope Slope as a percentage (postive or negative).
 * @param weight Runner's weight in kg.
 *
 * @return kcal per second.
 */
fun kcalExpenditure(speed: Double, slope: Double, weight: Double): Double {
    var factor = 0.2 + 0.9*slope
    if (factor < 0.1) factor = 0.1
    val VO2 = speed * factor / 1000
    val PE = VO2 * weight * 5
    return PE
}