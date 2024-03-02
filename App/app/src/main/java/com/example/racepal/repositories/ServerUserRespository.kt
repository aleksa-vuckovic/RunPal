package com.example.racepal.repositories

import android.content.Context
import android.graphics.BitmapFactory
import com.example.racepal.IntelligibleException
import com.example.racepal.getBitmap
import com.example.racepal.models.User
import com.example.racepal.server.UserApi
import com.example.racepal.toMultipartPart
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerUserRespository @Inject constructor(
    private val userApi: UserApi,
    private val loginManager: LoginManager,
    @ApplicationContext private val context: Context,
    private val serverUploadRepository: ServerUploadRepository
): UserRepository {


    override suspend fun update(user: User) {
        if (user.email != loginManager.currentUser()) throw IntelligibleException("Cannot alter other users' data.")
        val response = userApi.update(user.name, user.last, user.weight.toString(), user.profileUri.getBitmap(context.contentResolver)?.toMultipartPart("profile"))
        if (response.message != "ok") throw IntelligibleException(response.message)
        loginManager.refresh()
    }

    /**
     * Unsupported - The only way to add a new user on the server is through registration.
     */
    override suspend fun upsert(user: User) {
        throw Exception("Upserts not supported on the server side!")
    }

    /**
     * The profile picture will be retrieved, saved in the cache directory,
     * and the profile field will be set to the corresponding URI.
     */
    override suspend fun getUser(email: String): User {
        val response = userApi.data(email)
        if (response.message != "ok") throw IntelligibleException(response.message)
        val user = response.data
        if (user == null) throw IntelligibleException("Server error.") //Never happens

        user.profileUri = serverUploadRepository.get(user.profile)
        return user
    }
}