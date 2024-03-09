package com.example.runpal.activities.running

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.runpal.GoogleMapPath
import com.example.runpal.KcalFormatter
import com.example.runpal.ProgressFloatingButton
import com.example.runpal.R
import com.example.runpal.TimeFormatter
import com.example.runpal.Units
import com.example.runpal.borderRight
import com.example.runpal.models.PathPoint
import com.example.runpal.models.toLatLng
import com.example.runpal.ui.theme.TransparentWhite
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.delay


@Composable
fun PanelText(text: Pair<String,String>, modifier: Modifier = Modifier) {
    var subscriptOffset: Float
    LocalDensity.current.run { subscriptOffset = MaterialTheme.typography.labelLarge.fontSize.toPx() / 2 }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = text.first,
            style = MaterialTheme.typography.labelLarge)
        Text(
            text = text.second,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.graphicsLayer {
                this.translationY = subscriptOffset
            })
    }
}

@Composable
fun RunDataPanel(runState: RunState,
                 units: Units,
                 onChangeUnits: () -> Unit,
                 pace: Boolean,
                 onChangePace: () -> Unit,
                 modifier: Modifier = Modifier) {


    Column(modifier = modifier) {
        Row(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()) {
            PanelText(text = units.distanceFormatter.format(runState.location.distance),
                modifier = Modifier
                    .fillMaxSize()
                    .borderRight(1.dp, Color.LightGray)
                    .weight(1f)
                    .clickable { onChangeUnits() }
                    .padding(vertical = 20.dp)
            )
            PanelText(text = KcalFormatter.format(runState.location.kcal),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(vertical = 20.dp)
            )
        }
        Divider(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
        )
        Row(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()) {
            PanelText(text = if (pace) units.paceFormatter.format(runState.location.speed)
                        else units.speedFormatter.format(runState.location.speed),
                modifier = Modifier
                    .fillMaxSize()
                    .borderRight(1.dp, Color.LightGray)
                    .weight(1f)
                    .clickable { onChangePace() }
                    .padding(vertical = 20.dp))
            PanelText(text = TimeFormatter.format(runState.run.running),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(vertical = 20.dp)
            )
        }
    }
}

@Composable
fun RunStart(onStart: () -> Unit, sound: MediaPlayer? = null) {
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
                else {
                    val prev = countdown
                    countdown = (4f - it*4f).toInt().toString()
                    if (countdown != prev && prev != "") sound?.start()
                }
            },
            time = 4000L,
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
fun RunPause(onPause: () -> Unit, onFinish: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxSize()
    ) {
        ProgressFloatingButton(
            onProgress = { if (it > 1f) onPause() },
            time = 2000L,
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
            time = 2000L,
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
fun RunResume(onResume: () -> Unit, onFinish: () -> Unit) {
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
            time = 2000L,
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
            time = 2000L,
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
fun RunCountown(till: Long, onStart: () -> Unit, sound: MediaPlayer? = null) {
    var countdown by rememberSaveable {
        mutableStateOf("")
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = TransparentWhite)) {
        Text(text = countdown,
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.displayLarge)
    }
    LaunchedEffect(key1 = till) {
        while(true) {
            val left = till - System.currentTimeMillis()
            if (left <= 0) onStart()
            else {
                val prev = countdown
                countdown = (left/1000L).toString()
                if (countdown != prev && prev != "") sound?.start()
            }
            delay(200)
        }
    }
}

@Composable
fun GoogleMapRun(runStates: List<RunState>,
                 markers: List<BitmapDescriptor>,
                 colors: List<Color>,
                 mapState: MapState,
                 onCenterSwitch: () -> Unit,
                 modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = mapState.cameraPositionState
        ) {
            for (i in runStates.indices) {
                GoogleMapPath(pathPoints = runStates[i].path, color = colors[i])
                val loc = runStates[i].location
                if (loc != PathPoint.NONE) Marker(
                    state = MarkerState(position = loc.toLatLng()),
                    icon = markers[i],
                    anchor = Offset(0.5f, 0.5f)
                )
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
                imageVector = if (mapState.centered) Icons.Default.CropFree else Icons.Default.CenterFocusStrong,
                contentDescription = "Recenter/Uncenter"
            )
        }
    }
}
