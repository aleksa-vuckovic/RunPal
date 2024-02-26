package com.example.racepal.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.racepal.ProgressFloatingButton
import com.example.racepal.ui.theme.RacePalTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
            if (granted.size != 2) this@MainActivity.finish()
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))

        super.onCreate(savedInstanceState)
        setContent {
            RacePalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {


                        Button(onClick = {
                            val intent = Intent(this@MainActivity, SoloRunningActivity::class.java)
                            startActivity(intent)
                        }) {
                            Text(text = "GO")
                        }


                        ProgressFloatingButton(
                            onProgress = {},
                            time = 3000,
                            color = Color.Green,
                            modifier = Modifier
                                .padding(20.dp)
                                .size(200.dp)) {
                            Text("Start")
                        }


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
        while(true) {
            delay(1000)
            state.value += (goal.value-state.value)/2
        }
    }
    return state
}

