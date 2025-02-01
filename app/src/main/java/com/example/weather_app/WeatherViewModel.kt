package com.example.weather_app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.api.Constants
import com.example.weather_app.api.NetworkResponse
import com.example.weather_app.api.RetrofitInstance
import com.example.weather_app.api.WeatherModel
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val weatherApi = RetrofitInstance.api
    private val weatherResponse = MutableLiveData<NetworkResponse<WeatherModel>>()
    val exposedResponse:LiveData<NetworkResponse<WeatherModel>> = weatherResponse

    fun getData(city: String) {
        Log.i("WeatherViewModel", "getData called with city: $city")
        weatherResponse.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(Constants.apiKey, city)
                if (response.isSuccessful) {
                    Log.i("Response Succesfull", response.body().toString())
                    response.body()?.let {
                        weatherResponse.value = (NetworkResponse.Success(it))
                    }
                } else {
                    Log.i("Error showing Value", response.message())
                    weatherResponse.value = NetworkResponse.Error("Failed to Fetch the Info")
                }
            }
            catch(e: Exception) {
                Log.i("Error showing Value", e.message.toString())
                weatherResponse.value = NetworkResponse.Error("Failed to Fetch the Info")
            }
        }

    }
}