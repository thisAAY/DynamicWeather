package today.thisaay.dynamicweather.domain.repository

import today.thisaay.dynamicweather.domain.model.WeatherData

interface WeatherRepository {
    /** Fetch current weather for the given city name. */
    suspend fun getWeather(city: String): Result<WeatherData>
}
