package com.example.racepal

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.FloatAnimationSpec
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VectorizedAnimationSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.get
import androidx.core.graphics.set
import com.example.racepal.ui.theme.LightBlue
import com.example.racepal.ui.theme.MediumBlue
import com.example.racepal.ui.theme.Pink
import com.example.racepal.ui.theme.RacePalTheme
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
            if (granted.size != 2) this@MainActivity.finish()
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))

        super.onCreate(savedInstanceState)
        setContent {
            RacePalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val datasets = remember {
                        listOf(
                            PathLineChartDataset(
                                path = path2,
                                xValue = {it.time.toDouble()},
                                yValue = {it.altitude}
                            ),
                            PathLineChartDataset(
                                path = path3,
                                xValue = {it.time.toDouble()},
                                yValue = {it.altitude}
                            ),
                        )
                    }
                    val labelStyle = MaterialTheme.typography.labelSmall
                    val options = remember {
                        listOf(
                            PathLineChartOptions(
                                color = MediumBlue,
                                shade = true,
                                width = 15f,
                                markers = true,
                                markerLabel = AltitudeAxisValueFormatter(labelStyle)
                            ),
                            PathLineChartOptions(
                                color = Pink,
                                shade = true,
                                width = 10f,
                                markers = false,
                                markerLabel = null
                            )
                        )
                    }
                    val axes = remember {
                        AxesOptions(
                            xLabel = NoLabelAxisValueFormatter(),
                            yLabel = AltitudeAxisValueFormatter(labelStyle),
                            xTickCount = 10,
                            yTickCount = 5
                        )
                    }

                    Column(modifier = Modifier.fillMaxSize()) {

                        PathChart(
                            datasets = datasets,
                            options = options,
                            axes = axes,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(500.dp)
                        )

                        /*
                        Button(onClick = {
                            val intent = Intent(this@MainActivity, RunningActivity::class.java)
                            startActivity(intent)
                        }) {
                            Text(text = "GO")
                        }
                        */


                    }
                }
            }
        }
    }
}

val time = System.currentTimeMillis()
val inc = 10000L

val path = listOf(  PathPoint(altitude = 100.0, time = time, speed =  5.0, distance = 0.0, kcal = 0.0, end = false),
    PathPoint(altitude = 101.0, time = time + inc , speed =  5.0, distance = 0.0, kcal = 0.0, end = false),
    PathPoint(altitude = 102.0, time = time + 2*inc, speed =  5.0, distance = 0.0, kcal = 0.0, end = false),
    PathPoint(altitude = 103.0, time = time + 3*inc, speed =  5.0, distance = 0.0, kcal = 0.0, end = false),
    PathPoint(altitude = 105.0, time = time + 4*inc, speed =  5.0, distance = 0.0, kcal = 0.0, end = true),
    PathPoint(altitude = 105.0, time = time + 5*inc, speed =  5.0, distance = 0.0, kcal = 0.0, end = false),
    PathPoint(altitude = 105.0, time = time + 6*inc, speed =  5.0, distance = 0.0, kcal = 0.0, end = false),
    PathPoint(altitude = 104.0, time = time + 7*inc, speed =  5.0, distance = 0.0, kcal = 0.0, end = false),
    PathPoint(altitude = 103.0, time = time + 8*inc, speed =  5.0, distance = 0.0, kcal = 0.0, end = true))

val path2 = List<PathPoint>(100) {
    PathPoint(altitude = 100.0 + Math.sin(it.toDouble()/10)*5, time = time + it*inc, end = false)
}
val path3 = List<PathPoint>(100) {
    PathPoint(altitude = 100.0 + Math.sin(it.toDouble()/10 + 1)*5, time = time + it*inc, end = false)
}


@Composable
fun myAnimateFloat(newgoal: Float): State<Float> {
    val state = remember {
        mutableStateOf(newgoal)
    }
    val goal = remember {
        mutableStateOf(newgoal)
    }
    goal.value = newgoal

    LaunchedEffect(key1 = null) {
        while(true) {
            delay(1000)
            state.value += (goal.value-state.value)/2
        }
    }
    return state
}

