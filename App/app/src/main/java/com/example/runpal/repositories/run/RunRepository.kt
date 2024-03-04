package com.example.runpal.repositories.run

import com.example.runpal.models.Run
import com.example.runpal.models.RunInfo
import com.example.runpal.models.RunData


/**
 * All methods will throw an exception upon failure.
 */
interface RunRepository {

    /**
     * Creates a run as specified in the argument object.
     * The runId must be generated prior to invoking this method,
     * and it must be unique for the user. (System.currentTimeMillis() should work)
     */
    suspend fun create(run: Run)

    /**
     * Update run, location, and add the given pathPoints to path.
     *
     * @param runData has to contain a Run object with valid 'id' and 'user' fields.
     */
    suspend fun update(runData: RunData)

    /**
     * One of id/room/event must be specified to identify the run.
     * If the run does not exist, an exception will be thrown.
     *
     * @param since The path of the returned RunUpdate will consist of
     * pathPoints later then the specified unix timestamp (time > since).
     */
    suspend fun getUpdate(user: String, id: Long? = null, room: String? = null, event: String? = null,
        since: Long = 0): RunData


    /**
     * Get all all RunInfo objects for each run of the
     * specified user, sorted from newest to oldest.
     */
    suspend fun getRunInfos(user: String): List<RunInfo>
}