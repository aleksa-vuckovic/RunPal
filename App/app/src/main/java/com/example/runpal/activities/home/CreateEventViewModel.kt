package com.example.runpal.activities.home

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runpal.ServerException
import com.example.runpal.repositories.ServerEventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val serverEventRepository: ServerEventRepository
) : ViewModel() {

    enum class State {
        INPUT, WAITING, SUCCESS
    }

    private val _state = mutableStateOf(State.INPUT)
    private val _error = mutableStateOf("")

    val state: State
        get() = _state.value
    val error: String
        get() = _error.value


    fun create(name: String, description: String, time: Long?, image: Uri?) {
        if (time == null) {
            _error.value = "Time must be specified."
            return
        }
        _state.value = State.WAITING
        viewModelScope.launch {
            try {
                serverEventRepository.create(name = name, description = description, time = time, image = image)
                _state.value = State.SUCCESS
            } catch(e: ServerException) {
                _error.value = e.message ?: ""
                _state.value = State.INPUT
            } catch(e: Exception) {
                _error.value = "Check your internet connection and try again."
                _state.value = State.INPUT
            }


        }

    }

}