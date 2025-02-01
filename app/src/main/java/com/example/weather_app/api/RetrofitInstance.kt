package com.example.weather_app.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val BASE_URL="https://api.weatherapi.com"
    private fun getInstance(): Retrofit {
       return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api: WeatherAPI = getInstance().create(WeatherAPI::class.java)
}