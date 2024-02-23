package com.example.racepal

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
 * @property rate Defines the number n of previous locations to check for.
 */
class PositionFilter(private val rate: Int) {
    private val latFilter = MovingAverageFilter(3)
    private val lngFilter = MovingAverageFilter(3)

    private val buffer = Array<PathPoint>(3){PathPoint()}
    private var i = 0

    fun filter(cur: PathPoint): PathPoint {
        cur.latitude = latFilter.filter(cur.latitude)
        cur.longitude = lngFilter.filter(cur.longitude)

        for (prev in buffer) if (prev.distance(cur) > 20) {
            buffer[i++] = cur
            i %= 3
            return cur
        }
        cur.latitude = buffer[i].latitude
        cur.longitude = buffer[i].longitude
        cur.altitude = buffer[i].altitude
        return cur
    }
}