package com.example.runpal.activities.home

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.runpal.DateOnlyFormatter
import com.example.runpal.DoubleInput
import com.example.runpal.UTCDateTimeFormatter
import com.example.runpal.ImageSelector
import com.example.runpal.LoadingScreen
import com.example.runpal.LongTimeFormatter
import com.example.runpal.R
import com.example.runpal.TimeFormatter
import com.example.runpal.Units
import com.example.runpal.activities.running.PanelText
import com.example.runpal.risingDoubleAsState
import com.example.runpal.borderBottom
import com.example.runpal.join
import com.example.runpal.limitText
import com.example.runpal.models.Event
import com.example.runpal.models.PathPoint
import com.example.runpal.models.Run
import com.example.runpal.models.RunData
import com.example.runpal.risingLongAsState
import com.example.runpal.AxesOptions
import com.example.runpal.Chart
import com.example.runpal.ChartDataset
import com.example.runpal.ChartOptions
import com.example.runpal.ChartType
import com.example.runpal.ui.theme.BadgeType
import com.example.runpal.ui.theme.LightGreen
import com.example.runpal.ui.theme.MediumBlue
import com.example.runpal.ui.theme.StandardBadge
import com.example.runpal.ui.theme.StandardButton
import com.example.runpal.ui.theme.StandardSpinner
import com.example.runpal.ui.theme.StandardStatRow
import com.example.runpal.ui.theme.StandardTextField

@Composable
fun HomeButton(icon: ImageVector, text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .padding(start = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = text,
            modifier = Modifier.size(100.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text,
            //.wrapContentHeight(align = Alignment.CenterVertically),
            //.clickable { onClick() },
            style = MaterialTheme.typography.displayMedium)
            //textAlign = TextAlign.Center)
    }

}

@Composable
fun MenuScreen(onSoloRun: () -> Unit, onGroupRun: () -> Unit, onEvent: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) {
        HomeButton(
            icon = Icons.AutoMirrored.Filled.DirectionsRun,
            text = stringResource(id = R.string.solo_run),
            onClick = onSoloRun,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        HomeButton(
            icon = Icons.Default.Groups,
            text = stringResource(id = R.string.group_run),
            onClick = onGroupRun,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        HomeButton(
            icon = Icons.Default.SportsScore,
            text = stringResource(id = R.string.event),
            onClick = onEvent,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}


@OptIn(ExperimentalCoilApi::class)
@Composable
fun SmallEventCard(event: Event, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .clickable { onClick() }
            .width(150.dp)
            .fillMaxHeight()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val textColor = MaterialTheme.colorScheme.onPrimaryContainer
        Image(painter = rememberImagePainter(data = event.imageUri),
            contentDescription = event.name,
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            modifier = Modifier
                .size(130.dp)
                .clip(shape = RoundedCornerShape(15.dp))
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = event.name,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            color = textColor)
        Spacer(modifier = Modifier.height(10.dp))
        EventStatus(event = event, textColor = textColor)

    }
}

@Composable
fun EventsRow(events: List<Event>, onClick: (Event) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Text(text = stringResource(id = R.string.events_you_follow),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(10.dp))
        Row(
            modifier = modifier
                .horizontalScroll(rememberScrollState())
                .fillMaxWidth()
                .height(250.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (event in events) {
                SmallEventCard(event = event, onClick = {onClick(event)})
            }
            if (events.size == 0) Text(text = stringResource(id = R.string.no_events_to_show),
                modifier = Modifier.padding(10.dp))
        }
    }
}

@Composable
fun BigEventCard(event: Event, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        val textColor = MaterialTheme.colorScheme.onSurface
        Image(painter = rememberImagePainter(data = event.imageUri),
            contentDescription = event.name,
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            modifier = Modifier
                .size(130.dp)
                .clip(shape = RoundedCornerShape(15.dp))
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = event.name,
                style = MaterialTheme.typography.titleSmall,
                color = textColor
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = limitText(event.description),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(text = UTCDateTimeFormatter.format(event.time).first,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EventsScreen(followedEvents: List<Event>,
                 searchEvents: List<Event>,
                 onClick: (Event) -> Unit,
                 onSearch: (String) -> Unit,
                 onCreate: () -> Unit,
                 modifier: Modifier = Modifier) {
    var search by rememberSaveable {
        mutableStateOf("")
    }
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item { EventsRow(events = followedEvents, onClick = onClick) }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCreate() }
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = stringResource(id = R.string.create_event))
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = stringResource(id = R.string.create_event)
                )
            }
        }
        stickyHeader {
            TextField(
                value = search,
                onValueChange = { search = it; onSearch(it) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(id = R.string.search)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = stringResource(id = R.string.search_upcoming_events))
                }
            )
        }
        items(searchEvents) {
            BigEventCard(
                event = it,
                onClick = { onClick(it) },
                modifier = Modifier
                    .padding(10.dp)
                    .borderBottom(strokeWidth = 1.dp, color = Color.Black)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(onCreate: (String, String, Long?, Double, Uri?) -> Unit,
                      errorMessage: String,
                      preferredUnits: Units,
                      modifier: Modifier = Modifier) {

    var name by rememberSaveable {
        mutableStateOf("")
    }
    var description by rememberSaveable {
        mutableStateOf("")
    }
    var distance by rememberSaveable {
        mutableStateOf(0.0)
    }
    var units by remember {
        mutableStateOf(preferredUnits)
    }
    val time = rememberTimePickerState(initialHour = 12, initialMinute = 0, is24Hour = true)
    var timeDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var date = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    var dateDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var image by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }

    fun selectedTime() = if (date.selectedDateMillis == null) null else date.selectedDateMillis!! + (time.hour*60+time.minute)*60000

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val style = MaterialTheme.typography.labelMedium

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(id = R.string.event_name), style = style, modifier = Modifier.weight(0.3f))
            StandardTextField(value = name, onChange = {name = it}, modifier = Modifier.weight(0.7f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(id = R.string.description), style = style, modifier = Modifier.weight(0.3f))
            StandardTextField(value = description,
                onChange = {description = it},
                minLines = 4,
                modifier = Modifier.weight(0.7f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(id = R.string.distance), style = style, modifier = Modifier.weight(0.3f))
            Row(modifier = Modifier.weight(0.7f),
                horizontalArrangement = Arrangement.End) {
                DoubleInput(initial = distance, onChange = {distance = it}, modifier = Modifier.width(150.dp))
                Box(modifier = Modifier
                    .size(55.dp)
                    .clickable { units = units.next }
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
                    .borderBottom(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)) {
                    Text(text = units.standardDistanceInput,
                        style = style,
                        modifier = Modifier.align(Alignment.Center))
                }
            }
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(id = R.string.date_and_time_utc), style = style, modifier = Modifier.weight(0.3f))
            StandardTextField(
                value = if (selectedTime()!= null) UTCDateTimeFormatter.format(selectedTime()!!).first else "Click to select",
                onChange = {},
                enabled = false,
                modifier = Modifier
                    .clickable { dateDialog = true }
                    .weight(0.7f))
        }
        if (dateDialog) DatePickerDialog(
            onDismissRequest = { dateDialog = false },
            confirmButton = {
                Button(onClick = {dateDialog = false; timeDialog = true}) {
                    Text(stringResource(id = R.string.next))
                }
            }) {
            DatePicker(state = date)
        }
        if (timeDialog) DatePickerDialog(
            onDismissRequest = { timeDialog = false },
            confirmButton = {
                Button(onClick = { timeDialog = false }) {
                    Text(stringResource(id = R.string.ok))
                }
            },
            modifier = Modifier.padding(50.dp)) {
            TimeInput(state = time, modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(id = R.string.event_image), style = style, modifier = Modifier.weight(0.3f))
            ImageSelector(input = image, onSelect = {image = it}, Modifier.size(200.dp))
        }

        StandardButton(onClick = { onCreate(name, description, selectedTime(), units.fromStandardDistanceInput(distance), image)})
        {
            Text(stringResource(id = R.string.create))
        }
        Text(text = errorMessage, style = style.copy(color = Color.Red))
    }
}

@Composable
fun EventStatus(event: Event, textColor: Color) {
    if (event.status == Event.Status.UPCOMING)
        Text(text = stringResource(id = R.string.starting_in) + " " + LongTimeFormatter.format(event.time - System.currentTimeMillis()).first,
            style = MaterialTheme.typography.bodySmall,
            color = textColor)
    else if (event.status == Event.Status.CURRENT)
        StandardBadge(text = stringResource(id = R.string.happening_now),
            type = BadgeType.SUCCESS
        )
    else StandardBadge(text = stringResource(id = R.string.past),
        type = BadgeType.DANGER
    )
}

@Composable
fun EventScreen(event: Event, onJoin: () -> Unit, onFollow: () -> Unit, onUnfollow: () -> Unit, units: Units = Units.METRIC, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) {

        Row(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            val textColor = MaterialTheme.colorScheme.onPrimaryContainer
            Image(painter = rememberImagePainter(data = event.imageUri),
                contentDescription = event.name,
                modifier = Modifier
                    .sizeIn(140.dp, 140.dp, 160.dp, 160.dp)
                    .clip(shape = RoundedCornerShape(10.dp))
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center)
            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(text = event.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.borderBottom(strokeWidth = 1.dp)
                    )
                Text(text = UTCDateTimeFormatter.format(event.time).first,
                    style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StandardBadge(text = event.followers.toString(), fontSize = 20.sp)
                    Text(text = stringResource(id = R.string.of_followes), style = MaterialTheme.typography.labelMedium)
                }
                Spacer(modifier = Modifier.height(20.dp))
                EventStatus(event = event, textColor = textColor)
            }


        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surface)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(text = event.description,
                modifier = Modifier.padding(20.dp),
                color = MaterialTheme.colorScheme.onSurface)
            Text(text = "Distance: ${units.distanceFormatter.format(event.distance).join()}",
                modifier = Modifier.padding(20.dp),
                color = MaterialTheme.colorScheme.onSurface)
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (event.status == Event.Status.CURRENT)
                    ElevatedButton(onClick = onJoin,
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = LightGreen
                        )) {
                        Text(text = stringResource(id = R.string.join_now))
                    }
                if (event.following)
                    ElevatedButton(onClick = onUnfollow,
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )) {
                        Text(text = stringResource(id = R.string.unfollow))
                    }
                else ElevatedButton(onClick = onFollow,
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )) {
                    Text(text = stringResource(id = R.string.follow))
                }
            }
        }





    }
}


@Composable
fun RunInfo(runData: RunData, onClick: () -> Unit, units: Units, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .shadow(elevation = 5.dp, shape = RoundedCornerShape(10.dp))
            .clip(shape = RoundedCornerShape(10.dp))
            .background(color = MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = DateOnlyFormatter.format(runData.run.start!!).first, style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.width(10.dp))
        if (runData.run.room != null) StandardBadge(text = stringResource(id = R.string.group), type = BadgeType.INFO)
        else if (runData.run.event != null) StandardBadge(text = stringResource(id = R.string.event_lowercase), type = BadgeType.SUCCESS)
        Spacer(modifier = Modifier.weight(1f))
        PanelText(text = TimeFormatter.format(runData.run.running), modifier = Modifier
            .padding(10.dp))
        PanelText(text = units.distanceFormatter.format(runData.location.distance), modifier = Modifier
            .padding(10.dp))
    }
}

@Composable
fun HistoryScreen(onClick: (Run) -> Unit, units: Units) {
    val vm: HistoryViewModel = hiltViewModel()
    LazyColumn(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(20.dp),
        state = rememberLazyListState()
    ) {
        items(vm.runs) {
            RunInfo(runData = it, onClick = { onClick(it.run) }, units = units, modifier = Modifier.padding(20.dp))
        }
        if (!vm.end) item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(50.dp))
                LaunchedEffect(key1 = vm.runs) {
                    vm.more()
                }
            }
        }
    }
}

@Composable
fun StatsScreen(units: Units) {
    val vm: StatsViewModel = hiltViewModel()

    if (vm.state == StatsViewModel.State.LOADING) LoadingScreen()
    else {
        var selected by rememberSaveable {
            mutableStateOf(vm.options[0])
        }
        val totalKm by risingDoubleAsState(target = vm.totalKm[selected]!!)
        val totalTime by risingLongAsState(target = vm.totalTime[selected]!!)
        val dataset = ChartDataset(data = vm.runData[selected]!!, xValue={it.run.start!!.toDouble()}, yValue={it.location.distance})
        val options = ChartOptions(
            color = MediumBlue,
            width = vm.chartWidthMap[selected]!!,
            markers = true,
            markerLabel = units.distanceFormatter,
            show = true,
            type = ChartType.SCATTER
        )
        val axesOptions = AxesOptions(
            yLabel = units.distanceFormatter,
            yTickCount = 5,
            xExpandFactor = 1.2,
            yExpandFactor = 1.4,
            xSpanMin = 24*60*60*1000.0
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StandardSpinner(values = vm.options, selected = selected, onSelect = {selected = it})
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(id = R.string.total_distance),
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                PanelText(
                    text = units.distanceFormatter.format(totalKm),
                    bigStyle = MaterialTheme.typography.displayLarge,
                    smallStyle = MaterialTheme.typography.displaySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .borderBottom()
                        .padding(10.dp)
                )
                StandardStatRow(
                    name = stringResource(id = R.string.best_run),
                    value = units.distanceFormatter.format(vm.bestKm[selected]!!)
                )
                StandardStatRow(
                    name = stringResource(id = R.string.average_run),
                    value = units.distanceFormatter.format(vm.avgKm[selected]!!)
                )

                Spacer(modifier = Modifier.height(30.dp))
                Chart(
                    datasets = listOf(dataset),
                    options = listOf(options),
                    axesOptions = axesOptions,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                )
                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = stringResource(id = R.string.total_running_time),
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                PanelText(
                    text = LongTimeFormatter.format(totalTime),
                    bigStyle = MaterialTheme.typography.displayLarge,
                    smallStyle = MaterialTheme.typography.displaySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .borderBottom()
                        .padding(10.dp)
                )
                StandardStatRow(
                    name = stringResource(id = R.string.longest_run),
                    value = TimeFormatter.format(vm.longestTime[selected]!!)
                )
            }
        }
    }
}


//////////////////////////Previews

@Preview
@Composable
fun PreviewHomeButton() {
    val runData = RunData(run = Run(start = System.currentTimeMillis(), running = 34*60000, event =""), location = PathPoint(distance = 1256.0))
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        RunInfo(runData = runData, onClick = { }, units = Units.METRIC,
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth())
        RunInfo(runData = runData, onClick = { }, units = Units.METRIC,
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth())
    }
    
}