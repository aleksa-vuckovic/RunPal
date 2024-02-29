package com.example.racepal.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.racepal.activities.login.RegisterScreen
import com.example.racepal.ui.theme.RacePalTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var loginClient: LoginClient

    override fun onCreate(savedInstanceState: Bundle?) {
        val launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
            if (granted.size != 2) this@MainActivity.finish()
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))

        /*
        lifecycleScope.launch {
            delay(1000)
            val result = loginClient.test("Some test data")
            Log.d("SERVER TEST", result.toString())
        }*/

        super.onCreate(savedInstanceState)
        setContent {
            RacePalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RegisterScreen(onRegister = { _, _, _, _, _, _ -> }, errorMessage = "", modifier = Modifier.fillMaxSize())
                    /*
                    Column(modifier = Modifier.fillMaxSize()) {


                        
                        Button(onClick = {
                            val intent = Intent(this@MainActivity, SoloRunningActivity::class.java)
                            startActivity(intent)
                        }) {
                            Text(text = "GO")
                        }
                     
                    }
                    
                     */
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

