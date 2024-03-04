package com.example.runpal.activities.login

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.runpal.Destination


object LoginDestination: Destination {
    override val argsRoute: String = "login"
    override val baseRoute: String = "login"
    override val icon: ImageVector = Icons.AutoMirrored.Filled.Login
    override val label: String = "Login"
    override val title: String = "Login"
}

object RegisterDestination: Destination {
    override val argsRoute: String = "register"
    override val baseRoute: String = "register"
    override val icon: ImageVector = Icons.Default.HowToReg
    override val label: String = "Register"
    override val title: String = "Register"
}

val destinations = listOf(LoginDestination, RegisterDestination)