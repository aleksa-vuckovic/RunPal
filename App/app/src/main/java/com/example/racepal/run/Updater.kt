package com.example.racepal.run

import com.example.racepal.models.PathPoint
import com.example.racepal.room.PathDao
import com.example.racepal.room.RunDao

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

/**
 * Passes run updates to the local room database.
 *
 * Ne data caching is necessary, since room
 * is always expected to respond.
 */
class RoomUpdater(val runId: Long, val room: String?, val event: String?, val runDao: RunDao, val pathDao: PathDao): Updater {

    //mutual exclusion and order?
    override suspend fun start(startTime: Long) {
        //Checking if the record exists already
        var run = runDao.findById(runId)
        if (run == null) {
            //insert
            run = Run(
                id = runId,
                room = room,
                event = event
            )
            runDao.insert(run)
        }
    }

    override fun updateCur(cur: PathPoint) {
        TODO("Not yet implemented")
    }

    override fun updatePath(pathPoint: PathPoint) {
        TODO("Not yet implemented")
    }

    override fun updateRunningTime(time: Long) {
        TODO("Not yet implemented")
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun end(endTime: Long) {
        TODO("Not yet implemented")
    }


}