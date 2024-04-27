package com.example.weatherapp.api

import com.example.weatherapp.models.WeatherApp
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("weather")
    fun getWeatherData(
        @Query("q") city: String,
        @Query("appid") appId: String,
        @Query("units") units: String
    ) : Call<WeatherApp>
}