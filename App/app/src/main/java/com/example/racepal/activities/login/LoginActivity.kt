package com.example.racepal.activities.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.racepal.IntelligibleException
import com.example.racepal.repositories.LoginManager
import com.example.racepal.activities.home.HomeActivity
import com.example.racepal.repositories.RoomUserRepository
import com.example.racepal.repositories.ServerUserRespository
import com.example.racepal.ui.theme.RacePalTheme
import com.example.racepal.ui.theme.StandardNavBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {


    @Inject
    lateinit var loginManager: LoginManager
    @Inject
    lateinit var serverUserRespository: ServerUserRespository
    @Inject
    lateinit var roomUserRepository: RoomUserRepository

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
                    val startDestination = LoginDestination.argsRoute
                    val curDestination = navController.currentBackStackEntryAsState().value?.destination?.route ?: startDestination

                    Scaffold(
                        bottomBar = {
                            StandardNavBar(destinations = destinations, curDestination = curDestination, onClick = {
                                if (curDestination != it.argsRoute) navController.navigate(it.argsRoute)
                            })
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        NavHost(navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier
                                .padding(it)
                                .fillMaxSize()) {
                            composable(route = LoginDestination.argsRoute) {
                                var error by remember {
                                    mutableStateOf("")
                                }
                                val scope = rememberCoroutineScope()
                                LoginScreen(onLogin = { email, password ->
                                    scope.launch {
                                        try {
                                            loginManager.login(email, password)
                                            this@LoginActivity.startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                                        } catch (e: IntelligibleException) {
                                            error = e.message ?: ""
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }, errorMessage = error,
                                    modifier = Modifier.fillMaxSize().padding(20.dp))
                            }
                            composable(route = RegisterDestination.argsRoute) {
                                var error by remember {
                                    mutableStateOf("")
                                }
                                val scope = rememberCoroutineScope()
                                RegisterScreen(onRegister = { email, password, name, last, weight, uri ->
                                    scope.launch {
                                        try {
                                            loginManager.register(email, password, name, last, weight, uri)
                                            val user = serverUserRespository.getUser(email)
                                            roomUserRepository.upsert(user)
                                            this@LoginActivity.startActivity(
                                                Intent(
                                                    this@LoginActivity,
                                                    HomeActivity::class.java
                                                )
                                            )
                                        } catch (e: IntelligibleException) {
                                            error = e.message ?: ""
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }, errorMessage = error,
                                    modifier = Modifier.padding(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

