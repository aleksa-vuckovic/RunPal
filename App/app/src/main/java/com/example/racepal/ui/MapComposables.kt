package com.example.racepal.ui

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.racepal.MapState
import com.example.racepal.PathPoint
import com.example.racepal.ProgressFloatingButton
import com.example.racepal.RunState
import com.example.racepal.toLatLng
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline

@Composable
fun GoogleMapRun(runStates: List<RunState>,
                 icons: List<Bitmap>,
                 colors: List<Color>,
                 mapState: MapState,
                 onCenterSwitch: () -> Unit,
                 modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        GoogleMap(
            modifier = modifier,
            cameraPositionState = mapState.cameraPositionState
        ) {
            for (i in 0..runStates.size-1) {
                Marker(
                    state = MarkerState(position = runStates[i].curAsState().value.toLatLng()),
                    icon = BitmapDescriptorFactory.fromBitmap(icons[i]),
                    anchor = Offset(0.5f, 0.5f)
                )
                GoogleMapPath(pathPoints = runStates[i].pathAsState(), color = colors[i])
            }

        }

        IconButton(
            onClick = onCenterSwitch,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 9.dp, bottom = 100.dp)
                .background(
                    color = Color.White.copy(alpha = 0.6f), shape = CircleShape
                )
        ) {
            Icon(
                imageVector = if (mapState.centeredAsState().value) Icons.Default.CropFree else Icons.Default.CenterFocusStrong,
                contentDescription = "Recenter/Uncenter"
            )
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
        if (pathPoints[i].end || pathPoints[i+1].end) PathEndMarker(latLng = pathPoints[i+1].toLatLng(), color)
        if (!pathPoints[i].end) Polyline(points = listOf(pathPoints[i].toLatLng(), pathPoints[i+1].toLatLng()), color = color, width = 20f, visible = true)
    }
    if (pathPoints.size > 0) PathEndMarker(latLng = pathPoints[0].toLatLng(), color = color)
}

@Composable
fun MapStart(onStart: () -> Unit) {
    var countdown by remember {
        mutableStateOf("")
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = if (countdown == "") Color.Transparent else Color.White.copy(alpha = 0.75f))) {

        if (countdown != "")
            Text(text = countdown,
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.displayLarge)

        ProgressFloatingButton(
            onProgress = {
                if (it == 0f) countdown = ""
                else if (it >= 1f) onStart()
                else countdown = (3.5f - it*3.5f).toInt().toString()
            },
            time = 3500,
            color = Color.Green,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .size(90.dp)
                .align(Alignment.BottomCenter)

        ) {
            Text("Start")
        }
    }
}
@Composable
fun MapPause(onPause: () -> Unit, onFinish: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxSize()
    ) {
        ProgressFloatingButton(
            onProgress = { if (it > 1f) onPause() },
            time = 3500L,
            color = Color.Gray,
            modifier = Modifier
                .padding(start = 10.dp, bottom = 10.dp)
                .size(70.dp)
                .align(Alignment.BottomStart)
        ) {
            Icon(imageVector = Icons.Default.Pause, contentDescription = "Pause", modifier = Modifier.size(40.dp))
        }
        ProgressFloatingButton(
            onProgress = { if (it > 1f) onFinish() },
            time = 3500L,
            color = Color.Red,
            modifier = Modifier
                .padding(start = 90.dp, bottom = 10.dp)
                .size(70.dp)
                .align(Alignment.BottomStart)

        ) {
            Text("Finish")
        }
    }
}
@Composable
fun MapResume(onResume: () -> Unit, onFinish: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White.copy(alpha = 0.75f))
    ) {
        Icon(imageVector = Icons.Filled.Pause,
            contentDescription = "Paused",
            tint = Color.LightGray,
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center))
        ProgressFloatingButton(
            onProgress = { if (it > 1f) onResume() },
            time = 3500L,
            color = Color.Yellow,
            modifier = Modifier
                .padding(start = 10.dp, bottom = 10.dp)
                .size(70.dp)
                .align(Alignment.BottomStart)
        ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Resume", modifier = Modifier.size(40.dp))
        }
        ProgressFloatingButton(
            onProgress = { if (it > 1f) onFinish() },
            time = 3500L,
            color = Color.Red,
            modifier = Modifier
                .padding(start = 90.dp, bottom = 10.dp)
                .size(70.dp)
                .align(Alignment.BottomStart)
        ) {
            Text("Finish")
        }
    }
}