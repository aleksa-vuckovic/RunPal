package com.example.racepal

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.racepal.activities.MainActivity
import kotlinx.coroutines.delay
import java.io.File
import kotlin.math.max
import kotlin.math.sin


interface Destination {
    val argsRoute: String
    val baseRoute: String
    val icon: ImageVector?
    val label: String?
    val title: String?
}

/**
 * The list should be already sorted in ascending order.
 */
fun<T> binarySearch(data: List<T>, value: (T) -> Float, target: Float): T? {
    var i = 0
    var j = data.size
    if (j == 0) return null
    if (j == 1) return data[0]

    while (j - i > 1) {
        val k = (i + j) / 2
        val valueK = value(data[k])

        if (valueK > target) j = k
        else if (valueK < target) i = k
        else return data[k]
    }

    val valueI = value(data[i])
    val valueJ = value(data[j])
    if (target - valueI <  valueJ - target) return data[i]
    else return data[j]
}

/**
 * @param speed Running speed in m/s.
 * @param slope Slope as a percentage (postive or negative).
 * @param weight Runner's weight in kg.
 *
 * @return kcal per second.
 */
fun kcalExpenditure(speed: Double, slope: Double, weight: Double): Double {
    var factor = 0.2 + 0.9*slope
    if (factor < 0.1) factor = 0.1
    val VO2 = speed * factor / 1000
    val PE = VO2 * weight * 5
    return PE
}
fun waveFloat(mid: Float, range: Float, period: Long, phase: Long, lowerBound: Float, t: Long): Float {
    val sin = sin((t + phase) * 2 * Math.PI.toFloat() / period)
    return max(lowerBound, mid + sin*range)
}

@Composable
fun timeAsState(start: Long = 0L, updateInterval: Long = 100L): State<Long> {
    val state = remember { mutableStateOf(start) }
    LaunchedEffect(key1 = null) {
        while(true) {
            state.value = System.currentTimeMillis() - start
            delay(updateInterval)
        }
    }
    return state
}

fun Context.permanentServerFile(name: String): File {
    val imagesDir = File(filesDir, "server")
    if (!imagesDir.exists()) imagesDir.mkdir()
    return File(imagesDir, name)
}
fun Context.tempServerFile(name: String): File {
    val imagesDir = File(cacheDir, "images")
    if (!imagesDir.exists()) imagesDir.mkdir()
    return File(imagesDir, name)
}

/**
 * If the file is not in the application's permanent files diractory,
 * a copy is placed there, and the corresponding File object is returned.
 */
fun Context.makePermanentFile(profileFile: File): File {
    val permanentFile = this.permanentServerFile(profileFile.name)
    if (!profileFile.equals(permanentFile)) {
        //save the file permanently in the app's files directory
        profileFile.copyTo(permanentFile, true)
    }
    return permanentFile
}

/**
 * Clears all activities on the activity stack and starts MainActivity.
 * (I guess?)
 */
fun Context.restartApp() {
    val intent = Intent(this, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //??
    startActivity(intent)
}