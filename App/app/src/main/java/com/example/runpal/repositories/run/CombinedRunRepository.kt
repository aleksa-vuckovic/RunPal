package com.example.runpal.repositories.run

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.runpal.NotFound
import com.example.runpal.R
import com.example.runpal.ServerException
import com.example.runpal.models.Run
import com.example.runpal.models.RunData
import com.example.runpal.repositories.LoginManager
import com.example.runpal.room.Sync
import com.example.runpal.room.SyncDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This class is designed to work with both repositories,
 * preferring one or the other depending on context, and synchronizing data when useful.
 */
@Singleton
class CombinedRunRepository @Inject constructor(
    private val localRunRepository: LocalRunRepository,
    private val serverRunRepository: ServerRunRepository,
    private val loginManager: LoginManager,
    private val syncDao: SyncDao,
    @ApplicationContext private val context: Context
): RunRepository {

    private val unsynced: MutableSet<Pair<String, Long>> = mutableSetOf()
    init {
        CoroutineScope(Dispatchers.Main).launch {
            for (sync in syncDao.getAll()) unsynced.add(sync.user to sync.runId)
        }
    }
    private suspend fun unsyncedCreate(run: Run) {
        if (!isSynced(run)) return
        unsynced.add(run.user to run.id)
        syncDao.insert(Sync(user = run.user, runId = run.id, since = null))
    }
    private suspend fun unsyncedUpdate(runData: RunData) {
        val run = runData.run
        if (!isSynced(run)) return
        unsynced.add(run.user to run.id)
        val since = if (runData.path.size > 0) runData.path[0].time - 1 else System.currentTimeMillis()
        syncDao.insert(Sync(user = run.user, runId = run.id, since = since))
    }
    private fun isSynced(run: Run) = !unsynced.contains(run.user to run.id)
    private suspend fun attemptSync(run: Run) {
        val s = syncDao.get(run.user, run.id)
        if (s == null) {
            unsynced.remove(run.user to run.id)
            return
        }
        val update = localRunRepository.getUpdate(user = run.user, id = run.id, since = s.since ?: 0)
        try {
            if (s.since == null) {
                serverRunRepository.create(update.run)
                s.since = 0L
                syncDao.update(s)
            }
            serverRunRepository.update(update)
            syncDao.delete(s)
            unsynced.remove(run.user to run.id)
        } catch(e: ServerException) {
            //The server has received the request and rejected it
            syncDao.delete(s)
            unsynced.remove(run.user to run.id)
        } catch(e: Exception) {
            //The data remains to be synced
        }
    }
    suspend fun attemptSyncAll() {
        for (item in syncDao.getAll()) attemptSync(Run(user = item.user, id = item.runId ))
    }







    override suspend fun create(run: Run) {
        localRunRepository.create(run)
        if (isSynced(run)) try {
            serverRunRepository.create(run)
        } catch(_: ServerException) {/*-||-*/}
        catch(e: Exception) {
            unsyncedCreate(run)
        }
        else attemptSync(run)
    }
    override suspend fun update(runData: RunData) {
        localRunRepository.update(runData)
        val run = runData.run
        if (isSynced(run)) try {
            serverRunRepository.update(runData)
        } catch (_: ServerException) {/*-||-*/}
        catch(e: Exception) {
            unsyncedUpdate(runData)
        }
        else attemptSync(run)
    }

    override suspend fun getUpdate(
        user: String,
        id: Long?,
        room: String?,
        event: String?,
        since: Long
    ): RunData {
        if (loginManager.currentUser() == user) {
            try {
                return localRunRepository.getUpdate(user, id, room, event, since)
            } catch (e: NotFound) {
                Log.d("NOT FOUND", "Run not found in local rep. Serching server.")
                //The run must be from a different device, so synchronize.
                try {
                    val ret = serverRunRepository.getUpdate(user, id, room, event, since)
                    Log.d("FOUND", "Found run in server rep. distance = " + ret.location.distance.toString())
                    localRunRepository.create(ret.run)
                    localRunRepository.update(ret)
                    return ret
                } catch (e: ServerException) {
                    e.printStackTrace()
                    throw e
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, context.getString(R.string.no_internet_message), Toast.LENGTH_SHORT).show()
                    throw e
                }
            }
        }
        else {
            try {
                return serverRunRepository.getUpdate(user, id, room, event, since)
            } catch (e: ServerException) {
                e.printStackTrace()
                //Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, context.getString(R.string.no_internet_message), Toast.LENGTH_SHORT).show()
                throw e
            }
        }

    }

    override suspend fun getRuns(until: Long, limit: Int): List<RunData> {
        try {
            return serverRunRepository.getRuns(until, limit)
        } catch(e: ServerException) {
            e.printStackTrace()
        }
        catch(e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, context.getString(R.string.no_internet_message), Toast.LENGTH_SHORT).show()
        }
        return localRunRepository.getRuns(until, limit)
    }

    override suspend fun getRunsSince(since: Long): List<RunData> {
        try {
            return serverRunRepository.getRunsSince(since)
        } catch(e: ServerException) {
            e.printStackTrace()
        }
        catch(e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, context.getString(R.string.no_internet_message), Toast.LENGTH_SHORT).show()
        }
        return localRunRepository.getRunsSince(since)
    }

    override suspend fun delete(runId: Long) {
        try {
            serverRunRepository.delete(runId)
        } catch(_: Exception) {}
        localRunRepository.delete(runId)
    }

    override suspend fun unfinished(): RunData? {
        var ret: RunData? = null
        try {
            ret = serverRunRepository.unfinished()
        } catch(_: Exception) {}
        if (ret != null) return ret
        return localRunRepository.unfinished()
    }
}