package com.example.racepal

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.racepal.ui.theme.RacePalTheme

class Smt() {
    fun Int.toSmt(): Smt {
        return Smt()
    }
}

@Composable
fun Modifier.borderRight(strokeWidth: Dp, color: Color): Modifier {
    val density = LocalDensity.current
    val strokeWidthPx = density.run { strokeWidth.toPx() }

    return this.drawBehind {
        val width = size.width - strokeWidthPx/2
        val height = size.height

        drawLine(
            color = color,
            start = Offset(x = width, y = 0f),
            end = Offset(x = width , y = height),
            strokeWidth = strokeWidthPx
        )
    }
}

@Composable
fun PanelText(text: String, modifier: Modifier = Modifier) {
    val tmp = text.split("|")
    val textBig = tmp[0]
    val textSmall = if (tmp.size > 1) tmp[1] else ""
    var subscriptOffset: Float = 0f
    LocalDensity.current.run { subscriptOffset = MaterialTheme.typography.labelLarge.fontSize.toPx() / 2 }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = textBig,
            style = MaterialTheme.typography.labelLarge)
        Text(
            text = textSmall,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
            modifier = Modifier.graphicsLayer {
                this.translationY = subscriptOffset
            })
    }

}

@Composable
fun RunDataPanel(distance: Double, kcal: Double, time: Long, speed: Double, modifier: Modifier = Modifier) {
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
            PanelText(text = if (imperial) Utils.formatDistanceImperial(distance) else Utils.formatDistanceMetric(distance),
                modifier = Modifier
                    .fillMaxSize()
                    .borderRight(1.dp, Color.LightGray)
                    .weight(1f)
                    .clickable { imperial = !imperial }
                    .padding(vertical = 20.dp)
            )
            PanelText(text = "${kcal.toInt()}|kcal", modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(vertical = 20.dp)
            )
        }
        Divider(modifier = Modifier
            .height(1.dp)
            .fillMaxWidth())
        Row(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()) {
            PanelText(text = if (pace) Utils.formatPace(speed) else Utils.formatSpeed(speed),
                modifier = Modifier
                    .fillMaxSize()
                    .borderRight(1.dp, Color.LightGray)
                    .weight(1f)
                    .clickable { pace = !pace }
                    .padding(vertical = 20.dp))
            PanelText(text = Utils.formatTime(time), modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(vertical = 20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    RacePalTheme {
        RunDataPanel(distance = 1240.0, kcal = 120.0, time = 1250000, speed = 3.0, modifier = Modifier
            .fillMaxWidth()
            .height(200.dp))
    }

}