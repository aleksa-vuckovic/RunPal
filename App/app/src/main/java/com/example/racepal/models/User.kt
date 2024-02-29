package com.example.racepal.models

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
class User(
    @PrimaryKey(autoGenerate = false)
    var email: String = "",
    @Ignore
    var password: String? = null,
    var name: String = "",
    var last: String = "",
    var profile: Bitmap? = null,
    var weight: Double = 0.0
) {
}