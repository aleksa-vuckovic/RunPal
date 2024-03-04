package com.example.racepal.server

import com.example.racepal.models.Run
import com.example.racepal.models.RunData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RunApi {

    @POST("run/create")
    suspend fun create(@Body run: Run): GenericResponse<Unit>
    @POST("run/update")
    suspend fun update(@Body runData: RunData): GenericResponse<Unit>
    @GET("run/getupdate")
    suspend fun getUpdate(@Query("user") user: String, @Query("id") id: Long? = null,
                          @Query("room") room: String? = null, @Query("event") event: String? = null,
                          @Query("since") since: Long = 0) : GenericResponse<RunData>




}