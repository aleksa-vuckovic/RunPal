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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.runpal.ImperialDistanceFormatter
import com.example.runpal.ImperialPaceFormatter
import com.example.runpal.ImperialSpeedFormatter
import com.example.runpal.KcalFormatter
import com.example.runpal.MetricDistanceFormatter
import com.example.runpal.MetricPaceFormatter
import com.example.runpal.MetricSpeedFormatter
import com.example.runpal.ProgressFloatingButton
import com.example.runpal.R
import com.example.runpal.TimeFormatter
import com.example.runpal.borderRight
import com.example.runpal.timeAsState
import com.example.runpal.ui.theme.TransparentWhite
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
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
            modifier = Modifier.graphicsLayer {
                this.translationY = subscriptOffset
            })
    }

}

@Composable
fun RunDataPanel(runState: RunState, modifier: Modifier = Modifier) {
    var imperial by rememberSaveable {
        mutableStateOf(false)
    }
    var pace by rememberSaveable {
        mutableStateOf(false)
    }

    Column(modifier = modifier) {
        Row(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()) {
            PanelText(text = if (imperial) ImperialDistanceFormatter.format(runState.location.distance)
            else MetricDistanceFormatter.format(runState.location.distance),
                modifier = Modifier
                    .fillMaxSize()
                    .borderRight(1.dp, Color.LightGray)
                    .weight(1f)
                    .clickable { imperial = !imperial }
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
            PanelText(text = if (pace && imperial) ImperialPaceFormatter.format(runState.location.speed)
            else if (pace && !imperial) MetricPaceFormatter.format(runState.location.speed)
            else if (!pace && imperial) ImperialSpeedFormatter.format(runState.location.speed)
            else MetricSpeedFormatter.format(runState.location.speed),
                modifier = Modifier
                    .fillMaxSize()
                    .borderRight(1.dp, Color.LightGray)
                    .weight(1f)
                    .clickable { pace = !pace }
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
    val context = LocalContext.current
    val beep = remember {
        MediaPlayer.create(context, R.raw.shortbeep)
    }
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