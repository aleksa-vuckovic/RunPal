package com.example.racepal.activities.home

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.racepal.RUN_ID_KEY
import com.example.racepal.account.AccountActivity
import com.example.racepal.activities.running.solo.SoloRunningActivity
import com.example.racepal.hasLocationPermission
import com.example.racepal.repositories.LoginManager
import com.example.racepal.restartApp
import com.example.racepal.ui.theme.RacePalTheme
import com.example.racepal.ui.theme.StandardNavBar
import com.example.racepal.ui.theme.StandardTopBar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    companion object {
        val PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    @Inject
    lateinit var loginManager: LoginManager

    var startIntent: Intent? = null
    val launcher =  registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
        if (granted.size == 2 && this@HomeActivity.startIntent != null) startActivity(this@HomeActivity.startIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RacePalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val startDestination = MenuDestination.argsRoute
                    val curDestination = navController.currentBackStackEntryAsState().value?.destination?.route ?: startDestination
                    Scaffold(
                        topBar = {
                            StandardTopBar(
                                onBack = { navController.popBackStack() },
                                onAccount = { startActivity(Intent(this@HomeActivity, AccountActivity::class.java)) },
                                onLogout = {
                                    loginManager.logout()
                                    this@HomeActivity.restartApp()
                                })
                        },
                        bottomBar = {
                            StandardNavBar(
                                destinations = destinations,
                                curDestination = curDestination,
                                onClick = {
                                    if (curDestination != it.argsRoute) navController.navigate(it.argsRoute)
                                }
                            )
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        NavHost(navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier.padding(it)) {
                            
                            composable(route = HistoryDestination.argsRoute) {
                                Text(text = "TODO")
                            }

                            composable(route = MenuDestination.argsRoute) {
                                MenuScreen(
                                    onSoloRun = {
                                        val runId = System.currentTimeMillis()
                                        startIntent = Intent(this@HomeActivity, SoloRunningActivity::class.java)
                                        startIntent?.putExtra(RUN_ID_KEY, runId)
                                        if (hasLocationPermission()) startActivity(startIntent)
                                        else launcher.launch(PERMISSIONS)
                                    },
                                    onGroupRun = { /*TODO*/ },
                                    onEvent = { /*TODO*/ },
                                    modifier = Modifier.fillMaxSize())
                            }

                            composable(route = StatsDestination.argsRoute) {
                                Text(text = "TODO")
                            }
                        }
                    }
                }
            }
        }
    }
}