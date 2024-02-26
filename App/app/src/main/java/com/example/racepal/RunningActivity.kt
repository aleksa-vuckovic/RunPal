package com.example.racepal

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.graphics.get
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.racepal.ui.theme.LightBlue
import com.example.racepal.ui.theme.MediumBlue
import com.example.racepal.ui.theme.RacePalTheme
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.ButtCap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberMarkerState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunningActivity : ComponentActivity() {

    val vm: RunningViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) this.finish()

        val provider = LocationServices.getFusedLocationProviderClient(this)
        val req = LocationRequest.Builder(200).setMaxUpdateAgeMillis(0).setPriority(
            Priority.PRIORITY_HIGH_ACCURACY).build()
        provider.requestLocationUpdates(req, {vm.updateLocation(it)}, null)
        /*provider.requestLocationUpdates(req, object: LocationCallback() {
            override fun onLocationResult(res: LocationResult) {
                val loc = res.lastLocation
                if (loc != null) vm.updateLocation(loc)
            }
        }, null)*/

        setContent {
            RacePalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val cur by vm.runState.curAsState()
                    val state by vm.timer.stateAsState()
                    val time by vm.timer.timeAsState()
                    val runnerBitmap = remember {
                        getRunnerBitmap(RUNNER_ICON_SIZE)
                    }
                    val centered by vm.mapState.centeredAsState()

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        RunDataPanel(distance = cur.distance, kcal = cur.kcal, time = time, speed = cur.speed, modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp))
                        Box(modifier = Modifier.fillMaxSize()) {
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = vm.mapState.cameraPositionState
                            ) {
                                Marker(
                                    state = MarkerState(position = cur.toLatLng()),
                                    icon = BitmapDescriptorFactory.fromBitmap(runnerBitmap),
                                    anchor = Offset(0.5f, 0.5f)
                                )
                                GoogleMapPath(pathPoints = vm.runState.pathAsState(), color = MediumBlue)
                            }

                            IconButton(onClick = { vm.centerSwitch() },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 9.dp, bottom = 100.dp)
                                    .background(
                                        color = Color.White.copy(alpha = 0.6f), shape = CircleShape
                                    )) {
                                Icon(
                                    imageVector = if (centered) Icons.Default.CropFree else Icons.Default.CenterFocusStrong,
                                    contentDescription = "Recenter/Uncenter"
                                )
                            }

                            if (state == Timer.State.READY) ElevatedButton(onClick = {vm.start()},
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 10.dp),
                                colors = ButtonDefaults.elevatedButtonColors(containerColor = Color.Green.copy(alpha = 0.7f))
                            ) {
                                Text("Start")
                            }
                            else if (state == Timer.State.RUNNING) ElevatedButton(onClick = {vm.pause()},
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 10.dp),
                                colors = ButtonDefaults.elevatedButtonColors(containerColor = Color.Gray.copy(alpha = 0.7f), contentColor = Color.White)
                            ) {
                                Text("Pause")
                            }
                            else if (state == Timer.State.PAUSED) ElevatedButton(onClick = {vm.resume()},
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 10.dp),
                                colors = ButtonDefaults.elevatedButtonColors(containerColor = Color.Yellow.copy(alpha = 0.7f))
                            ) {
                                Text("Resume")
                            }
                            else {
                                //activity ended
                            }
                        }

                    }


                }
            }
        }
    }
}

@Composable
fun PathEndMarker(latLng: LatLng, color: Color) {
    Circle(center = latLng,
        clickable = false,
        fillColor = color.copy(alpha = 0.7f),
        strokeColor = color,
        radius = 20.0,
    )
}

@Composable
fun GoogleMapPath(pathPoints: List<PathPoint>, color: Color) {
    for (i in 0..pathPoints.size-2) {
        if (pathPoints[i].end || pathPoints[i+1].end)
            PathEndMarker(latLng = pathPoints[i+1].toLatLng(), color)
        if (pathPoints[i].end) Polyline(points = listOf(pathPoints[i].toLatLng(), pathPoints[i+1].toLatLng()), color = color, width = 20f, visible = true)
    }
    if (pathPoints.size > 0) PathEndMarker(latLng = pathPoints[0].toLatLng(), color = color)
}