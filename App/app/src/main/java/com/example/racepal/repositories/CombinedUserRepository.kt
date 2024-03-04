package com.example.racepal.repositories

import android.content.Context
import android.widget.Toast
import com.example.racepal.ServerException
import com.example.racepal.models.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This class is designed to work with both repositories,
 * preferring the server for retrieval,
 * synchronizing local data when useful,
 * and falling back on the local data in case of server unavailability,
 * BUT only for retrieval of data, whereas updates must be sent to the server first.
 */
@Singleton
class CombinedUserRepository @Inject constructor(
    private val roomUserRepository: RoomUserRepository,
    private val serverUserRepository: ServerUserRespository,
    @ApplicationContext private val context: Context
): UserRepository {
    override suspend fun update(user: User) {
        serverUserRepository.update(user)
        val newUser = serverUserRepository.getUser(user.email)
        roomUserRepository.upsert(newUser)
        //No try block, because this is the only acceptable sequence of events.
    }

    override suspend fun upsert(user: User) {
        throw Exception("Upserts not supported on the server side!")
    }

    //Users whose data has been loaded from the server already (during the lifetime of this singleton)
    private val fresh: MutableList<String> = mutableListOf()

    override suspend fun getUser(email: String): User {
        if (fresh.contains(email)) return roomUserRepository.getUser(email)
        else try {
            val user = serverUserRepository.getUser(email)
            roomUserRepository.upsert(user)
            fresh.add(email)
            return user
        } catch(e: ServerException) {
            e.printStackTrace()
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
        }
        return roomUserRepository.getUser(email)
    }
}