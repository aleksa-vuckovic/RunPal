package com.example.racepal.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.racepal.models.Run


@Dao
interface RunDao {

    @Query("select * from runs where user = :user and id = :id")
    suspend fun findById(user: String, id: Long): Run?
    @Query("select * from runs where user = :user and room = :room")
    suspend fun findByRoom(user: String, room: String): Run?
    @Query("select * from runs where user = :user and event = :event")
    suspend fun findByEvent(user: String, event: String): Run


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(run: Run)

    @Update
    suspend fun update(run: Run)

    @Query("delete from runs")
    suspend fun deleteAll()

}