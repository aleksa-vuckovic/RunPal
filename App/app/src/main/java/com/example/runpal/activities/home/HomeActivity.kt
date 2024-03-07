package com.example.runpal.activities.home

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.runpal.EVENT_ID_KEY
import com.example.runpal.ErrorScreen
import com.example.runpal.LoadingScreen
import com.example.runpal.RUN_ID_KEY
import com.example.runpal.activities.account.AccountActivity
import com.example.runpal.activities.running.event.EventRunActivity
import com.example.runpal.activities.running.group.GroupRunEntryActivity
import com.example.runpal.activities.running.solo.SoloRunActivity
import com.example.runpal.hasLocationPermission
import com.example.runpal.models.Run
import com.example.runpal.repositories.LoginManager
import com.example.runpal.restartApp
import com.example.runpal.ui.theme.RunPalTheme
import com.example.runpal.ui.theme.StandardButton
import com.example.runpal.ui.theme.StandardDialog
import com.example.runpal.ui.theme.StandardNavBar
import com.example.runpal.ui.theme.StandardTopBar
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

    var refresh by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RunPalTheme {
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
                                onBack = { if(!navController.popBackStack()) finish() },
                                onAccount = { startActivity(Intent(this@HomeActivity, AccountActivity::class.java)) },
                                onLogout = {
                                    loginManager.logout()
                                    this@HomeActivity.restartApp()
                                },
                                onRefresh = if (curDestination != EventsDestination.argsRoute && curDestination != EventDestination.argsRoute) null
                                            else { -> refresh = true})
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
                                        startIntent = Intent(this@HomeActivity, SoloRunActivity::class.java)
                                        startIntent?.putExtra(RUN_ID_KEY, Run.UNKNOWN_ID)
                                        if (hasLocationPermission()) startActivity(startIntent)
                                        else launcher.launch(PERMISSIONS)
                                    },
                                    onGroupRun = {
                                        startIntent = Intent(this@HomeActivity, GroupRunEntryActivity::class.java)
                                        if (hasLocationPermission()) startActivity(startIntent)
                                        else launcher.launch(PERMISSIONS)
                                    },
                                    onEvent = { navController.navigate(EventsDestination.argsRoute)},
                                    modifier = Modifier.fillMaxSize())
                            }

                            composable(route = StatsDestination.argsRoute) {
                                Text(text = "TODO")
                            }

                            composable(route = EventsDestination.argsRoute) {

                                val vm: EventsViewModel = hiltViewModel()

                                LaunchedEffect(key1 = refresh) {
                                    if (refresh) {
                                        refresh = false
                                        vm.reload()
                                    }
                                }
                                Box(modifier = Modifier.fillMaxSize()) {
                                    EventsScreen(
                                        followedEvents = vm.followedEvents,
                                        searchEvents = vm.otherEvents,
                                        onClick = {
                                            navController.navigate(EventDestination.baseRoute + it._id)
                                        },
                                        onSearch = {
                                            vm.search(it)
                                        },
                                        onCreate = {
                                            navController.navigate(CreateEventDestination.argsRoute)
                                        },
                                        modifier = Modifier.fillMaxSize())
                                    if (vm.state == EventsViewModel.State.LOADING) LoadingScreen()
                                }


                            }
                            composable(route = CreateEventDestination.argsRoute) {

                                val vm: CreateEventViewModel = hiltViewModel()

                                Box(modifier = Modifier.fillMaxSize()) {
                                    CreateEventScreen(
                                        onCreate = { name, desc, time, image ->
                                            vm.create(name, desc, time, image)
                                        },
                                        errorMessage = vm.error,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(20.dp))
                                    if (vm.state == CreateEventViewModel.State.WAITING) LoadingScreen()
                                    else if (vm.state == CreateEventViewModel.State.SUCCESS) {
                                        StandardDialog(
                                            text = "Your event is now public! You can see it in the 'Followed events' section of the Events screen.",
                                            onDismiss = { navController.popBackStack() },
                                            onOk = {navController.popBackStack()})
                                    }
                                }
                                
                            }
                            composable(route = EventDestination.argsRoute, arguments = listOf(
                                navArgument(name = EventDestination.arg) {type = NavType.StringType}
                            )) {

                                val vm: EventViewModel = hiltViewModel()

                                LaunchedEffect(key1 = refresh) {
                                    if (refresh) {
                                        refresh = false
                                        vm.reload()
                                    }
                                }

                                if (vm.state == EventViewModel.State.ERROR) ErrorScreen(message = "Can't connect. Check your internet connection")
                                else if (vm.state == EventViewModel.State.LOADING) LoadingScreen()
                                else EventScreen(
                                    event = vm.event,
                                    onJoin = {
                                        startIntent = Intent(this@HomeActivity, EventRunActivity::class.java)
                                        startIntent?.putExtra(EVENT_ID_KEY, vm.event._id)
                                        if (hasLocationPermission()) startActivity(startIntent)
                                        else launcher.launch(PERMISSIONS)
                                    },
                                    onFollow = vm::follow,
                                    onUnfollow = vm::unfollow
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}