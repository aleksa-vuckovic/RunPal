package com.example.runpal.activities.results.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.runpal.R
import com.example.runpal.TimeFormatter
import com.example.runpal.activities.running.PanelText
import com.example.runpal.borderBottom
import com.example.runpal.models.EventResult
import com.example.runpal.ui.theme.Bronze
import com.example.runpal.ui.theme.DarkBronze
import com.example.runpal.ui.theme.DarkGold
import com.example.runpal.ui.theme.DarkSilver
import com.example.runpal.ui.theme.Gold
import com.example.runpal.ui.theme.LightBlue
import com.example.runpal.ui.theme.MainBlue
import com.example.runpal.ui.theme.RichBlack
import com.example.runpal.ui.theme.Silver

@Composable
fun EventResultScreen(ranking: List<EventResult>, user: String) {
    val place = remember(ranking) {
        ranking.indexOfFirst { it.user == user } + 1
    }
    val colors = remember(place) {
        if (place == 1) Gold to DarkGold
        else if (place == 2) Silver to DarkSilver
        else if (place == 3) Bronze to DarkBronze
        else MainBlue to RichBlack
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .borderBottom()
            .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)) {
            val placetext = place.toString() + (if (place == 1) stringResource(id = R.string.st) else if (place == 2) stringResource(id = R.string.nd) else if (place == 3) stringResource(id = R.string.rd) else stringResource(id = R.string.th))
            Box(modifier = Modifier
                .width(150.dp)
                .height(200.dp)
                .shadow(elevation = 5.dp, shape = RoundedCornerShape(50.dp))
                .clip(shape = RoundedCornerShape(10.dp))
                .background(color = colors.first),
                contentAlignment = Alignment.Center
            ) {
                Text(text = placetext, style = MaterialTheme.typography.displayLarge, color = colors.second)
            }
            Text(text = stringResource(id = R.string.congrats_you_won)+ " ${placetext} " + stringResource(id = R.string.place) + ".")
        }
        Row(
            modifier = Modifier
                .borderBottom()
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "", style = MaterialTheme.typography.titleSmall, modifier = Modifier.width(30.dp))
            Text(text = stringResource(id = R.string.user), style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = stringResource(id = R.string.time), style = MaterialTheme.typography.titleSmall)
        }
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(ranking.indices.toList()) {
                Row(
                    modifier = Modifier
                        .borderBottom()
                        .background(color = if (it == place - 1) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${it+1}.", style = MaterialTheme.typography.titleSmall, modifier = Modifier.width(30.dp))
                    Text(text = "${ranking[it].name} ${ranking[it].last}", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.weight(1f))
                    PanelText(text = TimeFormatter.format(ranking[it].time!!))
                }
            }
        }
    }
}