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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.racepal.ui.theme.StandardButton
import com.example.racepal.ui.theme.StandardTextField

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
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        val style = MaterialTheme.typography.displaySmall

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Email", style = style, modifier = Modifier.weight(0.3f))
            StandardTextField(value = email, onChange = {email = it}, modifier = Modifier.weight(0.7f))
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Password", style = style, modifier = Modifier.weight(0.3f))
            StandardTextField(value = "*".repeat(password.length), onChange = {password = it}, modifier = Modifier.weight(0.7f))
        }

        StandardButton(onClick = { onLogin(email, password)}) {
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
        modifier = modifier.verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val spacing = 10.dp
        val style = MaterialTheme.typography.displaySmall

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Email", style = style, modifier = Modifier.weight(0.3f))
            StandardTextField(value = email, onChange = {email = it}, modifier = Modifier.weight(0.7f))
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Password", style = style, modifier = Modifier.weight(0.3f))
            StandardTextField(value = "*".repeat(password.length), onChange = {password = it}, modifier = Modifier.weight(0.7f))
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("First name", style = style, modifier = Modifier.weight(0.3f))
            StandardTextField(value = name, onChange = {name = it}, modifier = Modifier.weight(0.7f))
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Last name", style = style, modifier = Modifier.weight(0.3f))
            StandardTextField(value = last, onChange = {last = it}, modifier = Modifier.weight(0.7f))
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Weight", style = style, modifier = Modifier.weight(0.3f))
            Row(modifier = Modifier.weight(0.7f),
                horizontalArrangement = Arrangement.End) {
                DoubleInput(initial = 80.0, onChange = {weight = it}, modifier = Modifier.width(150.dp))
                Box(modifier = Modifier
                    .size(55.dp)
                    .clickable { imperial = !imperial }
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
                    .borderBottom(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)) {
                    Text(text = if (imperial) "lb" else "kg",
                        style = style,
                        modifier = Modifier.align(Alignment.Center))
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Profile photo", style = style, modifier = Modifier.weight(0.3f))
            ImageSelector(input = profile, onSelect = {profile = it}, Modifier.size(200.dp))
        }

        StandardButton(onClick = { onRegister(email, password, name, last, profile, if (imperial) weight* LB_TO_KG else weight)})
         {
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