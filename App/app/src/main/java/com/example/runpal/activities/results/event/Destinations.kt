package com.example.runpal.activities.results.event

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.AreaChart
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.runpal.Destination

object ChartsDestination: Destination {
    override val argsRoute: String = "charts"
    override val baseRoute: String = "charts"
    override val icon: ImageVector = Icons.Default.AreaChart
    override val label: String = "Details"
    override val title: String = "Details"
}

object ResultsDestination: Destination {
    override val argsRoute: String = "results"
    override val baseRoute: String = "results"
    override val icon: ImageVector = Icons.AutoMirrored.Filled.DirectionsRun
    override val label: String = "Results"
    override val title: String = "Results"
}

object RankingDestination: Destination {
    override val argsRoute: String = "ranking"
    override val baseRoute: String = "ranking"
    override val icon: ImageVector = Icons.Default.SportsScore
    override val label: String = "Ranking"
    override val title: String = "Ranking"

}

val bottomBarDestinations = listOf(ChartsDestination, ResultsDestination, RankingDestination)
val destinationMap = mapOf(
    ChartsDestination.argsRoute to ChartsDestination,
    ResultsDestination.argsRoute to ResultsDestination,
    RankingDestination.argsRoute to RankingDestination
)