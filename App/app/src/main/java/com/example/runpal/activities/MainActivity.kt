package com.example.runpal.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.runpal.LoadingDots
import com.example.runpal.repositories.LoginManager
import com.example.runpal.R
import com.example.runpal.RUN_MARKER_SIZE
import com.example.runpal.activities.home.HomeActivity
import com.example.runpal.activities.login.LoginActivity
import com.example.runpal.generateSimpleMarkerBitmap
import com.example.runpal.repositories.run.CombinedRunRepository
import com.example.runpal.room.PathDao
import com.example.runpal.room.RunDao
import com.example.runpal.room.SyncDao
import com.example.runpal.ui.theme.DarkBlue
import com.example.runpal.ui.theme.DarkPink
import com.example.runpal.ui.theme.DarkPurple
import com.example.runpal.ui.theme.DarkYellow
import com.example.runpal.ui.theme.RunPalTheme
import com.example.runpal.ui.theme.YellowGreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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
    /*
        val colors = listOf(DarkBlue, DarkPurple, DarkPink, DarkYellow, YellowGreen, Color.Green, Color.Red)
        val names = listOf("darkblue", "darkpurple", "darkpink", "darkyellow", "yellowgreen", "green", "red")
        for (i in colors.indices) {
            val file = File(filesDir, names[i] + ".png")
            val bitmap = generateSimpleMarkerBitmap(RUN_MARKER_SIZE, colors[i])
            try {
                val fileOutputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                fileOutputStream.close()
                // File saved successfully
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle the exception
            }
        }*/

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
