package com.example.racepal.activities.home

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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
            text = "Solo run",
            onClick = {

            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        HomeButton(
            icon = Icons.Default.Groups,
            text = "Group run",
            onClick = {

            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        HomeButton(
            icon = Icons.Default.SportsScore,
            text = "Event",
            onClick = {

            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}




//////////////////////////Previews

@Preview
@Composable
fun PreviewHomeButton() {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HomeButton(
            icon = Icons.AutoMirrored.Filled.DirectionsRun,
            text = "Solo run",
            onClick = {

            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        HomeButton(
            icon = Icons.Default.Groups,
            text = "Group run",
            onClick = {

            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        HomeButton(
            icon = Icons.Default.SportsScore,
            text = "Event",
            onClick = {

            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}