package com.example.runpal.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.toSize
import com.example.runpal.EmptyFormatter
import com.example.runpal.Formatter
import com.example.runpal.binarySearch
import com.example.runpal.models.PathPoint
import com.example.runpal.join


/**
 * Represents one dataset (one run)
 * and provides the minimuma, maxima and average.
 * (The average is only calculated for the Y value)
 */
class PathChartDataset(
    val path: List<PathPoint>,
    val xValue: (PathPoint) -> Double,
    val yValue: (PathPoint) -> Double
) {
    companion object {
        val EMPTY: PathChartDataset = PathChartDataset(path = listOf(PathPoint.INIT), xValue = {0.0}, yValue = {0.0})
    }

    val minX: Double
    val maxX: Double
    val minY: Double
    val maxY: Double
    val avgY: Double


    init {
        minX = path.minOf(xValue)
        maxX = path.maxOf(xValue)
        minY = path.minOf(yValue)
        maxY = path.maxOf(yValue)
        var t = 0.0
        var p = 0.0
        for (i in 0..path.size-2)
            if (!path[i+1].end) {val dt = path[i+1].time-path[i].time; t+=dt; p+=dt*(yValue(path[i]) + yValue(path[i+1]))/2; }
        if (t > 0.0) avgY = p/t
        else avgY = 0.0
    }
}


/**
 * This class is used for calculating the range of values presented on the chart,
 * an mapping values to chart coordinates based on the value range, chartSize,
 * chartOffset and axes options.
 */
class ChartConfig(val datasets: List<PathChartDataset>,
                  val axes: AxesOptions,
                  val chartSize: Size,
                  val chartOffset: Size
) {

    val originX: Double
    val originY: Double
    val spanX: Double
    val spanY: Double

    init {
        val minX = datasets.minOf { it.minX }
        val maxX = datasets.maxOf { it.maxX }
        val minY = datasets.minOf { it.minY }
        val maxY = datasets.maxOf { it.maxY }

        spanX = maxX - minX
        val spanYData = maxY-minY
        spanY = if (spanYData * axes.yExpandFactor < axes.ySpanMin) axes.ySpanMin else spanYData*axes.yExpandFactor
        originX = minX
        //Keeping y values positive
        originY = if (minY - (spanY-spanYData)/2 < 0) 0.0 else minY - (spanY-spanYData)/2
    }

    /**
     * @return The horizontal canvas coordinate
     * corresponding to value x.
     */
    fun mapX(x: Double): Float {
        if (spanX == 0.0) return 0f
        return ((x-originX)/spanX).toFloat()*chartSize.width + chartOffset.width
    }
    /**
     * @return The vertical canvas coordinate
     * corresponding to value y.
     */
    fun mapY(y: Double): Float {
        val t = chartSize.height
        if (spanY == 0.0) return t
        else return t - t * ((y - originY) / spanY).toFloat()
    }
    /**
     * @return The canvas Offset
     * corresponding to the (x,y) value pair.
     */
    fun map(x: Double, y: Double): Offset {
        return Offset(mapX(x), mapY(y))
    }

    /**
     * @return The canvas Offset
     * corresponding to the horizontal tick for value x.
     */
    fun mapTickX(x: Double): Offset {
        return Offset(mapX(x), chartSize.height)
    }
    /**
     * @return The canvas Offset
     * corresponding to the horizontal tick for value y.
     */
    fun mapTickY(y: Double): Offset {
        return Offset(chartOffset.width ,mapY(y))
    }
    /**
     * @return The canvas Offset
     * corresponding to the chart origin.
     */
    val chartOrigin = Offset(chartOffset.width, chartSize.height)
    val chartTopLeft = Offset(chartOffset.width, 0f)
    val chartTopRight = Offset(chartOffset.width + chartSize.width,  0f)
    val chartBottomRight = Offset(chartOffset.width + chartSize.width, chartSize.height)
}

/**
 * Options for a single dataset.
 */
class PathChartOptions(
    val color: Color = Color.Black,
    val shade: Boolean = false,
    val width: Float = 3f,
    val markers: Boolean = false,
    val markerLabel: Formatter<Double>? = null,
    val markerLabelStyle: TextStyle = TextStyle.Default,
    val show: Boolean = true
)

/**
 * Axis options for the entire chart.
 *
 * @param yExpandFactor Specifies the expansion factor for the y axis span,
 * relative to the y value span of the dataset. This is to prevent the curve from hitting
 * the very top and bottom of the chart. This value should be equal to or greater than 1.
 * @param ySpanMin The minimum span of the y axis. If the span calculated using yExpandFactor
 * is less than this, then the span will be expanded to fit this value.
 */
data class AxesOptions(
    val xLabel: Formatter<Double> = EmptyFormatter,
    val yLabel: Formatter<Double> = EmptyFormatter,
    val labelStyle: TextStyle = TextStyle.Default,
    val xTickCount: Int = 0,
    val yTickCount: Int = 0,
    val yExpandFactor: Double = 1.1,
    val ySpanMin: Double = 3.0
)

@Composable
private fun PathChartLine(data: PathChartDataset,
                          options: PathChartOptions,
                          chartConfig: ChartConfig,
                          touchPositionState: State<Offset>
                  ) {
    if (!options.show) return
    if (data.path.size < 2) return
    val textMeasurer = rememberTextMeasurer()
    val chartOrigin = chartConfig.chartOrigin
    val bounds = remember(chartConfig) {
        Path().apply {
            moveTo(chartConfig.chartTopLeft.x, chartConfig.chartTopLeft.y)
            lineTo(chartConfig.chartTopRight.x, chartConfig.chartTopRight.y)
            lineTo(chartConfig.chartBottomRight.x, chartConfig.chartBottomRight.y)
            lineTo(chartConfig.chartOrigin.x, chartConfig.chartOrigin.y)
            close()
        }
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
        clipPath(
            path = bounds,
            clipOp = ClipOp.Intersect
        ) {
            for (i in 0..data.path.size - 2) {
                if (!data.path[i].end) {
                    val startX = data.xValue(data.path[i])
                    val startY = data.yValue(data.path[i])
                    val endX = data.xValue(data.path[i + 1])
                    val endY = data.yValue(data.path[i + 1])
                    val start = chartConfig.map(startX, startY)
                    val end = chartConfig.map(endX, endY)
                    if (options.shade) {
                        val path = Path().apply {
                            moveTo(start.x, start.y)
                            lineTo(end.x, end.y)
                            lineTo(end.x, chartOrigin.y)
                            lineTo(start.x, chartOrigin.y)
                            close()
                        }
                        drawPath(
                            path = path,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    options.color.copy(alpha = 0.8f),
                                    options.color.copy(alpha = 0f)
                                )
                            )
                        )
                    }
                    drawLine(
                        color = options.color,
                        strokeWidth = options.width,
                        start = start,
                        end = end,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
        if (options.markers && touchPositionState.value != Offset.Zero) {
            val touch = touchPositionState.value
            val point =
                binarySearch(data.path, { chartConfig.mapX(data.xValue(it)) }, touch.x)
            if (point == null) return@Canvas
            val selectedX = data.xValue(point)
            val selectedY = data.yValue(point)
            val selected = chartConfig.map(selectedX, selectedY)
            drawLine(
                color = options.color,
                start = selected.copy(x = chartOrigin.x),
                end = selected,
                strokeWidth = options.width / 2,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
            drawCircle(brush = Brush.radialGradient(
                colors = listOf(options.color, options.color.copy(alpha = 0f)),
                center = selected,
                radius = options.width*3),
                radius = options.width * 3,
                center = selected)
            drawCircle(color = Color.White, center = selected, radius = options.width/2)

            if (options.markerLabel != null) {
                val label = options.markerLabel.format(selectedY).join()
                val text = textMeasurer.measure(label, options.markerLabelStyle)
                drawText(text, topLeft = Offset(selected.x - text.size.width - options.width*4, selected.y - text.size.height))
            }
        }
    }
}
@Composable
fun PathChart(datasets: List<PathChartDataset>,
              options: List<PathChartOptions>,
              axesOptions: AxesOptions,
              modifier: Modifier = Modifier) {

    var size by remember {
        mutableStateOf(Size(0f, 0f))
    }
    val chartOffset = Size(90f, 45f)
    val chartSize = remember(size) {
        size.copy(width = size.width - chartOffset.width, height = size.height - chartOffset.height)
    }
    val chartConfig = remember(datasets, axesOptions, size) {
        ChartConfig(datasets = datasets, axes = axesOptions, chartSize = chartSize, chartOffset = chartOffset)
    }
    val textMeasurer = rememberTextMeasurer()

    val touchPosition = remember {
        mutableStateOf(Offset.Zero)
    }
    Box(modifier = modifier
        .onSizeChanged { size = it.toSize() }
        .pointerInput(Unit) {
            awaitPointerEventScope {
                val on = listOf(
                    PointerEventType.Enter,
                    PointerEventType.Press,
                    PointerEventType.Move
                )
                val off = listOf(
                    PointerEventType.Exit,
                    PointerEventType.Release,
                    PointerEventType.Unknown
                )
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Main)
                    if (on.contains(event.type)) {
                        if (event.changes.size > 0) touchPosition.value =
                            event.changes[0].position
                    } else {
                        touchPosition.value = Offset.Zero
                    }
                }
            }

        }) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            //drawing the axes
            val chartOrigin = chartConfig.chartOrigin
            drawLine(color = Color.Black, strokeWidth = 1f, start = chartOrigin, end = chartOrigin.copy(y = 0f))
            drawLine(color = Color.Black, strokeWidth = 1f, start = chartOrigin, end = chartOrigin.copy(x = size.width))

            //drawing the ticks and labels
            if (axesOptions.xTickCount!= 0 && axesOptions.xLabel != null) {
                val stepX = chartConfig.spanX / axesOptions.xTickCount
                for (i in 0..axesOptions.xTickCount) {
                    val x = chartConfig.originX + i*stepX
                    val pos = chartConfig.mapTickX(x)
                    drawLine(color = Color.Black, strokeWidth = 10f, start = pos, end = pos.copy(y = pos.y + 6f))


                    val label = axesOptions.xLabel.format(x).join()
                    val text = textMeasurer.measure(label, axesOptions.labelStyle)
                    drawText(text, topLeft = pos.copy(x = pos.x - text.size.width/2, y = pos.y+6f))
                }
            }
            if (axesOptions.yTickCount != 0 && axesOptions.yLabel != null) {
                val stepY = chartConfig.spanY / axesOptions.yTickCount
                for (i in 0..axesOptions.yTickCount) {
                    val y = chartConfig.originY + i*stepY
                    val pos = chartConfig.mapTickY(y)
                    drawLine(color = Color.Black, strokeWidth = 10f, start = pos, end = pos.copy(x = pos.x - 5f))

                    val label = axesOptions.yLabel.format(y).join()
                    val text = textMeasurer.measure(label, axesOptions.labelStyle)
                    drawText(text, topLeft = pos.copy(x = pos.x - chartOffset.width, y = pos.y - (if(i!=axesOptions.yTickCount) text.size.height/2  else 0)))
                }
            }
        }

        //Draw the lines
        //for (i in datasets.size - 1 downTo 0) {
        for (i in 0 .. datasets.size - 1) {
            PathChartLine(
                data = datasets[i],
                options = options[i],
                chartConfig = chartConfig,
                touchPositionState = touchPosition
            )
        }
    }
}