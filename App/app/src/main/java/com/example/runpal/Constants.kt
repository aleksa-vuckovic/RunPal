package com.example.runpal

import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.example.runpal.ui.theme.DarkBlue
import com.example.runpal.ui.theme.DarkPink
import com.example.runpal.ui.theme.DarkPurple
import com.example.runpal.ui.theme.DarkYellow
import com.example.runpal.ui.theme.YellowGreen

val DEFAULT_PROFILE_URI = Uri.parse("android.resource://com.example.runpal/" + R.drawable.default_prof)
val RUN_MARKER_COLORS: List<Color> = listOf(DarkBlue, DarkPurple, DarkPink, DarkYellow, YellowGreen)
const val KM_TO_MILE: Double = 0.621371
const val MILE_TO_FT: Double = 5280.0
const val LB_TO_KG: Double = 0.45359237
const val KM_TO_M: Double = 1000.0
const val RUN_MARKER_SIZE: Int = 100
const val DEFAULT_ZOOM: Float = 15f
const val RUN_ID_KEY: String = "RUN_ID"
const val ROOM_ID_KEY: String = "ROOM_ID"
const val EVENT_ID_KEY: String = "EVENT_ID"
//const val SERVER_ADDRESS = "https://run-pal-server-f256155400de.herokuapp.com/"
//const val SERVER_ADDRESS = "http://127.0.0.1:4000/"
//const val SERVER_ADDRESS = "http://10.174.9.184:4000/"
const val SERVER_ADDRESS = "http://192.168.0.7:4000/"