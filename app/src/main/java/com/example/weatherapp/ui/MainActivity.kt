package com.example.weatherapp.ui

import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import com.example.weatherapp.api.ApiInterface
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.models.WeatherApp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Bishkek")
        searchCity()
        hideLayout()

        binding.scrollSearchCard.setOnClickListener {
            hideLayout()
        }
        binding.btnShow.setOnClickListener {
            showLayout()
        }
    }

    private fun hideLayout() {
        binding.scrollSearchCard.animate()
            .translationY(binding.scrollSearchCard.height.toFloat())
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                binding.scrollSearchCard.visibility = View.GONE
                binding.btnShow.visibility = View.VISIBLE
            }
            .start()
        binding.btnShow.visibility = View.VISIBLE
        binding.txtDesign.visibility = View.VISIBLE
    }

    fun showLayout() {
        binding.scrollSearchCard.visibility = View.VISIBLE
        binding.scrollSearchCard.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(300)
            .start()
        binding.btnShow.visibility = View.INVISIBLE
        binding.txtDesign.visibility = View.INVISIBLE
    }

    private val appId: String = "3e26d337e9ee09404f0636b2ae165a98"

    private fun date(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date(timestamp * 1000)))
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp)))
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName, appId, "metric")

        response.enqueue(object: Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min

                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val pressure = responseBody.main.pressure

                    binding.cityName.text = cityName.capitalize()
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date(System.currentTimeMillis())

                    binding.temp.text = "$temperature °С"
                    binding.maxTemp.text = "Max Temp: $maxTemp °С"
                    binding.minTemp.text = "Min Temp: $minTemp °С"

                    binding.humidity.text = "$humidity %"
                    binding.wind.text = "$windSpeed m/s"
                    binding.weather.text = "$condition"
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.sunset.text = "${time(sunSet)}"
                    binding.pressure.text = "$pressure hPa"
                    binding.condition.text = "$condition"

                    changeImagesAccordingToWeatherCondition(condition)
                }
                else {
                    Toast.makeText(applicationContext, "Failed to fetch weather data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Toast.makeText(applicationContext, "Failed to fetch weather data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun changeImagesAccordingToWeatherCondition(conditions: String) {
        when (conditions) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy", "Haze" -> {
                binding.root.setBackgroundResource(R.drawable.cloud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain", "Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow", "Moderate Snow", "Blizzard", "Heavy Snow", "Snow" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }

        binding.lottieAnimationView.playAnimation()
    }
}