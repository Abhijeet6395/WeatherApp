package com.example.weather_app.api
//T is Weather Model, using Generic
sealed class NetworkResponse<out T> {
    data class Success<out T>(val value: T) : NetworkResponse<T>()
    data class Error(val message: String? = null) : NetworkResponse<Nothing>()
    object Loading : NetworkResponse<Nothing>()
}