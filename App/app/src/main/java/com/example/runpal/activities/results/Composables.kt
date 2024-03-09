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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.runpal.Formatter
import com.example.runpal.RUN_MARKER_COLORS
import com.example.runpal.activities.running.PanelText
import com.example.runpal.borderBottom
import com.example.runpal.models.User
import com.example.runpal.ui.AxesOptions
import com.example.runpal.ui.PathChart
import com.example.runpal.ui.PathChartDataset
import com.example.runpal.ui.PathChartOptions

@Composable
fun PathChartAndPanel(
    title: String,
    datasets: List<PathChartDataset>,
    selected: List<Boolean>,
    axesOptions: AxesOptions,
    cumulative: Boolean = false,
    modifier: Modifier = Modifier) {

    if (datasets.isEmpty()) return
    val options: MutableList<PathChartOptions> = mutableListOf()
    val selectedCount = selected.count{it}
    var main: PathChartDataset = datasets[0]
    for (i in datasets.indices) {
        options.add(
            PathChartOptions(
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
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(10.dp),
            color = MaterialTheme.colorScheme.onSurface)
        PathChart(datasets = datasets,
            options = options,
            axesOptions = axesOptions,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
        )
        if (cumulative) Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.2f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Total", style = MaterialTheme.typography.labelLarge)
            Text(text = main.maxY.toString(), style = MaterialTheme.typography.labelLarge)
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
        modifier = modifier.horizontalScroll(rememberScrollState())
    ) {
        for (i in users.indices) {
            Column(modifier = Modifier
                .borderBottom(
                    color = if (selected[i]) RUN_MARKER_COLORS[i].copy(alpha = 0.7f) else MaterialTheme.colorScheme.background,
                    strokeWidth = 10.dp
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
