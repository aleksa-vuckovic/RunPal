package com.example.racepal.repositories

import com.example.racepal.models.PathPoint
import com.example.racepal.models.Run
import com.example.racepal.models.RunInfo
import com.example.racepal.room.PathDao
import com.example.racepal.room.RunDao

class RoomRunRepository(val runDao: RunDao, val pathDao: PathDao): RunRepository {
    override suspend fun create(run: Run) {
        TODO("Not yet implemented")
    }

    override suspend fun update(run: Run) {
        TODO("Not yet implemented")
    }

    override suspend fun updateLocation(run: Run, pathPoint: PathPoint) {
        TODO("Not yet implemented")
    }

    override suspend fun updatePath(run: Run, pathPoint: PathPoint) {
        TODO("Not yet implemented")
    }

    override suspend fun getPath(run: Run, since: Long): List<PathPoint> {
        TODO("Not yet implemented")
    }

    override suspend fun getLocation(run: Run): PathPoint {
        TODO("Not yet implemented")
    }

    override suspend fun getRunInfos(user: String): List<RunInfo> {
        TODO("Not yet implemented")
    }
}