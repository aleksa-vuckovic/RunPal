package com.example.runpal

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.runpal.models.PathPoint
import com.example.runpal.models.toLatLng
import com.example.runpal.ui.theme.DarkPink
import com.example.runpal.ui.theme.DarkPurple
import com.example.runpal.ui.theme.StandardTextField
import com.example.runpal.ui.theme.TransparentWhite
import com.example.runpal.ui.theme.YellowGreen
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import kotlinx.coroutines.delay
import java.lang.Float.min
import kotlin.math.sqrt


@Composable
fun Modifier.borderRight(strokeWidth: Dp = 1.dp, color: Color = Color.Black): Modifier {
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
fun Modifier.borderBottom(strokeWidth: Dp = 1.dp, color: Color = Color.Black): Modifier {
    val density = LocalDensity.current
    val strokeWidthPx = density.run { strokeWidth.toPx() }

    return this.drawBehind {
        val width = size.width - strokeWidthPx/2
        val height = size.height

        drawLine(
            color = color,
            start = Offset(x = 0f, y = height),
            end = Offset(x = width , y = height),
            strokeWidth = strokeWidthPx
        )
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
    val center = remember(size) {
        Offset(size.width/2, size.height/2)
    }
    val topLeft = remember(size) {
        Offset(center.x - diameter/2, center.y - diameter/2)
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
                color = color,
                startAngle = -90f,
                sweepAngle = progress * 360f,
                useCenter = true,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Fill
            )
            drawCircle(
                color = color,
                center = center,
                radius = diameter / 2,
                style = Stroke(width = 4f)
            )
        }) {
        FloatingActionButton(onClick = {  },
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .size(buttonSize)
                .align(Alignment.Center)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Main)
                            if (event.type == PointerEventType.Press) pressed =
                                System.currentTimeMillis()
                            else if (event.type == PointerEventType.Release) pressed = 0L
                        }
                    }
                },
            shape = CircleShape) {
            content()
        }
    }

}

@Composable
fun DoubleInput(initial: Double, onChange: (Double) -> Unit, modifier: Modifier = Modifier) {
    var input by rememberSaveable {
        mutableStateOf("%.2f".format(initial))
    }
    StandardTextField(value = input, onChange = {
        val v = it.toDoubleOrNull()
        if (v != null) {
            input = it
            onChange(v)
        }
        else if (it == "") {
            input = it
            onChange(0.0)
        }
    }, modifier = modifier)
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ImageSelector(input: Uri?, onSelect: (Uri?) -> Unit, modifier: Modifier = Modifier) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri?.let {
            onSelect(it)
        }
    }

    Box(
        modifier = modifier
            .clickable {
                launcher.launch("image/*")
            },
        contentAlignment = Alignment.Center
    ) {
        if (input != null) {
            Image(
                painter = rememberImagePainter(data = input),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Icon(
                imageVector = Icons.Default.ImageSearch,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun LoadingDots(size: Dp, count: Int, color: Color = MaterialTheme.colorScheme.primary, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(size * 2 / 3),
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
    ) {
        val time = timeAsState(start = System.currentTimeMillis())
        for (i in 0..count - 1)
            Box(
                modifier = Modifier
                    .padding(
                        bottom = waveFloat(
                            0f,
                            size.value * 4 / 3,
                            3000L,
                            -i * 200L,
                            size.value / 3,
                            time.value
                        ).dp
                    )
                    .size(size)
                    .background(
                        color = color,
                        shape = CircleShape
                    )
            )
    }
}

@Composable
fun LoadingScreen(dotSize: Dp = 30.dp) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = TransparentWhite),
        contentAlignment = Alignment.Center
    ) {
        LoadingDots(size = dotSize, count = 3)
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = TransparentWhite),
        contentAlignment = Alignment.Center) {
        Text(text = message, style = MaterialTheme.typography.displaySmall, textAlign = TextAlign.Center)
    }
}

@Composable
fun PathEndMarker(latLng: LatLng, color: Color) {
    val desc = remember(color) {
        val res =
            if (color == DarkPink) R.drawable.darkpink
            else if (color == DarkPurple) R.drawable.darkpurple
            else if (color == Color.Green) R.drawable.green
            else if (color == Color.Red) R.drawable.red
            else if (color == YellowGreen) R.drawable.yellowgreen
            else R.drawable.darkblue
        BitmapDescriptorFactory.fromResource(res)
    }
    Marker(
        state = MarkerState(position = latLng),
        icon = desc,
        anchor = Offset(0.5f, 0.5f)
    )
}


fun List<PathPoint>.toSegments(): List<List<LatLng>> {
    val res: MutableList<List<LatLng>> = mutableListOf()
    var cur: MutableList<LatLng> = mutableListOf()
    for (p in this) {
        cur.add(p.toLatLng())
        if (p.end) {
            res.add(cur)
            cur = mutableListOf()
        }
    }
    if (!cur.isEmpty()) res.add(cur)
    return res
}
@Composable
fun GoogleMapPath(pathPoints: List<PathPoint>, startColor: Color = Color.Green, color: Color, endColor: Color = Color.Red) {
    /*
    val skip: Int = pathPoints.size / max
    var prev: PathPoint? = null
    for (i in 0..pathPoints.size-2) {
        if (pathPoints[i].end || pathPoints[i+1].end) PathEndMarker(latLng = pathPoints[i+1].toLatLng(), color = if (i == pathPoints.size - 2) endColor else color)
        if (!pathPoints[i].end) Polyline(points = listOf(pathPoints[i].toLatLng(), pathPoints[i+1].toLatLng()), color = color, width = 10f, visible = true)
    }
    if (pathPoints.isNotEmpty()) PathEndMarker(latLng = pathPoints[0].toLatLng(), color = startColor)
    */
    val segments = pathPoints.toSegments()
    for (i in segments.indices) {
        if (i == 0) PathEndMarker(latLng = segments[i].first(), color = startColor)
        else PathEndMarker(latLng = segments[i].first(), color = color)
        Polyline(points = segments[i], color = color, width = 10f, visible = true)
        if (i == segments.size - 1) {
            if (pathPoints.lastOrNull()?.end == true)
                PathEndMarker(latLng = segments[i].last(), color = endColor)
        }
        else PathEndMarker(latLng = segments[i].last(), color = color)

    }
}


@Composable
fun risingDoubleAsState(target: Double): State<Double> {
    val cur = rememberSaveable {
        mutableStateOf(0.0)
    }
    LaunchedEffect(key1 = target) {
        val time = 2000
        val start = System.currentTimeMillis()
        while(true) {
            delay(20)
            val x = (System.currentTimeMillis() - start).toDouble()/time
            val t = Math.pow(x, 0.2)
            if (t >= 1) {
                cur.value = target
                break
            }
            else cur.value = t*target
        }
    }
    return cur
}
@Composable
fun risingLongAsState(target: Long): State<Long> {
    val cur = rememberSaveable {
        mutableStateOf(0L)
    }
    LaunchedEffect(key1 = target) {
        val time = 2000
        val start = System.currentTimeMillis()
        while(true) {
            delay(20)
            val x = (System.currentTimeMillis() - start).toDouble()/time
            val t = sqrt(x)
            if (t >= 1) {
                cur.value = target
                break
            }
            else cur.value = (t*target).toLong()
        }
    }
    return cur
}