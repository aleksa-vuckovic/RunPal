package com.example.racepal.activities.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.racepal.Destination


object HistoryDestination: Destination {
    override val argsRoute: String = "hitsory"
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