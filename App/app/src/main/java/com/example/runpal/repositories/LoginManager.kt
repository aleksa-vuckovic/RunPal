package com.example.runpal.repositories

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.example.runpal.ServerException
import com.example.runpal.getBitmap
import com.example.runpal.server.LoginApi
import com.example.runpal.toMultipartPart
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This class manages user JWT tokens,
 * user login, and logout.
 */
@Singleton
class LoginManager @Inject constructor(
    private val loginApi: LoginApi,
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val SHARED_PREFS_LOGIN = "LOGIN"
        private const val SHARED_PREFS_LOGIN_EMAIL = "EMAIL"
        private const val SHARED_PREFS_LOGIN_TOKEN = "TOKEN"
        //private const  val SHARED_PREFS_LOGIN_TOKEN_TIMESTAMP = "TOKEN_TIMESTAMP"
        //private const val TOKEN_DURATION = 3*24*60*60*1000L
    }
    /**
     * Returns the email of the currently logged in user
     * or null if none is.
     */
    private fun getPrefs(): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFS_LOGIN, Context.MODE_PRIVATE)
    }
    fun currentUser(): String? {
        val prefs = getPrefs()
        return prefs.getString(SHARED_PREFS_LOGIN_EMAIL, null)
    }
    fun currentToken(): String? {
        val prefs = getPrefs()
        return prefs.getString(SHARED_PREFS_LOGIN_TOKEN, null)
    }

    private fun setUser(email: String?) {
        val prefs = getPrefs()
        prefs.edit().putString(SHARED_PREFS_LOGIN_EMAIL, email).apply()
    }
    private fun setToken(token: String?) {
        val prefs = getPrefs()
        prefs.edit().putString(SHARED_PREFS_LOGIN_TOKEN, token).apply()
    }
    /**
     * Attempts to refresh the jwt token, if it exists.
     * Throws exception if not successful.
     */
    suspend fun refresh() {
        val user = currentUser()
        val token = currentToken()
        if (user == null || token == null) throw Exception("No log history.")

        val response = loginApi.refresh(auth = "Bearer ${token}")
        if (response.message != "ok") throw ServerException(response.message)

        setToken(response.data)
    }

    /**
     * Attempts to register a new user on the server.
     * If successful, the user is immediately logged in.
     * Otherwise an exception is thrown.
     */
    suspend fun register(email: String, password: String, name: String, last: String, weight: Double, profile: Uri?) {
        val response = loginApi.register(email, password, name, last, weight.toString(), profile?.getBitmap(context.contentResolver)?.toMultipartPart(fieldName = "profile", fileName = "profile.png"))
        if (response.message != "ok") throw ServerException(response.message)

        setUser(email)
        setToken(response.data)
    }

    /**
     * Logs the user out by deleting the JWT token.
     */
    fun logout() {
        setUser(null)
        setToken(null)
    }

    /**
     * Attempts to log in using the given email and password.
     * Throws exception if not successful.
     */
    suspend fun login(email: String, password: String) {
        val response = loginApi.login(email, password)
        if (response.message != "ok") throw ServerException(response.message)

        setUser(email)
        setToken(response.data)
    }
}