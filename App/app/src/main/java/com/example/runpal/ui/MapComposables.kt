package com.example.runpal.ui

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.runpal.MapState
import com.example.runpal.R
import com.example.runpal.RUN_MARKER_SIZE
import com.example.runpal.activities.running.RunState
import com.example.runpal.models.PathPoint
import com.example.runpal.models.Run
import com.example.runpal.models.toLatLng
import com.example.runpal.ui.theme.DarkPink
import com.example.runpal.ui.theme.DarkPurple
import com.example.runpal.ui.theme.LightGreen
import com.example.runpal.ui.theme.LightRed
import com.example.runpal.ui.theme.YellowGreen
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.ktx.buildGoogleMapOptions

@Composable
fun GoogleMapRun(runStates: List<RunState>,
                 markers: List<BitmapDescriptor>,
                 colors: List<Color>,
                 mapState: MapState,
                 onCenterSwitch: () -> Unit,
                 modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        GoogleMap(
            modifier = modifier,
            cameraPositionState = mapState.cameraPositionState
        ) {
            for (i in runStates.indices) {
                GoogleMapPath(pathPoints = runStates[i].path, color = colors[i])
                val loc = runStates[i].location
                if (loc != PathPoint.NONE) Marker(
                    state = MarkerState(position = loc.toLatLng()),
                    icon = markers[i],
                    anchor = Offset(0.5f, 0.5f)
                )
            }

        }

        IconButton(
            onClick = onCenterSwitch,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 9.dp, bottom = 100.dp)
                .background(
                    color = Color.White.copy(alpha = 0.6f), shape = CircleShape
                )
        ) {
            Icon(
                imageVector = if (mapState.centered) Icons.Default.CropFree else Icons.Default.CenterFocusStrong,
                contentDescription = "Recenter/Uncenter"
            )
        }
    }
}

@Composable
fun PathEndMarker(latLng: LatLng, color: Color) {
    val desc = remember(color) {
        val res =
            if (color == DarkPink) R.drawable.darkpink
            else if (color == DarkPurple) R.drawable.darkpurple
            else if (color == Color.Green) R.drawable.green
            else if (color == Color.Red) R.drawable.red
            else if (color == YellowGreen) R.drawable.yellowgreen
            else R.drawable.darkblue
        BitmapDescriptorFactory.fromResource(res)
    }
    Marker(
        state = MarkerState(position = latLng),
        icon = desc,
        anchor = Offset(0.5f, 0.5f)
    )
}

@Composable
fun GoogleMapPath(pathPoints: List<PathPoint>, startColor: Color = Color.Green, color: Color, endColor: Color = Color.Red) {
    for (i in 0..pathPoints.size-2) {
        if (pathPoints[i].end || pathPoints[i+1].end) PathEndMarker(latLng = pathPoints[i+1].toLatLng(), color = if (i == pathPoints.size - 2) endColor else color)
        if (!pathPoints[i].end) Polyline(points = listOf(pathPoints[i].toLatLng(), pathPoints[i+1].toLatLng()), color = color, width = 10f, visible = true)
    }
    if (pathPoints.isNotEmpty()) PathEndMarker(latLng = pathPoints[0].toLatLng(), color = startColor)
}