package com.example.runpal.activities.running.group

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.runpal.Destination
import com.example.runpal.ROOM_ID_KEY


object EntryDestination: Destination {
    override val argsRoute: String = "entry"
    override val baseRoute: String = "entry"
    override val icon: ImageVector? = null
    override val label: String? = null
    override val title: String = "Group run"
}

object LobbyDestination: Destination {
    override val argsRoute: String = "lobby/{${ROOM_ID_KEY}}"
    override val baseRoute: String = "lobby/"
    override val icon: ImageVector? = null
    override val label: String? = null
    override val title: String = "Lobby"
    val arg: String = ROOM_ID_KEY
}