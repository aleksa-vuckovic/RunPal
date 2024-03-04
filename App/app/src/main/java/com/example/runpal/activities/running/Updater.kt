package com.example.runpal.activities.running

import com.example.runpal.models.PathPoint

/**
 * An updater takes note of run updates, and
 * passes them to a repository.
 *
 * The updater is not expected to keep track
 * of integrity and correctness of the data,
 * but it is expected to transfer the data
 * as soon as possibly, with a delay only
 * if necessary (when there is no internet
 * connection for example).
 *
 * The updater should be constructed with
 * a specific record identifier, and
 * is expected to initialize it if necessary,
 * or retrieve the existing data if necessary.
 */
interface Updater {

    /**
     * Should update the start time of the run.
     */
    suspend fun start(startTime: Long)

    /**
     * Should update the current location.
     */
    suspend fun updateCur(cur: PathPoint)

    /**
     * Should add the pathPoint to the path.
     */
    suspend fun updatePath(pathPoint: PathPoint)

    /**
     * Should update the running time
     */
    suspend fun updateRunningTime(time: Long)

    /**
     * Should mark the last path segment as end.
     */
    suspend fun pause()

    /**
     * Should update the endTime
     */
    suspend fun end(endTime: Long)
}