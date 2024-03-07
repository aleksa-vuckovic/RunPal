package com.example.runpal.server

import com.example.runpal.models.Event
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * All of the API calls return a GenericResponse object.
 * If successful, 'message' is set to "ok", and 'data' holds the relevant data, if any.
 * (This is what @return refers to.)
 * If unsuccessful, 'message' contains the relevant error message.
 */
interface EventApi {

    /**
     * Create a new event.
     * Name and time must be specified.
     *
     * @return The created event _id.
     */
    @Multipart
    @POST("event/create")
    suspend fun create(@Part("name") name: String,
                       @Part("description") description: String? = null,
                       @Part("time") time: Long,
                       @Part image: MultipartBody.Part?): GenericResponse<String>


    /**
     * Get data for the event with given identifier.
     */
    @GET("event/data/{event}")
    suspend fun data(@Path("event") event: String): GenericResponse<Event>

    /**
     *  Find upcoming events, using the given criteria.
     *
     *  @return A list of matched events, sorted by time ascending.
     */
    @GET("event/find")
    suspend fun find(@Query("search") search: String? = null,
                     @Query("following") following: Boolean? = null
                    ): GenericResponse<List<Event>>

    /**
     * Follow the event.
     */
    @GET("event/follow/:event")
    suspend fun follow(@Path("event") event: String): GenericResponse<Unit>

    /**
     * Unfollow event.
     */
    @GET("event/unfollow/:event")
    suspend fun unfollow(@Path("event") event: String): GenericResponse<Unit>

}