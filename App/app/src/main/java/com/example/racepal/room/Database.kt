package com.example.racepal.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.racepal.models.Run
import com.example.racepal.models.User


@TypeConverters(BitmapConverter::class)
@Database(entities = [User::class, Run::class, Path::class], version = 1, exportSchema = false)
abstract class Database: RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun runDao(): RunDao
    abstract fun pathDao(): PathDao
}