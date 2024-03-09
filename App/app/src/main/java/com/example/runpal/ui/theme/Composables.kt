package com.example.runpal.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.runpal.Destination
import com.example.runpal.lightness


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
fun StandardTextField(value: String,
                      onChange: (String) -> Unit,
                      visualTransformation: VisualTransformation = VisualTransformation.None,
                      minLines: Int = 1,
                      enabled: Boolean = true,
                      modifier: Modifier = Modifier) {

    TextField(value = value, onValueChange = onChange, modifier = modifier,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
            focusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTextColor = MaterialTheme.colorScheme.onSurface
        ),
        visualTransformation = visualTransformation,
        minLines = minLines,
        enabled = enabled
    )
}

@Composable
fun StandardNavBar(destinations: List<Destination>, curRoute: String, onClick: (Destination) -> Unit) {
    NavigationBar(

    ) {
        for (dest in destinations)
            NavigationBarItem(
                selected = curRoute == dest.argsRoute,
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
fun StandardTopBar(onBack: (() -> Unit)? = null,
                   onRefresh: (() -> Unit)? = null,
                   onAccount: () -> Unit,
                   onLogout: () -> Unit,
                   title: String? = null) {
    //val height = 50.dp
    TopAppBar(
        title = { Text(text = title ?: "")},
        navigationIcon = {
            if (onBack != null) IconButton(onClick = onBack) {
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
            if (onRefresh != null) IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh"
                )
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
                    onClick = {
                        expanded = false
                        onAccount()
                    })
                HorizontalDivider(modifier = Modifier.fillMaxSize())
                DropdownMenuItem(
                    text = { Text("Logout") },
                    onClick = {
                        expanded = false
                        onLogout()
                    }
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

enum class BadgeType {
    DANGER,
    INFO,
    SUCCESS
}
@Composable
fun StandardBadge(text: String, type: BadgeType = BadgeType.INFO, fontSize: TextUnit? = null) {
    var font = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
    if (fontSize != null) font = font.copy(fontSize = fontSize)

    val bg = if (type == BadgeType.DANGER) LightRed
            else if (type == BadgeType.INFO) LightBlue
            else LightGreen
    Box(modifier = Modifier
        .shadow(elevation = 1.dp, shape = RoundedCornerShape(8.dp))
        .clip(shape = RoundedCornerShape(8.dp))
        .background(color = bg)
        .padding(5.dp)


        ,
        contentAlignment = Alignment.Center
    ) {
        Text(text = text,
            style = font,
            textAlign = TextAlign.Center
        )
    }

}

@Composable
fun StandardDialog(text: String, onDismiss: () -> Unit, onOk: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = TransparentWhite),
        contentAlignment = Alignment.Center) {
        Dialog(onDismissRequest = onDismiss) {
            Column(
                modifier = modifier
                    .width(300.dp)
                    .height(200.dp)
                    .clip(shape = RoundedCornerShape(5.dp))
                    .background(color = MaterialTheme.colorScheme.background)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(2.dp)
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = text,
                    textAlign = TextAlign.Center
                )
                StandardButton(onClick = onOk) {
                    Text(text = "OK")
                }
            }
        }
    }
}

@Composable
fun StandardSpinner(values: List<String>, selected: String, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    var expanded: Boolean by rememberSaveable {
        mutableStateOf(false)
    }
    Box() {
        OutlinedButton(onClick = { expanded = !expanded },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Text(text = selected)
            Spacer(modifier = Modifier.weight(1f))
            Icon(imageVector = if (expanded) Icons.Default.ExpandLess
            else Icons.Default.ExpandMore,
                contentDescription = "Expand")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            for (value in values) DropdownMenuItem(text = { Text(text = value) }, onClick = { onSelect(value); expanded = false })
        }
    }
}



//@Preview(showBackground = true)
@Composable
fun designPreview() {
    RunPalTheme {
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

