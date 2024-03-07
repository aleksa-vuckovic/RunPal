package com.example.runpal.activities.account

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.runpal.DoubleInput
import com.example.runpal.ImageSelector
import com.example.runpal.Units
import com.example.runpal.borderBottom
import com.example.runpal.models.User
import com.example.runpal.ui.theme.StandardButton
import com.example.runpal.ui.theme.StandardSpinner
import com.example.runpal.ui.theme.StandardTextField

@Composable
fun EditScreen(init: User,
               onUpdate: (String, String, Double, Uri?) -> Unit,
               errorMessage: String,
               preferredUnits: Units = Units.METRIC,
               modifier: Modifier = Modifier) {

    var name by rememberSaveable {
        mutableStateOf(init.name)
    }
    var last by rememberSaveable {
        mutableStateOf(init.last)
    }
    var units by rememberSaveable {
        mutableStateOf(preferredUnits)
    }
    var weight by rememberSaveable {
        mutableStateOf(units.fromStandardWeightInput(init.weight))
    }
    var profile by rememberSaveable {
        mutableStateOf<Uri?>(init.profileUri)
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .background(color = MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.spacedBy(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val style = MaterialTheme.typography.labelMedium

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
                DoubleInput(initial = weight, onChange = {weight = it}, modifier = Modifier.width(150.dp))
                Box(modifier = Modifier
                    .size(55.dp)
                    .clickable { units = units.next }
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
                    .borderBottom(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)) {
                    Text(text = units.weightFormatter.format(weight).second,
                        style = style,
                        modifier = Modifier.align(Alignment.Center)
                    )
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

        StandardButton(onClick = { onUpdate(name, last, units.toStandardWeightInput(weight), profile)})
        {
            Text("Update")
        }
        Text(text = errorMessage, style = style.copy(color = Color.Red))
    }
}


@OptIn(ExperimentalCoilApi::class)
@Composable
fun AccountScreen(user: User, onEdit: () -> Unit, units: Units, onSelectUnits: (Units) -> Unit,  modifier: Modifier) {

    Box(modifier = modifier) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(color = MaterialTheme.colorScheme.surfaceVariant)
                    ) {}
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(color = MaterialTheme.colorScheme.surface)
                    ) {
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(painter = rememberImagePainter(data = user.profileUri),
                        contentDescription = "Profile photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(140.dp)
                            .clip(shape = CircleShape)
                            .border(
                                BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface),
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(30.dp))
                    Text(text = "${user.name} ${user.last}", style = MaterialTheme.typography.titleMedium)
                }

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(text = "Weight: ${user.weight}kg", style = MaterialTheme.typography.bodyLarge)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Preferred units: ", style = MaterialTheme.typography.bodyLarge)
                StandardSpinner(values = Units.values().map { it.name }, selected = units.name, onSelect = {onSelectUnits(Units.valueOf(it))})
            }

        }
        FloatingActionButton(onClick = onEdit,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)) {
                Icon(imageVector = Icons.Default.Edit,
                    contentDescription = "Edit")
        }
    }
}
