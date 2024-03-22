package com.example.runpal.activities.results

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.runpal.DEFAULT_ZOOM
import com.example.runpal.GoogleMapPath
import com.example.runpal.RUN_MARKER_COLORS
import com.example.runpal.activities.running.PanelText
import com.example.runpal.borderBottom
import com.example.runpal.models.RunData
import com.example.runpal.models.User
import com.example.runpal.AxesOptions
import com.example.runpal.Chart
import com.example.runpal.PathChartDataset
import com.example.runpal.ChartOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import kotlin.math.log2
import com.example.runpal.R
import com.example.runpal.ui.theme.StandardStatRow

@Composable
fun PathChartAndPanel(
    title: String,
    datasets: List<PathChartDataset>,
    selected: List<Boolean>,
    axesOptions: AxesOptions,
    cumulative: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier) {

    if (datasets.isEmpty()) return
    val options: MutableList<ChartOptions> = mutableListOf()
    val selectedCount = selected.count{it}
    var main: PathChartDataset = datasets[0]
    for (i in datasets.indices) {
        options.add(
            ChartOptions(
                color = RUN_MARKER_COLORS[i],
                shade = selectedCount == 1,
                width = 15f,
                markers = selected[i] && (i == 0 || selectedCount == 1),
                markerLabel = axesOptions.yLabel,
                markerLabelStyle = MaterialTheme.typography.labelMedium,
                show = selected[i]
            )
        )
        if (selected[i]) main = datasets[i]
    }
    Column(
        modifier = modifier
    ) {
        Text(text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick?.invoke() }
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(10.dp),
            color = MaterialTheme.colorScheme.onSurface)
        Chart(datasets = datasets,
            options = options,
            axesOptions = axesOptions,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
        )
        if (cumulative) Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.2f)
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(id = R.string.total), style = MaterialTheme.typography.labelLarge, modifier = Modifier.fillMaxHeight(0.5f))
            PanelText(text = axesOptions.yLabel.format(main.maxY), modifier = Modifier.fillMaxHeight(0.5f))
        }
        else Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.2f)
        ) {
            val data = listOf("min" to main.minY, "max" to main.maxY, "avg" to main.avgY)
            data.forEach {
                Column(modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PanelText(text = axesOptions.yLabel.format(it.second), modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f))
                    Text(text = it.first,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(bottom = 10.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun UserSelection(users: List<User>, selected: List<Boolean>, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(bottom = 4.dp)
    ) {
        for (i in users.indices) {
            Column(modifier = Modifier
                .borderBottom(
                    color = if (selected[i]) RUN_MARKER_COLORS[i].copy(alpha = 0.7f) else MaterialTheme.colorScheme.background,
                    strokeWidth = 8.dp
                )
                .clickable { onSelect(i) }
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(painter = rememberImagePainter(data = users[i].profileUri),
                    contentDescription = users[i].name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(shape = CircleShape),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
                Text(text = "${users[i].name} ${users[i].last}", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}


@Composable
fun GoogleMapRunResult(run: RunData,
                       color: Color,
                       cameraPositionState: CameraPositionState,
                       modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            GoogleMapPath(pathPoints = run.path, color = color)
        }
    }
}

@Composable
fun GeneralResults(runData: RunData,
                   color: Color,
                   mapMin: LatLng,
                   mapMax: LatLng,
                   values: List<Pair<String, Pair<String, String>>>,
                   modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng((mapMin.latitude + mapMax.latitude) / 2, (mapMin.longitude + mapMax.longitude) /2),
            DEFAULT_ZOOM)
    }
    val density = LocalDensity.current
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        GoogleMapRunResult(
            run = runData,
            color = color,
            cameraPositionState = cameraPositionState,
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .height(300.dp)
                .clip(shape = RoundedCornerShape(10.dp))
                .onSizeChanged {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                        LatLng(
                            (mapMin.latitude + mapMax.latitude) / 2,
                            (mapMin.longitude + mapMax.longitude) / 2
                        ),
                        zoomToFit(
                            latSpan = mapMax.latitude - mapMin.latitude,
                            height = it.height,
                            lngSpan = mapMax.longitude - mapMin.longitude,
                            width = it.width,
                            density = density
                        )
                    )
                })

        for (value in values) {
            StandardStatRow(name = value.first, value = value.second)
        }
    }
}


fun zoomToFit(latSpan: Double, height: Int, lngSpan: Double, width: Int, density: Density): Float {
    val heightDp = density.run { height.toDp() }
    val widthDp = density.run { width.toDp() }

    //at zoom N, 360 lat/lng degrees cover 256*2^N dp
    //find N such that latSpan is covered with heightDp
    //256*2^N/360*latSpan = heightDp*0.8 (leave some empty space)
    //2^N = heightDp*0.8*360/256/latSpan

    val zoomLat = log2(heightDp.value*0.8f*360f/256f/latSpan.toFloat())
    val zoomLng = log2(widthDp.value*0.8f*360f/256f/lngSpan.toFloat())
    val max = 20f

    return minOf(max, zoomLat, zoomLng)
}
