package com.example.racepal.room

import androidx.room.Entity

/**
 * This entity represents a path point within a running session.
 * Corresponds to the PathPoint class, with runId as the only
 * addition.
 */
@Entity(tableName = "path", primaryKeys = ["runId", "time"])
class Path(
    val runId: Long = 0L,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val altitude: Double = 0.0,
    val time: Long = 0L,
    val end: Boolean = false,
    val speed: Double = 0.0,
    val distance: Double = 0.0,
    val kcal: Double = 0.0
) {

}