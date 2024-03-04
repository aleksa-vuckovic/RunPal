package com.example.runpal.repositories.run

import com.example.runpal.NotFound
import com.example.runpal.models.Run
import com.example.runpal.models.RunInfo
import com.example.runpal.models.RunData
import com.example.runpal.room.PathDao
import com.example.runpal.room.RunDao
import com.example.runpal.room.toPath
import com.example.runpal.room.toPathPoint
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalRunRepository @Inject constructor (val runDao: RunDao, val pathDao: PathDao):
    RunRepository {
    override suspend fun create(run: Run) {
        runDao.insert(run)
    }

    override suspend fun update(runData: RunData) {
        //location is ignored as it is not saved locally and does not need to be
        runDao.update(runData.run)
        for (pathPoint in runData.path) {
            pathDao.insert(pathPoint.toPath(runData.run.user, runData.run.id))
        }
    }

    override suspend fun getUpdate(
        user: String,
        id: Long?,
        room: String?,
        event: String?,
        since: Long
    ): RunData {
        val run: Run?
        if (id != null) run = runDao.findById(user, id)
        else if (room != null) run = runDao.findByRoom(user, room)
        else if (event != null) run = runDao.findByEvent(user, event)
        else throw NotFound("Not enough data to identify run.")
        if (run == null) throw NotFound("Run not found in local database.")

        val path = pathDao.get(run.user, run.id, since).map { it.toPathPoint() }
        val ret = RunData(run = run, path = path)
        return ret
    }

    override suspend fun getRunInfos(user: String): List<RunInfo> {
        TODO("Not yet implemented")
    }

}