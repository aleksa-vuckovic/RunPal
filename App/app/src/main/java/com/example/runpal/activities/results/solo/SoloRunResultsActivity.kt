package com.example.runpal.activities.results.solo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.runpal.ErrorScreen
import com.example.runpal.KcalFormatter
import com.example.runpal.LoadingScreen
import com.example.runpal.activities.account.AccountActivity
import com.example.runpal.activities.results.PathChartAndPanel
import com.example.runpal.activities.results.UserSelection
import com.example.runpal.repositories.LoginManager
import com.example.runpal.repositories.SettingsManager
import com.example.runpal.restartApp
import com.example.runpal.ui.AxesOptions
import com.example.runpal.ui.theme.RunPalTheme
import com.example.runpal.ui.theme.StandardNavBar
import com.example.runpal.ui.theme.StandardTopBar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SoloRunResultsActivity : ComponentActivity() {

    @Inject
    lateinit var loginManager: LoginManager
    @Inject
    lateinit var settingsManager: SettingsManager

    val vm: SoloRunResultsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val units = settingsManager.units

        setContent {
            RunPalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val startDestination = ResultsDestination.argsRoute
                    val curRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: startDestination
                    val curDestination = destinationMap[curRoute]!!
                    Scaffold(
                        topBar = {
                            StandardTopBar(
                                onBack =  { if (!navController.popBackStack()) finish()},
                                onRefresh =  vm::reload,
                                onAccount = {startActivity(Intent(this@SoloRunResultsActivity, AccountActivity::class.java))},
                                onLogout = {
                                    loginManager.logout()
                                    this@SoloRunResultsActivity.restartApp()
                                },
                                title = curDestination.title
                            )
                        },
                        bottomBar = {
                            StandardNavBar(
                                destinations = bottomBarDestinations,
                                curRoute = curRoute,
                                onClick = {navController.navigate(it.argsRoute)}
                            )
                        }
                    ) {
                        if (vm.state == SoloRunResultsViewModel.State.LOADING) LoadingScreen()
                        else if (vm.state == SoloRunResultsViewModel.State.ERROR) ErrorScreen(message = "Try again.")
                        else NavHost(navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier.padding(it)) {

                            composable(route = ChartsDestination.argsRoute) {

                                val axesOptions = AxesOptions(
                                    yLabel = units.speedFormatter,
                                    labelStyle = MaterialTheme.typography.labelSmall,
                                    yTickCount = 5
                                )

                                Column(modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())) {
                                    PathChartAndPanel(
                                        title = "Speed",
                                        datasets = listOf(vm.speedDataset),
                                        selected = listOf(true),
                                        axesOptions = axesOptions,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(400.dp)
                                    )
                                    PathChartAndPanel(
                                        title = "Kcal",
                                        datasets = listOf(vm.kcalDataset),
                                        selected = listOf(true),
                                        axesOptions = axesOptions.copy(yLabel = KcalFormatter),
                                        cumulative = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(400.dp)
                                    )
                                    PathChartAndPanel(
                                        title = "Altitude",
                                        datasets = listOf(vm.altitudeDataset),
                                        selected = listOf(true),
                                        axesOptions = axesOptions.copy(yLabel = units.distanceFormatter, ySpanMin = 20.0),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(400.dp)
                                    )
                                    PathChartAndPanel(
                                        title = "Distance",
                                        datasets = listOf(vm.distanceDataset),
                                        selected = listOf(true),
                                        axesOptions = axesOptions.copy(yLabel = units.distanceFormatter),
                                        cumulative = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(400.dp)
                                    )
                                }
                            }

                            composable(route = ResultsDestination.argsRoute) {
                                Text(text = "TO DO")
                            }
                        }
                    }
                }
            }
        }
    }
}