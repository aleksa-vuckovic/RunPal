package com.example.racepal.account

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.racepal.Destination

object AccountDestination: Destination {
    override val argsRoute: String = "account"
    override val baseRoute: String = "account"
    override val icon: ImageVector? = null
    override val label: String? = null
    override val title: String = "Account"
}

object EditDestination: Destination {
    override val argsRoute: String = "edit"
    override val baseRoute: String = "edit"
    override val icon: ImageVector? = null
    override val label: String? = null
    override val title: String = "Edit"
}

val destinationsMap = mapOf<String, Destination>(
    AccountDestination.argsRoute to AccountDestination,
    EditDestination.argsRoute to EditDestination
    )