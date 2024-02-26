package com.example.racepal

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState


class MapState {
    private val _centered: MutableState<Boolean> = mutableStateOf(true)
    val cameraPositionState: CameraPositionState = CameraPositionState()
    fun centeredAsState(): State<Boolean> = _centered

    fun adjustCamera(pathPoint: PathPoint, zoom: Float? = null) {
        if (_centered.value)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(pathPoint.toLatLng(), if (zoom == null) cameraPositionState.position.zoom else zoom)
    }
    fun centerToggle() {
        _centered.value = !_centered.value
    }
}