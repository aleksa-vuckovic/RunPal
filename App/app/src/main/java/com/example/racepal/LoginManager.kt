package com.example.racepal

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import com.example.racepal.models.User
import com.example.racepal.server.LoginApi
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This class manages user JWT tokens,
 * user log in, and log out.
 */

@Singleton
class LoginManager @Inject constructor(val loginApi: LoginApi, @ApplicationContext val context: Context) {

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
     * Throws GenericException if the token was not successfully refreshed.
     */
    suspend fun refresh() {
        val user = currentUser()
        val token = currentToken()

        if (user == null || token == null) throw GenericException("No log history.")

        val response = loginApi.refresh(auth = "Bearer ${token}")
        val body = response.body()
        if (!response.isSuccessful) throw GenericException(body)
        if (body == null) throw GenericException("Server error.")

        setToken(body)
    }

    /**
     * Attempts to register a new user on the server.
     * If successful, the user is immediately logged in.
     * Otherwise, a GenericException is thrown.
     */
    suspend fun register(user: User) {

        val response = loginApi.register(user.email, user.password?:"", user.name, user.last, user.weight.toString(), user.profile?.toMultipartPart())
        val body = response.body()
        if (!response.isSuccessful) throw GenericException(body)
        if (body == null) throw GenericException("Server error.")

        setUser(user.email)
        setToken(body)
    }

    /**
     * Logs the user out by deleting the JWT token.
     */
    fun logOut() {
        setUser(null)
        setToken(null)
    }

    /**
     * Attempts to log in using the given email and password.
     * Throws GenericException if unsuccessful.
     */
    suspend fun login(email: String, password: String) {
        val response = loginApi.login(email, password)
        val body = response.body()
        if (!response.isSuccessful) throw GenericException(body)
        if (body == null) throw GenericException("Server error.")

        setUser(email)
        setToken(body)
    }
}