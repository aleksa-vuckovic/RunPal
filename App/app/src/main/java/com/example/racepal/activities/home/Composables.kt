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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.racepal.ui.theme.LightGreen
import com.example.racepal.ui.theme.StandardButton
import com.example.racepal.ui.theme.StandardOutlinedTextField

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
            onClick = onSoloRun,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        HomeButton(
            icon = Icons.Default.Groups,
            text = "Group run",
            onClick = onGroupRun,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        HomeButton(
            icon = Icons.Default.SportsScore,
            text = "Event",
            onClick = onEvent,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}


@Composable
fun GroupRunSelectionScreen( onJoin: (String) -> Unit, onCreate: () -> Unit, modifier: Modifier = Modifier) {

    var room by rememberSaveable {
        mutableStateOf("")
    }

    val round = 20.dp
    val style = MaterialTheme.typography.displaySmall

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {

        Text(text = "Paste the room ID here:",
            style = style,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp)
        ) {
            StandardOutlinedTextField(value = room,
                onChange = {room = it},
                shape = RoundedCornerShape(topStart = round, topEnd = round, bottomStart = 0.dp, bottomEnd = 0.dp),
                minLines = 3,
                modifier = Modifier
                    .fillMaxWidth())
            Button(onClick = { onJoin(room) },
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = round, bottomEnd = round),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightGreen,
                    contentColor = Color.Black
                )
            ) {
                Text(text = "Join")
            }
        }
        
        Text(text = "Or create and share the code with your friends:",
            style = style,
            textAlign = TextAlign.Center)
        
        StandardButton(onClick = onCreate) {
            Text(text = "Create")
        }
    }
}




//////////////////////////Previews

@Preview
@Composable
fun PreviewHomeButton() {
    GroupRunSelectionScreen(onJoin = {}, onCreate = {}, modifier = Modifier.fillMaxSize().padding(30.dp))
    
}