package com.example.runpal.activities.running.group

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.runpal.DEFAULT_PROFILE_URI
import com.example.runpal.LoadingDots
import com.example.runpal.models.Room
import com.example.runpal.models.User
import com.example.runpal.ui.theme.LightGreen
import com.example.runpal.ui.theme.LightRed
import com.example.runpal.ui.theme.StandardButton
import com.example.runpal.ui.theme.StandardOutlinedTextField

@Composable
fun EntryScreen( onJoin: (String) -> Unit, onCreate: () -> Unit, modifier: Modifier = Modifier) {

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


@OptIn(ExperimentalCoilApi::class)
@Composable
fun LobbyScreen(room: Room,
                users: Map<String, User>,
                state: LobbyViewModel.State,
                onCopy: () -> Unit,
                onLeave: () -> Unit,
                onReady: () -> Unit,
                modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {

            Text(text = "Copy and share the code:")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                    .padding(start = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = room._id, modifier = Modifier.weight(0.8f))
                IconButton(onClick = onCopy, modifier = Modifier.weight(0.2f)) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy room code.")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Members: ${room.members.size}/5")
            for (member in room.members) {
                val user = users[member]
                if (user == null) continue
                val ready: Boolean = room.ready.contains(member)
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .shadow(elevation = 10.dp)
                        .background(color = if (ready) LightGreen else MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.8f)
                            .padding(10.dp)
                    ) {
                        Image(painter = rememberImagePainter(data = /*user.profileUri*/ DEFAULT_PROFILE_URI),
                            contentDescription = "Profile picture of ${user.name} ${user.last}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(10.dp))
                                .size(100.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "${user.name} ${user.last}", style = MaterialTheme.typography.titleSmall)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.2f)
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (ready) Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = "Ready",
                            modifier = Modifier.size(50.dp))
                    }

                }
            }
        }

        Row(
            modifier = Modifier
                .height(60.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (state == LobbyViewModel.State.LOADING) LoadingDots(size = 18.dp, count = 3)
            else if (state == LobbyViewModel.State.WAITING) {
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .weight(0.5f)
                    .clickable { onLeave() }
                    .background(color = LightRed),
                    contentAlignment = Alignment.Center) {
                    Text(text = "Leave", style = MaterialTheme.typography.labelMedium)
                }
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .weight(0.5f)
                    .clickable { onReady() }
                    .background(color = LightGreen),
                    contentAlignment = Alignment.Center) {
                    Text(text = "I'm ready", style = MaterialTheme.typography.labelMedium)
                }
            }
            else if (state == LobbyViewModel.State.READY) Text(text = "Waiting for other members...")
        }
    }
}

@Preview
@Composable
fun PreviewLobby() {
    val room = Room(
        _id = "123456789123456789001234",
        members = listOf("1", "2", "3"),
        ready = listOf("2")
    )
    val user = User(name = "First", last = "Last")
    val users = mapOf<String, User>("1" to user, "2" to user, "3" to user)
    LobbyScreen(room = room,
        users = users,
        state = LobbyViewModel.State.READY,
        onCopy = {},
        onLeave = {},
        onReady = {},
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp))
}