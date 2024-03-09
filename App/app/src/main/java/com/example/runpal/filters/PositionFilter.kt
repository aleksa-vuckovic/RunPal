package com.example.runpal.filters

import com.example.runpal.models.PathPoint

/**
 * Even when a device is stationary, the location provided by the GPS satellite will oscillate.
 * These oscillations should not count as running distance.
 * Therefore, this class filters them out.
 * The prinicple is simple - if the location is within 20 meters of the previous n locations,
 * the previous location is returned.
 *
 * On the other hand, since oscillations sometimes, though infrequently, reach up to 100m,
 * additional moving average filters are used.
 *
 * @property avgCount Moving average filter buffer size.
 * @property rate Defines the number n of previous locations to check for.
 * @property radius Defines the minimum distance difference necessary for a position change to be accepted.
 * @property maxUpdateInterval Defines the maximum allowed interval between two consecutive updates.
 */
class PositionFilter(private val avgCount: Int = 10, private val rate: Int = 3, private val radius: Double = 10.0, private val maxUpdateInterval: Long = 3000L) {
    private val latFilter = MovingAverageFilter(avgCount)
    private val lngFilter = MovingAverageFilter(avgCount)
    private val altFilter = MovingAverageFilter(avgCount)

    private val buffer = Array<PathPoint>(rate){ PathPoint() }
    private var i = 0

    fun filter(cur: PathPoint): PathPoint? {
        cur.latitude = latFilter.filter(cur.latitude)
        cur.longitude = lngFilter.filter(cur.longitude)
        cur.altitude = if (cur.altitude == 0.0) buffer[i].altitude else altFilter.filter(cur.altitude)


        var pass = true
        for (prev in buffer) if (prev.distance(cur) < radius) { pass = false; break; }
        if (pass || (cur.time - buffer[i].time > maxUpdateInterval)) {
            buffer[i++] = cur
            i %= rate
            return cur
        }

        return null
    }
}