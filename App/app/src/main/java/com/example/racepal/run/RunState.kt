package com.example.racepal.run

import android.location.Location
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.racepal.models.PathPoint
import com.example.racepal.Utils
import com.example.racepal.filters.MovingAverageFilter
import com.example.racepal.filters.PositionFilter
import com.example.racepal.models.toPathPoint

/**
 * Represents the state of an ongoing run, including distance, kcal,
 * the entire path, and only excluding time.
 */
interface RunState {
    val path: List<PathPoint>
    val cur: PathPoint
    fun pathAsState(): List<PathPoint>
    fun curAsState(): State<PathPoint>
}

/**
 * A RunState which filters and manages GPS location data.
 * Used for activities on the host device.
 *
 * @param updateInterval The average interval between two consecutive location updates,
 * used to determine the filter buffer sizes.
 */
class LocalRunState(updateInterval: Int = 200): RunState {
    private val _path: SnapshotStateList<PathPoint> = SnapshotStateList()
    private val _cur: MutableState<PathPoint> = mutableStateOf(PathPoint.NONE)
    private val positionFilter: PositionFilter = PositionFilter(if (updateInterval < 2000) 2000/updateInterval else 1, 3, 10.0, 2000)
    private val speedFilter: MovingAverageFilter = MovingAverageFilter(if (updateInterval < 2000) 2000/updateInterval else 1)

    override val path: List<PathPoint>
        get() = _path
    override val cur: PathPoint
        get() = _cur.value

    override fun pathAsState(): List<PathPoint> = _path
    override fun curAsState(): State<PathPoint> = _cur

    /**
     * @param loc The current location provided by GPS.
     * @param userWeight Used for kcal calculation.
     * @param addToPath Whether the new path point should be added to the path.
     */
    fun update(loc: Location, userWeight: Double, addToPath: Boolean) {
        val cur = positionFilter.filter(loc.toPathPoint())
        if (cur == null) return

        val prev = _cur.value
        if (prev != PathPoint.NONE) {
            cur.distance = prev.distance
            cur.kcal = prev.kcal

            val distanceDifference = prev.distance(cur)
            val timeDifference = (cur.time - prev.time) / 1000
            cur.speed = if (timeDifference != 0L) distanceDifference / timeDifference else prev.speed
            cur.speed = speedFilter.filter(cur.speed)

            if (addToPath) {
                cur.distance += distanceDifference
                val slope = if (distanceDifference != 0.0) (cur.altitude - prev.altitude)/distanceDifference else 0.0
                val expenditure = Utils.kcalExpenditure(cur.speed, slope, userWeight)
                cur.kcal += expenditure*timeDifference
                _path.add(cur)
            }
        }
        _cur.value = cur
    }

    /**
     * When user presses pause, a segment of the path is completed.
     */
    fun endSegment() {
        _cur.value.end = true
    }
}

/**
 * A RunState which is used for data obtained from the server,
 * tracked by another device.
 */
class NonlocalRunState: RunState {
    private val _path: SnapshotStateList<PathPoint> = SnapshotStateList()
    private val _cur: MutableState<PathPoint> = mutableStateOf(PathPoint.NONE)

    override val path: List<PathPoint>
        get() = _path
    override val cur: PathPoint
        get() = _cur.value
    override fun pathAsState(): List<PathPoint> = _path
    override fun curAsState(): State<PathPoint> = _cur

    fun update(pathPoint: PathPoint, addToPath: Boolean) {
        if (addToPath) _path.add(pathPoint)
        _cur.value = pathPoint
    }
}