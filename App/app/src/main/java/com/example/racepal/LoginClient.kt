package com.example.racepal

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface LoginClient {
    @GET("test")
    suspend fun test(@Query("test") testData: String): ResponseBody
}