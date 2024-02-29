package com.example.racepal.models

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
class User(
    @PrimaryKey(autoGenerate = false)
    val email: String = "",
    @Ignore
    val password: String? = null,
    val name: String = "",
    val last: String = "",
    val profile: Bitmap? = null,
    val weight: Double = 0.0
) {
}