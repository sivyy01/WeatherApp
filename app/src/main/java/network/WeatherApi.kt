package com.example.weatherapp.network

import com.example.weatherapp.data.WeatherResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast.json")
    suspend fun getWeather(
        @Query("key") apiKey: String = "fa8b3df74d4042b9aa7135114252304",
        @Query("q") location: String,
        @Query("days") days: Int = 3
    ): WeatherResponse
}

object WeatherApi {
    val service: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}
