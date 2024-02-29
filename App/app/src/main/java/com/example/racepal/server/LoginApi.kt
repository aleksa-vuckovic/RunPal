package com.example.racepal.server

import com.example.racepal.models.User
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface LoginApi {

    @GET("test")
    suspend fun test(@Query("test") testData: String): ResponseBody

    /**
     * Login using email and password.
     *
     * @return On success, the JWT token as plain text,
     * on failure the error message as plain text.
     */
    @FormUrlEncoded
    @POST("login")
    suspend fun login(@Field("email") email: String, @Field("password") password: String): Response<String>

    /**
     * Refresh a valid JWT token.
     *
     * @return On success the JWT token as plain text,
     * on failure the error message as plain text.
     */
    @GET("refresh")
    suspend fun refresh(@Header("Authorization") auth: String = "Bearer "): Response<String>

    /**
     * Submits the user data for registration.
     *
     * @return On success a new JWT token as plain text,
     * on failure the error message as plain text.
     */
    @Multipart
    @POST("register")
    suspend fun register(@Part("email") email: String,
                         @Part("password") password: String,
                         @Part("name") name: String,
                         @Part("last") last: String,
                         @Part("weight") weight: String,
                         @Part("profile") profile: MultipartBody.Part?): Response<String>
}

