package com.example.runpal.activities.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.runpal.Destination
import com.example.runpal.EVENT_ID_KEY


object HistoryDestination: Destination {
    override val argsRoute: String = "history"
    override val baseRoute: String = "history"
    override val icon: ImageVector = Icons.Default.History
    override val label: String = "History"
    override val title: String = "History"
}
object MenuDestination: Destination {
    override val argsRoute: String = "menu"
    override val baseRoute: String = "menu"
    override val icon: ImageVector = Icons.Default.Home
    override val label: String = "Run"
    override val title: String = "Run"
}
object StatsDestination: Destination {
    override val argsRoute: String = "stats"
    override val baseRoute: String = "stats"
    override val icon: ImageVector = Icons.Default.QueryStats
    override val label: String = "Stats"
    override val title: String = "Stats"
}

val destinations = listOf(HistoryDestination, MenuDestination, StatsDestination)


object EventsDestination: Destination {
    override val argsRoute: String = "events"
    override val baseRoute: String = "events"
    override val icon: ImageVector? = null
    override val label: String? = null
    override val title: String = "Events"
}

object EventDestination: Destination {
    override val argsRoute: String = "event/{${EVENT_ID_KEY}}"
    override val baseRoute: String = "event/"
    override val icon: ImageVector? = null
    override val label: String? = null
    override val title: String = "Event"
    val arg: String = EVENT_ID_KEY
}

object CreateEventDestination: Destination {
    override val argsRoute: String = "create"
    override val baseRoute: String = "create"
    override val icon: ImageVector? = null
    override val label: String? = null
    override val title: String = "Create event"
}
