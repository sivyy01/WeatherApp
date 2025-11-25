package com.example.weatherapp.data

// Корневая модель ответа от API
data class WeatherResponse(
    val location: Location,
    val current: Current,
    val forecast: Forecast
)

// Локация
data class Location(
    val name: String,
    val region: String,
    val country: String,
    val localtime: String
)

// Текущая погода
data class Current(
    val temp_c: Float,
    val condition: Condition,
    val wind_kph: Float,
    val humidity: Int
)

// Условие погоды (текст + иконка)
data class Condition(
    val text: String,
    val icon: String
)

// Прогноз
data class Forecast(
    val forecastday: List<ForecastDay>
)

// День прогноза
data class ForecastDay(
    val date: String,
    val day: Day,
    val hour: List<Hour>
)

// Информация за день
data class Day(
    val maxtemp_c: Float,
    val mintemp_c: Float,
    val condition: Condition
)

// Почасовой прогноз
data class Hour(
    val time: String,
    val temp_c: Float,
    val condition: Condition
)
