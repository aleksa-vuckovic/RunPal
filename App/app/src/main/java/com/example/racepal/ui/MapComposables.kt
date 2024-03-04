package com.example.racepal.ui

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.racepal.MapState
import com.example.racepal.activities.running.RunState
import com.example.racepal.models.PathPoint
import com.example.racepal.models.toLatLng
import com.example.racepal.ui.theme.LightGreen
import com.example.racepal.ui.theme.LightRed
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline

@Composable
fun GoogleMapRun(runStates: List<RunState>,
                 icons: List<Bitmap>,
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
                val loc = runStates[i].location
                if (loc != PathPoint.NONE) Marker(
                    state = MarkerState(position = loc.toLatLng()),
                    icon = BitmapDescriptorFactory.fromBitmap(icons[i]),
                    anchor = Offset(0.5f, 0.5f)
                )
                GoogleMapPath(pathPoints = runStates[i].path, color = colors[i])
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
    Circle(center = latLng,
        clickable = false,
        fillColor = color.copy(alpha = 0.7f),
        strokeColor = color,
        radius = 20.0,
    )
}

@Composable
fun GoogleMapPath(pathPoints: List<PathPoint>, startColor: Color = LightGreen, color: Color, endColor: Color = LightRed) {
    for (i in 0..pathPoints.size-2) {
        if (pathPoints[i].end || pathPoints[i+1].end) PathEndMarker(latLng = pathPoints[i+1].toLatLng(), color = if (i == pathPoints.size - 2) endColor else color)
        if (!pathPoints[i].end) Polyline(points = listOf(pathPoints[i].toLatLng(), pathPoints[i+1].toLatLng()), color = color, width = 20f, visible = true)
    }
    if (pathPoints.isNotEmpty()) PathEndMarker(latLng = pathPoints[0].toLatLng(), color = startColor)
}