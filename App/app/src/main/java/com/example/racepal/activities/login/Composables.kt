package com.example.racepal.activities.login

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.racepal.DoubleInput
import com.example.racepal.LB_TO_KG
import com.example.racepal.borderBottom

@Composable
fun LoginScreen(onLogin: (String, String) -> Unit, errorMessage: String, modifier: Modifier = Modifier) {

    var email by rememberSaveable {
        mutableStateOf("")
    }
    var password by rememberSaveable {
        mutableStateOf("")
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val spacing = 10.dp
        val style = MaterialTheme.typography.displaySmall

        Text(text = "Email:", style = style)
        TextField(value = email, onValueChange = {email = it})
        Spacer(modifier = modifier.height(spacing))

        Text(text = "Password:", style = style)
        TextField(value = "*".repeat(password.length), onValueChange = {password = it})
        Spacer(modifier = modifier.height(spacing))

        Button(onClick = { onLogin(email, password)}) {
            Text("Login")
        }
        Text(text = errorMessage, style = style.copy(color = Color.Red))
    }
}


@Composable
fun RegisterScreen(onRegister: (String, String, String, String, Uri?, Double) -> Unit, errorMessage: String, modifier: Modifier = Modifier) {

    var email by rememberSaveable {
        mutableStateOf("")
    }
    var password by rememberSaveable {
        mutableStateOf("")
    }
    var name by rememberSaveable {
        mutableStateOf("")
    }
    var last by rememberSaveable {
        mutableStateOf("")
    }
    var profile by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }
    var weight by rememberSaveable {
        mutableStateOf(0.0)
    }
    var imperial by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val spacing = 10.dp
        val style = MaterialTheme.typography.displaySmall

        Text(text = "Email:", style = style)
        TextField(value = email, onValueChange = {email = it})
        Spacer(modifier = modifier.height(spacing))

        Text(text = "Password:", style = style)
        TextField(value = "*".repeat(password.length), onValueChange = {password = it})
        Spacer(modifier = modifier.height(spacing))

        Text(text = "First name:", style = style)
        TextField(value = name, onValueChange = {name = it})
        Spacer(modifier = modifier.height(spacing))

        Text(text = "Last name:", style = style)
        TextField(value = last, onValueChange = {last = it})
        Spacer(modifier = modifier.height(spacing))

        Text(text = "Profile photo:", style = style)
        ImageSelector(input = profile, onSelect = {profile = it}, Modifier.size(200.dp))
        Spacer(modifier = modifier.height(spacing))

        Text(text = "Weight:", style = style)
        Row {
            DoubleInput(initial = 80.0, onChange = {weight = it}, modifier = Modifier.width(150.dp))
            Box(modifier = Modifier.size(55.dp)
                .clickable { imperial = !imperial }
                .background(color = Color.LightGray)
                .borderBottom(1.dp, MaterialTheme.colorScheme.primary)) {
                Text(text = if (imperial) "lb" else "kg",
                    style = style,
                    modifier = Modifier.align(Alignment.Center))
            }
        }
        Spacer(modifier = modifier.height(spacing))

        Button(onClick = { onRegister(email, password, name, last, profile, if (imperial) weight* LB_TO_KG else weight)}) {
            Text("Register")
        }
        Text(text = errorMessage, style = style.copy(color = Color.Red))
    }
}


@Composable
fun ImageSelector(input: Uri?, onSelect: (Uri?) -> Unit, modifier: Modifier = Modifier) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri?.let {
            onSelect(it)
        }
    }

    Box(
        modifier = modifier
            .clickable {
                launcher.launch("image/*")
            },
        contentAlignment = Alignment.Center
    ) {
        if (input != null) {
            Image(
                painter = rememberImagePainter(data = input),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Icon(
                imageVector = Icons.Default.ImageSearch,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
    }
}