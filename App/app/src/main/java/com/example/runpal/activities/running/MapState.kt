package com.example.runpal.activities.running

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.runpal.models.PathPoint
import com.example.runpal.models.toLatLng
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState


class MapState {
    private val _centered: MutableState<Boolean> = mutableStateOf(true)

    val cameraPositionState: CameraPositionState = CameraPositionState(CameraPosition.fromLatLngZoom(
        LatLng(44.8195, 20.4423), 15f
    ))
    val centered: Boolean
        get() = _centered.value

    fun adjustCamera(pathPoint: PathPoint, zoom: Float? = null) {
        if (_centered.value)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(pathPoint.toLatLng(), if (zoom == null) cameraPositionState.position.zoom else zoom)
    }
    fun centerToggle() {
        _centered.value = !_centered.value
    }
}