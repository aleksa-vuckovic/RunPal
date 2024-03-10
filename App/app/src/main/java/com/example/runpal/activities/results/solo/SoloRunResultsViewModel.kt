package com.example.runpal.activities.results.solo

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runpal.RUN_ID_KEY
import com.example.runpal.ServerException
import com.example.runpal.models.RunData
import com.example.runpal.models.User
import com.example.runpal.repositories.LoginManager
import com.example.runpal.repositories.run.CombinedRunRepository
import com.example.runpal.repositories.user.CombinedUserRepository
import com.example.runpal.tryRepeat
import com.example.runpal.ui.PathChartDataset
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.runpal.R

@HiltViewModel
class SoloRunResultsViewModel @Inject constructor(
    private val userRepository: CombinedUserRepository,
    private val runRepository: CombinedRunRepository,
    private val loginManager: LoginManager,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
): ViewModel() {
    enum class State {
        LOADING, LOADED, ERROR
    }
    private val _state = mutableStateOf(State.LOADING)
    private val _user = mutableStateOf(User())
    private val _run = mutableStateOf(RunData())
    private val _speedDataset = mutableStateOf(PathChartDataset.EMPTY)
    private val _kcalDataset = mutableStateOf(PathChartDataset.EMPTY)
    private val _altitudeDataset = mutableStateOf(PathChartDataset.EMPTY)
    private val _distanceDataset = mutableStateOf(PathChartDataset.EMPTY)
    private val _mapMin = mutableStateOf(LatLng(0.0, 0.0))
    private val _mapMax = mutableStateOf(LatLng(0.0,0.0))

    val state: State
        get() = _state.value
    val user: User
        get() = _user.value
    val run: RunData
        get() = _run.value
    val speedDataset: PathChartDataset
        get() = _speedDataset.value
    val kcalDataset: PathChartDataset
        get() = _kcalDataset.value
    val altitudeDataset: PathChartDataset
        get() = _altitudeDataset.value
    val distanceDataset: PathChartDataset
        get() = _distanceDataset.value
    val mapMin: LatLng
        get() = _mapMin.value
    val mapMax: LatLng
        get() = _mapMax.value

    val runID: Long = savedStateHandle[RUN_ID_KEY]!!
    val email = loginManager.currentUser()!!

    init {
        reload()
    }

    fun reload() {
        _state.value = State.LOADING
        viewModelScope.launch {
            try {
                _user.value = tryRepeat { userRepository.getUser(email) }
                _run.value = tryRepeat { runRepository.getUpdate(user = email, id = runID) }
                _speedDataset.value = PathChartDataset(data = _run.value.path, xValue = {it.time.toDouble()}, yValue = {it.speed})
                _kcalDataset.value = PathChartDataset(data = _run.value.path, xValue = {it.time.toDouble()}, yValue = {it.kcal})
                _altitudeDataset.value = PathChartDataset(data = _run.value.path, xValue = {it.time.toDouble()}, yValue = {it.altitude})
                _distanceDataset.value = PathChartDataset(data = _run.value.path, xValue = {it.time.toDouble()}, yValue = {it.distance})
                val minLat = _run.value.path.minOf { it.latitude }
                val maxLat = _run.value.path.maxOf { it.latitude }
                val minLng = _run.value.path.minOf { it.longitude }
                val maxLng = _run.value.path.maxOf { it.longitude }
                _mapMin.value = LatLng(minLat, minLng)
                _mapMax.value = LatLng(maxLat, maxLng)
                _state.value = State.LOADED
            } catch(e: ServerException) {
                e.printStackTrace()
                _state.value = State.ERROR
            } catch(e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, context.resources.getString(R.string.no_internet_message), Toast.LENGTH_SHORT).show()
                _state.value = State.ERROR
            }

        }
    }
}