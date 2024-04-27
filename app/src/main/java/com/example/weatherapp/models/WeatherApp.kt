package com.example.weatherapp.models

import com.example.weatherapp.models.Clouds
import com.example.weatherapp.models.Coord
import com.example.weatherapp.models.Main
import com.example.weatherapp.models.Sys
import com.example.weatherapp.models.Weather
import com.example.weatherapp.models.Wind

data class WeatherApp(
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Int,
    val id: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
)