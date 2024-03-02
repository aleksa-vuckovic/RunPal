package com.example.racepal.server

class GenericResponse<T>(
    var message: String = "",
    var data: T? = null
) {
}