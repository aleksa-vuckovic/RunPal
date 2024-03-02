package com.example.racepal.ui

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
import com.example.racepal.Formatter
import com.example.racepal.binarySearch
import com.example.racepal.models.PathPoint
import com.example.racepal.join
import kotlin.math.max
import kotlin.math.min



/**
 * This class is used for calculating the range of values presented on the chart,
 * an retrieving the relative position of a value on the corresponding chart axis.
 */
class ChartData(val minX: Double, val maxX: Double, val minY: Double, val maxY: Double) {

    companion object {
        final val Empty: ChartData = ChartData(0.0, 0.0, 0.0, 0.0)
    }

    val spanX: Double = maxX - minX
    val spanY: Double = (maxY - minY) * 1.1
    val originX: Double = minX
    val originY: Double = minY

    /**
     * The dataset must not be empty.
     * For empty datasets, use ChartData.Empty.
     */
    constructor(path: List<PathPoint>,
                xValue: (PathPoint) -> Double,
                yValue: (PathPoint) -> Double) :
            this(path.minOf { xValue(it) },
                path.maxOf { xValue(it) },
                path.minOf { yValue(it) },
                path.maxOf { yValue(it) }
                )

    /**
     * Returns the relative position of x on the x axis
     * as a Double between 0.0 and 1.0.
     */
    fun mapX(x: Double): Double {
        if (spanX == 0.0) return 0.0
        else return (x - originX) / spanX
    }
    /**
     * Returns the relative position of y on the y axis
     * as a Double between 0.0 and 1.0.
     */
    fun mapY(y: Double): Double {
        if (spanY == 0.0) return 0.0
        else return (y - originY) / spanY
    }

    operator fun plus(data: ChartData): ChartData {
        if (this === Empty) return data
        else if (data === Empty) return data
        else return ChartData(
            min(this.minX, data.minX),
            max(this.maxX, data.maxX),
            min(this.minY, data.maxY),
            max(this.maxY, data.maxY)
        )
    }
}
/**
 * This class uses ChartData, the chart size and offset, to map
 * data points to actual canvas coordinates.
 */
class ChartConfiguration(val chartData: ChartData,
                         val chartSize: Size,
                         val chartOffset: Size
    ) {

    /**
     * @return The horizontal canvas coordinate
     * corresponding to value x.
     */
    fun mapX(x: Double): Float {
        return chartData.mapX(x).toFloat()*chartSize.width + chartOffset.width
    }
    /**
     * @return The horizontal canvas coordinate
     * corresponding to value y.
     */
    fun mapY(y: Double): Float {
        return chartSize.height - chartData.mapY(y).toFloat()*chartSize.height
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
    fun chartOrigin() = Offset(chartOffset.width, chartSize.height)
}

class PathLineChartDataset(val path: List<PathPoint>,
                           val xValue: (PathPoint) -> Double,
                           val yValue: (PathPoint) -> Double) {
    val chartData = ChartData(path, xValue, yValue)
}
class PathLineChartOptions(
    val color: Color = Color.Black,
    val shade: Boolean = false,
    val width: Float = 3f,
    val markers: Boolean = false,
    val markerLabel: Formatter<Double>? = null,
    val markerLabelStyle: TextStyle = TextStyle.Default
)
class AxesOptions(
    val xLabel: Formatter<Double>? = null,
    val yLabel: Formatter<Double>? = null,
    val labelStyle: TextStyle = TextStyle.Default,
    val xTickCount: Int = 0,
    val yTickCount: Int = 0,
)

@Composable
private fun PathChartLine(data: PathLineChartDataset,
                          options: PathLineChartOptions,
                          chartConfiguration: ChartConfiguration,
                          touchPositionState: State<Offset>
                  ) {
    val textMeasurer = rememberTextMeasurer()
    val chartOrigin = chartConfiguration.chartOrigin()
    val bounds = remember(chartConfiguration) {
        Path().apply {
            moveTo(chartConfiguration.chartOffset.width, 0f)
            relativeLineTo(dx = chartConfiguration.chartSize.width, dy = 0f)
            relativeLineTo(dx = 0f, dy = chartConfiguration.chartSize.height)
            relativeLineTo(dx = -chartConfiguration.chartSize.width, dy = 0f)
            close()
        }
    }
    if (data.path.size < 2) return
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
                    val start = chartConfiguration.map(startX, startY)
                    val end = chartConfiguration.map(endX, endY)
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
                binarySearch(data.path, { chartConfiguration.mapX(data.xValue(it)) }, touch.x)
            if (point == null) return@Canvas
            val selectedX = data.xValue(point)
            val selectedY = data.yValue(point)
            val selected = chartConfiguration.map(selectedX, selectedY)
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
fun PathChart(datasets: List<PathLineChartDataset>,
              options: List<PathLineChartOptions>,
              axes: AxesOptions,
              modifier: Modifier = Modifier) {

    var size by remember {
        mutableStateOf(Size(0f, 0f))
    }
    val chartOffset = remember {
        Size(90f, 45f)
    }
    val chartSize = remember(size) {
        size.copy(width = size.width - chartOffset.width, height = size.height - chartOffset.height)
    }
    val chartData = remember(datasets) {
        var res = ChartData.Empty
        for (dataset in datasets) res += dataset.chartData
        res
    }
    val chartConfiguration = remember(size, datasets) {
        ChartConfiguration(chartData, chartSize, chartOffset)
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
            val chartOrigin = chartConfiguration.chartOrigin()
            drawLine(color = Color.Black, strokeWidth = 1f, start = chartOrigin, end = chartOrigin.copy(y = 0f))
            drawLine(color = Color.Black, strokeWidth = 1f, start = chartOrigin, end = chartOrigin.copy(x = size.width))

            //drawing the ticks and labels
            if (axes.xTickCount!= 0 && axes.xLabel != null) {
                val stepX = (chartData.maxX-chartData.minX) / axes.xTickCount
                for (i in 0..axes.xTickCount) {
                    val x = (chartData.originX + i*stepX)
                    val pos = chartConfiguration.mapTickX(x)
                    drawLine(color = Color.Black, strokeWidth = 10f, start = pos, end = pos.copy(y = pos.y + 6f))


                    val label = axes.xLabel.format(x).join()
                    val text = textMeasurer.measure(label, axes.labelStyle)
                    drawText(text, topLeft = pos.copy(x = pos.x - text.size.width/2, y = pos.y+6f))
                }
            }
            if (axes.yTickCount != 0 && axes.yLabel != null) {
                val stepY = (chartData.maxY-chartData.minY) / axes.yTickCount
                for (i in 0..axes.yTickCount) {
                    val y = (chartData.originY + i*stepY)
                    val pos = chartConfiguration.mapTickY(y)
                    drawLine(color = Color.Black, strokeWidth = 10f, start = pos, end = pos.copy(x = pos.x - 5f))

                    val label = axes.yLabel.format(y).join()
                    val text = textMeasurer.measure(label, axes.labelStyle)
                    drawText(text, topLeft = pos.copy(x = pos.x - chartOffset.width, y = pos.y - (if(i!=axes.yTickCount) text.size.height/2  else 0)))
                }
            }
        }

        //Draw the lines
        for (i in 0..datasets.size - 1) {
            PathChartLine(
                data = datasets[i],
                options = options[i],
                chartConfiguration = chartConfiguration,
                touchPositionState = touchPosition
            )
        }
    }
}