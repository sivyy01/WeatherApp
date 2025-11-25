package com.example.weatherapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.network.WeatherApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val data: WeatherResponse) : WeatherState()
    data class Error(val message: String) : WeatherState()
}

class WeatherViewModel : ViewModel() {

    private val _state = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val state: StateFlow<WeatherState> = _state

    init {
        fetchWeather() // Москва по умолчанию
    }

    fun fetchWeather(location: String = "55.7569,37.6151") {
        viewModelScope.launch {
            _state.value = WeatherState.Loading
            try {
                val data = WeatherApi.service.getWeather(location = location)
                _state.value = WeatherState.Success(data)
            } catch (e: Exception) {
                _state.value = WeatherState.Error(e.localizedMessage ?: "Ошибка сети")
            }
        }
    }
}
