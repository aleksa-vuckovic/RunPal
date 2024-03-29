package com.example.runpal.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.runpal.models.User

@Dao
interface UserDao {

    @Update
    suspend fun update(user: User)

    @Upsert
    suspend fun upsert(user: User)

    @Query("select * from users where email = :email")
    suspend fun get(email: String): User?

}