package com.example.racepal.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.racepal.models.Run


@Dao
interface RunDao {

    @Query("select * from runs where id = :id")
    suspend fun findById(id: Long): Run?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(run: Run): Long
}