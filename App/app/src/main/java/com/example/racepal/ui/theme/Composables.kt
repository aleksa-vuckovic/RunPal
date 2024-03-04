package com.example.racepal.ui.theme

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.racepal.Destination
import com.example.racepal.activities.MainActivity
import com.example.racepal.lightness


@Composable
fun StandardButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable ()->Unit) {
    OutlinedButton(onClick = onClick,
        colors = ButtonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor = MaterialTheme.colorScheme.background.lightness(0.4f),
            disabledContentColor = MaterialTheme.colorScheme.background.lightness((0.4f))
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground),
        modifier = modifier
    ) {
        content()
    }
}

@Composable
fun StandardTextField(value: String, onChange: (String) -> Unit, visualTransformation: VisualTransformation = VisualTransformation.None, modifier: Modifier = Modifier) {

    TextField(value = value, onValueChange = onChange, modifier = modifier,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
            focusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        visualTransformation = visualTransformation
    )
}

@Composable
fun StandardNavBar(destinations: List<Destination>, curDestination: String, onClick: (Destination) -> Unit) {
    NavigationBar(

    ) {
        for (dest in destinations)
            NavigationBarItem(
                selected = curDestination == dest.argsRoute,
                onClick = {onClick(dest)},
                icon = {
                    if (dest.icon != null) Icon(imageVector = dest.icon!!, contentDescription = dest.title)
                },
                label = {
                    if (dest.label != null) Text(text = dest.label!!)
                },
                colors = NavigationBarItemDefaults.colors()
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardTopBar(onBack: () -> Unit, onAccount: () -> Unit, onLogout: () -> Unit, title: String = "") {
    //val height = 50.dp
    TopAppBar(
        title = { Text(text = title)},
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            var expanded by remember {
                mutableStateOf(false)
            }

            IconButton(onClick = {expanded = !expanded}) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
            DropdownMenu(expanded = expanded,
                onDismissRequest = { expanded = !expanded },
                //offset = DpOffset(x = 0.dp, y = height)
                ) {
                DropdownMenuItem(
                    text = { Text("Account")},
                    onClick = onAccount)
                HorizontalDivider(modifier = Modifier.fillMaxSize())
                DropdownMenuItem(
                    text = { Text("Logout") },
                    onClick = onLogout
                )
            }

        },
        //modifier = Modifier.height(height)
    )
}

@Composable
fun StandardOutlinedTextField(value: String,
                              onChange: (String) -> Unit,
                              minLines: Int = 3,
                              shape: Shape = RoundedCornerShape(30.dp),
                              modifier: Modifier = Modifier) {

    OutlinedTextField(value = value,
        onValueChange = onChange,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.secondary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
        ),
        minLines = minLines,
        modifier = modifier,
        shape = shape
    )
}




//@Preview(showBackground = true)
@Composable
fun designPreview() {
    RacePalTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            StandardButton(onClick = { /*TODO*/ }) {
                Text("Button")
            }
        }
    }
}

