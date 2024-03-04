package com.example.racepal.repositories

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import com.example.racepal.NotFound
import com.example.racepal.ServerException
import com.example.racepal.makePermanentFile
import com.example.racepal.models.User
import com.example.racepal.room.UserDao
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomUserRepository @Inject constructor(private val userDao: UserDao, @ApplicationContext val context: Context): UserRepository {

    private suspend fun saveProfile(user: User) {
        try { //delete previous profile picture
            val previous = getUser(user.email)
            previous.profileUri.toFile().delete()
        } catch (_: Exception) {}

        val profileFile = user.profileUri.toFile()
        val permanentFile = context.makePermanentFile(profileFile)
        user.profileUri = Uri.fromFile(permanentFile)
    }

    /**
     * Update user data in the local Room database.
     * If the profile field URI does not point to the files directory of the application,
     * a copy of the file is placed there, and the inserted entity contains the URI pointing to the new copy.
     */
    override suspend fun update(user: User) {
        saveProfile(user)
        userDao.update(user)
    }

    /**
     * Same as update, but inserts if does not exist yet.
     */
    override suspend fun upsert(user: User) {
        saveProfile(user)
        userDao.upsert(user)
    }


    override suspend fun getUser(email: String): User {
        val result = userDao.get(email)
        if (result == null) throw NotFound("User does not exist.")
        else return result
    }
}