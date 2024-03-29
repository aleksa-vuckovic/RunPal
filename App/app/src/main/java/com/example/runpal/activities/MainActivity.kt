package com.example.runpal.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.runpal.LoadingDots
import com.example.runpal.MetricDistanceFormatter
import com.example.runpal.R
import com.example.runpal.activities.home.HomeActivity
import com.example.runpal.activities.login.LoginActivity
import com.example.runpal.repositories.LoginManager
import com.example.runpal.activities.running.PanelText
import com.example.runpal.risingDoubleAsState
import com.example.runpal.models.PathPoint
import com.example.runpal.repositories.run.CombinedRunRepository
import com.example.runpal.room.PathDao
import com.example.runpal.room.RunDao
import com.example.runpal.room.SyncDao
import com.example.runpal.ui.theme.RunPalTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var loginManager: LoginManager
    @Inject
    lateinit var combinedRunRepository: CombinedRunRepository

    @Inject
    lateinit var runDao: RunDao
    @Inject
    lateinit var pathDao: PathDao
    @Inject
    lateinit var syncDao: SyncDao


    override fun onStart() {
        super.onStart()

        lifecycleScope.launch {

            runDao.deleteAll()
            pathDao.deleteAll()
            syncDao.deleteAll()


            //combinedRunRepository.attemptSyncAll()
            try {
                //Try to reuse an existing JWT
                loginManager.refresh()
                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
            } catch(e: Exception) {
                e.printStackTrace()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }

        }




    }


    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RunPalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(painter = painterResource(id = R.drawable.runner),
                            contentDescription = "Loading",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(140.dp))
                        LoadingDots(size = 30.dp, count = 3, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 30.dp))
                    }

                }
            }
        }
    }
}

@Composable
fun myAnimateFloat(newgoal: Float): State<Float> {
    val state = remember {
        mutableStateOf(newgoal)
    }
    val goal = remember {
        mutableStateOf(newgoal)
    }
    goal.value = newgoal

    LaunchedEffect(key1 = null) {
        while (true) {
            delay(1000)
            state.value += (goal.value - state.value) / 2
        }
    }
    return state
}


val time = System.currentTimeMillis()
val inc = 10000L

val path = listOf(  PathPoint(altitude = 100.0, time = time, speed =  5.0, distance = 0.0, kcal = 0.0, end = false),
    PathPoint(altitude = 101.0, time = time + inc , speed =  5.0, distance = 0.0, kcal = 0.0, end = false),
    PathPoint(altitude = 102.0, time = time + 2*inc, speed =  5.0, distance = 0.0, kcal = 0.0, end = false),
    PathPoint(altitude = 103.0, time = time + 3*inc, speed =  5.0, distance = 0.0, kcal = 0.0, end = false),
    PathPoint(altitude = 105.0, time = time + 4*inc, speed =  5.0, distance = 0.0, kcal = 0.0, end = true),
    PathPoint(altitude = 105.0, time = time + 5*inc, speed =  5.0, distance = 0.0, kcal = 0.0, end = false),
    PathPoint(altitude = 105.0, time = time + 6*inc, speed =  5.0, distance = 0.0, kcal = 0.0, end = false),
    PathPoint(altitude = 104.0, time = time + 7*inc, speed =  5.0, distance = 0.0, kcal = 0.0, end = false),
    PathPoint(altitude = 103.0, time = time + 8*inc, speed =  5.0, distance = 0.0, kcal = 0.0, end = true)
)

val path2 = List<PathPoint>(100) {
    PathPoint(altitude = 100.0 + Math.sin(it.toDouble()/10)*5, time = time + it*inc, end = false)
}
val path3 = List<PathPoint>(100) {
    PathPoint(altitude = 100.0 + Math.sin(it.toDouble()/10 + 1)*5, time = time + it*inc, end = false)
}