package com.example.weatherapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.weatherapp.data.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val state by weatherViewModel.state.collectAsState()

    Surface(modifier = modifier.fillMaxSize()) {
        when (state) {
            is WeatherState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is WeatherState.Error -> {
                val message = (state as WeatherState.Error).message
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ошибка: $message")
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = { weatherViewModel.fetchWeather() }) {
                            Text("Повторить")
                        }
                    }
                }
            }
            is WeatherState.Success -> {
                val data = (state as WeatherState.Success).data
                WeatherContent(data = data, onRetry = { weatherViewModel.fetchWeather() })
            }
        }
    }
}

@Composable
private fun WeatherContent(data: WeatherResponse, onRetry: () -> Unit) {
    // iOS-like soft gradient background
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFFEDF7FF), Color(0xFFE6F0FF))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(16.dp)
    ) {
        CurrentCard(location = data.location, current = data.current)

        Spacer(Modifier.height(16.dp))

        // Hourly row
        Text("Почасовой прогноз", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        HourlyRow(hours = data.forecast.forecastday.firstOrNull()?.hour ?: emptyList())

        Spacer(Modifier.height(16.dp))

        Text("Прогноз на 3 дня", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(data.forecast.forecastday) { day ->
                DayItem(day = day)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun CurrentCard(location: Location, current: Current) {
    val cardBg = Brush.horizontalGradient(listOf(Color.White, Color(0xFFFAFCFF)))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, shape = MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .background(cardBg)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = location.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(Modifier.height(4.dp))
                Text(text = current.condition.text, style = MaterialTheme.typography.bodyMedium)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "${current.temp_c.toInt()}°", fontSize = 36.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                AsyncImage(
                    model = "https:${current.condition.icon}",
                    contentDescription = current.condition.text,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(Modifier.height(4.dp))
                Text("Ветер ${current.wind_kph} км/ч", style = MaterialTheme.typography.labelSmall)
                Text("Влажность ${current.humidity}%", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun HourlyRow(hours: List<Hour>) {
    // We show first N hours of current day (e.g., 12)
    val visibleHours = hours
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(visibleHours) { hour ->
            HourItem(hour = hour)
        }
    }
}

@Composable
private fun HourItem(hour: Hour) {
    Surface(
        shape = CircleShape,
        tonalElevation = 2.dp,
        modifier = Modifier.size(width = 92.dp, height = 120.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = formatHour(hour.time), fontSize = 14.sp)
            AsyncImage(model = "https:${hour.condition.icon}", contentDescription = hour.condition.text, modifier = Modifier.size(40.dp))
            Text(text = "${hour.temp_c.toInt()}°", fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun DayItem(day: ForecastDay) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = dayOfWeek(day.date), fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(text = day.day.condition.text, style = MaterialTheme.typography.bodySmall)
            }

            AsyncImage(model = "https:${day.day.condition.icon}", contentDescription = day.day.condition.text, modifier = Modifier.size(44.dp))

            Spacer(Modifier.width(12.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text("${day.day.maxtemp_c.toInt()}°", fontWeight = FontWeight.SemiBold)
                Text("${day.day.mintemp_c.toInt()}°", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

/** --- Helpers --- */
private fun formatHour(time: String): String {
    // time example: "2025-11-25 14:00" — return "14:00"
    return time.split(" ").lastOrNull() ?: time
}

private fun dayOfWeek(dateStr: String): String {
    return try {
        val dt = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
        dt.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale("ru"))
    } catch (e: Exception) {
        dateStr
    }
}

