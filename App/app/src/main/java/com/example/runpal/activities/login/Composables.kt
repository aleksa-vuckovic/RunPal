package com.example.runpal.activities.login

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.runpal.DoubleInput
import com.example.runpal.ImageSelector
import com.example.runpal.Units
import com.example.runpal.borderBottom
import com.example.runpal.ui.theme.StandardButton
import com.example.runpal.ui.theme.StandardTextField

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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val style = MaterialTheme.typography.labelMedium

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Email", style = style, modifier = Modifier.weight(0.3f))
            StandardTextField(value = email, onChange = {email = it}, modifier = Modifier.weight(0.7f))
        }
        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Password", style = style, modifier = Modifier.weight(0.3f))
            StandardTextField(value = password,
                onChange = {password = it},
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.weight(0.7f))
        }
        Spacer(modifier = Modifier.height(30.dp))

        StandardButton(onClick = { onLogin(email, password)}) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(30.dp))

        Text(text = errorMessage, style = style.copy(color = Color.Red))
    }
}


@Composable
fun RegisterScreen(onRegister: (String, String, String, String, Double, Uri?) -> Unit, errorMessage: String, modifier: Modifier = Modifier) {

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
    var weight by rememberSaveable {
        mutableStateOf(80.0)
    }
    var units by rememberSaveable {
        mutableStateOf(Units.METRIC)
    }
    var profile by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val style = MaterialTheme.typography.labelMedium

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Email", style = style, modifier = Modifier.weight(0.3f))
            StandardTextField(value = email, onChange = {email = it}, modifier = Modifier.weight(0.7f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Password", style = style, modifier = Modifier.weight(0.3f))
            StandardTextField(value = password,
                onChange = {password = it},
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.weight(0.7f))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("First name", style = style, modifier = Modifier.weight(0.3f))
            StandardTextField(value = name, onChange = {name = it}, modifier = Modifier.weight(0.7f))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Last name", style = style, modifier = Modifier.weight(0.3f))
            StandardTextField(value = last, onChange = {last = it}, modifier = Modifier.weight(0.7f))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Weight", style = style, modifier = Modifier.weight(0.3f))
            Row(modifier = Modifier.weight(0.7f),
                horizontalArrangement = Arrangement.End) {
                DoubleInput(initial = 80.0, onChange = {weight = it}, modifier = Modifier.width(150.dp))
                Box(modifier = Modifier
                    .size(55.dp)
                    .clickable { units.next }
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
                    .borderBottom(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)) {
                    Text(text = if (units == Units.IMPERIAL) "lb" else "kg",
                        style = style,
                        modifier = Modifier.align(Alignment.Center))
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Profile photo", style = style, modifier = Modifier.weight(0.3f))
            ImageSelector(input = profile, onSelect = {profile = it}, Modifier.size(200.dp))
        }

        StandardButton(onClick = { onRegister(email, password, name, last, units.toStandardWeightInput(weight), profile)})
         {
            Text("Register")
        }
        Text(text = errorMessage, style = style.copy(color = Color.Red))
    }
}
