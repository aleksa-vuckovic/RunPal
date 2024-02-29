package com.example.racepal.repositories

import com.example.racepal.models.PathPoint
import com.example.racepal.models.Run
import com.example.racepal.models.RunInfo


/**
 * All methods may throw an IOException.
 */
interface RunRepository {

    /**
     * Creates a run as specified in the argument object.
     */
    suspend fun create(run: Run)
    /**
     * Updates data specified by run.
     * The run object must have a valid existing id.
     */
    suspend fun update(run: Run)

    /**
     * Update the current location (without changing the path).
     *
     * @param run Should contain valid 'id' and 'user' fields.
     * Other fields are NOT used, nor updated.
     */
    suspend fun updateLocation(run: Run, pathPoint: PathPoint)

    /**
     * Update path.
     * @param run Should contain valid 'id' and 'user' fields.
     * Other fileds are NOT used, nor updated.
     */
    suspend fun updatePath(run: Run, pathPoint: PathPoint)

    /**
     * Retrieve path points starting from and not including the timestamp 'since'
     *
     * @param run Should contain a valid pair of identifier fields:
     * id + user, event + user, or room + user.
     * Other fields will not be used.
     * @return All subsequent path points ordered by timestamp.
     */
    suspend fun getPath(run: Run, since: Long): List<PathPoint>

    /**
     * Retrieve the current location of a run.
     * @param run See getPath.
     */
    suspend fun getLocation(run: Run): PathPoint

    /**
     * Get all all RunInfo objects for each run of the
     * specified user, sorted from newest to oldest.
     */
    suspend fun getRunInfos(user: String): List<RunInfo>
}