package com.example.runpal.activities.running.event

import android.content.Context
import android.graphics.BitmapFactory
import android.location.Location
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runpal.DEFAULT_ZOOM
import com.example.runpal.EVENT_ID_KEY
import com.example.runpal.activities.running.MapState
import com.example.runpal.R
import com.example.runpal.RUN_MARKER_COLORS
import com.example.runpal.RUN_MARKER_SIZE
import com.example.runpal.ServerException
import com.example.runpal.activities.running.LocalRunState
import com.example.runpal.activities.running.LocalRunStateFactory
import com.example.runpal.activities.running.RunState
import com.example.runpal.getMarkerBitmap
import com.example.runpal.models.Event
import com.example.runpal.models.Run
import com.example.runpal.repositories.LoginManager
import com.example.runpal.repositories.ServerEventRepository
import com.example.runpal.tryRepeat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventRunViewModel @Inject constructor(
    private val loginManager: LoginManager,
    private val serverEventRepository: ServerEventRepository,
    private val savedStateHandle: SavedStateHandle,
    private val localRunStateFactory: LocalRunStateFactory,
    @ApplicationContext private val context: Context
): ViewModel() {
    val beep: MediaPlayer? = MediaPlayer.create(context, R.raw.longbeep)

    enum class State {
        LOADING, LOADED, FAILED
    }

    private val _state = mutableStateOf(State.LOADING)
    private val _event = mutableStateOf(Event())
    private val _runState: LocalRunState
    private val _marker: MutableState<BitmapDescriptor>

    val mapState: MapState = MapState()
    val state: State
        get() = _state.value
    val event: Event
        get() = _event.value
    val runState: RunState
        get() = _runState
    val marker: BitmapDescriptor
        get() = _marker.value

    init {
        val user = loginManager.currentUser()!!
        val eventID: String = savedStateHandle[EVENT_ID_KEY]!!
        _runState = localRunStateFactory.createLocalRunState(
            run = Run(user = user, id = Run.UNKNOWN_ID, event = eventID),
            scope = viewModelScope
        )
        _marker = mutableStateOf(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val event = tryRepeat { serverEventRepository.data(eventID) }
                _event.value = event
            } catch(e: ServerException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                _state.value = State.FAILED
                return@launch
            } catch(e: Exception) {
                Toast.makeText(context, "Check your internet connection and rejoin.", Toast.LENGTH_SHORT).show()
                _state.value = State.FAILED
                return@launch
            }
            val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.runner)
                .getMarkerBitmap(RUN_MARKER_SIZE, color = RUN_MARKER_COLORS[0])
            _marker.value = BitmapDescriptorFactory.fromBitmap(bitmap)
            _state.value = State.LOADED
        }
    }

    fun updateLocation(loc: Location) {
        _runState.update(loc)
        mapState.adjustCamera(_runState.location)
        if (_runState.location.distance >= event.distance) end()
    }
    fun start() = _runState.start()

    fun pause() = _runState.pause()
    fun resume() = _runState.resume()
    fun end() = _runState.stop()
    fun centerSwitch() {
        mapState.centerToggle()
        mapState.adjustCamera(_runState.location, DEFAULT_ZOOM)
    }
}