package com.example.runpal.repositories.run

import com.example.runpal.ServerException
import com.example.runpal.models.Run
import com.example.runpal.models.RunInfo
import com.example.runpal.models.RunData
import com.example.runpal.server.RunApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerRunRepository @Inject constructor(private val runApi: RunApi): RunRepository {
    override suspend fun create(run: Run) {
        val ret = runApi.create(run)
        if (ret.message != "ok") throw ServerException(ret.message)
    }

    override suspend fun update(runData: RunData) {
        val ret = runApi.update(runData)
        if (ret.message != "ok") throw ServerException(ret.message)
    }

    override suspend fun getUpdate(
        user: String,
        id: Long?,
        room: String?,
        event: String?,
        since: Long
    ): RunData {
        val ret = runApi.getUpdate(user = user, id = id, room = room, event = event, since = since)
        if (ret.message != "ok") throw ServerException(ret.message)
        else return ret.data!!
    }

    override suspend fun getRunInfos(user: String): List<RunInfo> {
        TODO("Not yet implemented")
    }

}