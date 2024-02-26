package com.example.racepal

import android.graphics.Color.alpha
import android.graphics.Color.blue
import android.graphics.Color.green
import android.graphics.Color.red
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
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.core.graphics.ColorUtils
import com.example.racepal.ui.theme.RacePalTheme
import kotlinx.coroutines.delay
import java.lang.Float.min

fun Color.lightness(factor: Float): Color {
    var argb = this.toArgb()
    val alpha = alpha(argb)
    val red = red(argb)
    val green = green(argb)
    val blue = blue(argb)

    val hsl = FloatArray(3)
    ColorUtils.RGBToHSL(red, green, blue, hsl)
    hsl[2] = factor
    argb = ColorUtils.HSLToColor(hsl) or (alpha shl 24)
    return Color(argb)
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
            PanelText(text = if (imperial) ImperialDistanceFormatter.format(distance)
                            else MetricDistanceFormatter.format(distance),
                modifier = Modifier
                    .fillMaxSize()
                    .borderRight(1.dp, Color.LightGray)
                    .weight(1f)
                    .clickable { imperial = !imperial }
                    .padding(vertical = 20.dp)
            )
            PanelText(text = KcalFormatter.format(kcal),
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
            PanelText(text = if (pace && imperial) ImperialPaceFormatter.format(speed)
                            else if (pace && !imperial) MetricPaceFormatter.format(speed)
                            else if (!pace && imperial) ImperialSpeedFormatter.format(speed)
                            else MetricSpeedFormatter.format(speed),
                modifier = Modifier
                    .fillMaxSize()
                    .borderRight(1.dp, Color.LightGray)
                    .weight(1f)
                    .clickable { pace = !pace }
                    .padding(vertical = 20.dp))
            PanelText(text = TimeFormatter.format(time),
                modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(vertical = 20.dp)
            )
        }
    }
}

@Composable
fun ProgressFloatingButton(onProgress: (Float) -> Unit,
                           time: Long,
                           color: Color = Color.Gray,
                           modifier: Modifier = Modifier,
                           content: @Composable () -> Unit) {

    var size by remember {
        mutableStateOf(Size(0f, 0f))
    }
    val diameter = remember(size) {
        min(size.width, size.height)
    }
    val topLeft = remember(size) {
        Offset(size.width/2 - diameter/2, size.height/2 - diameter/2)
    }
    val buttonSize = LocalDensity.current.run {
        (diameter*0.8f).toDp()
    }
    var pressed by remember {
        mutableStateOf(0L)
    }
    var progress by remember {
        mutableStateOf(0f)
    }
    LaunchedEffect(key1 = null) {
        while(true) {
            if (pressed == 0L) progress = 0f
            else progress = (System.currentTimeMillis() - pressed)/time.toFloat()
            onProgress(progress)
            delay(50)
        }
    }
    Box(modifier = modifier
        .onSizeChanged { size = it.toSize() }
        .drawBehind {
            drawArc(
                color = color.lightness(0.8f),
                startAngle = -90f,
                sweepAngle = progress * 360f,
                useCenter = true,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Fill
            )
        }) {
        FloatingActionButton(onClick = {  },
            containerColor = color,
            modifier = Modifier
                .size(buttonSize)
                .align(Alignment.Center)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Main)
                            if (event.type == PointerEventType.Press) pressed = System.currentTimeMillis()
                            else if (event.type == PointerEventType.Release) pressed = 0L
                        }
                    }
                },
            shape = CircleShape) {
            content()
        }
    }

}

//@Preview(showBackground = true)
@Composable
fun Preview() {
    RacePalTheme {
        RunDataPanel(distance = 1240.0, kcal = 120.0, time = 1250000, speed = 3.0, modifier = Modifier
            .fillMaxWidth()
            .height(200.dp))
    }

}