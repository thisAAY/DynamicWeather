package today.thisaay.dynamicweather.data.repository

import today.thisaay.dynamicweather.data.remote.WeatherApiService
import today.thisaay.dynamicweather.domain.model.WeatherData
import today.thisaay.dynamicweather.domain.repository.WeatherRepository

class WeatherRepositoryImpl(
    private val apiService: WeatherApiService,
) : WeatherRepository {

    override suspend fun getWeather(city: String): Result<WeatherData> = runCatching {
        val dto = apiService.fetchWeather(city)
        WeatherData(
            city = dto.name,
            country = dto.sys.country,
            temperatureCelsius = dto.main.temp,
            feelsLikeCelsius = dto.main.feelsLike,
            description = dto.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "",
            iconCode = dto.weather.firstOrNull()?.icon ?: "01d",
            humidity = dto.main.humidity,
            windSpeedMps = dto.wind.speed,
            pressureHpa = dto.main.pressure,
            visibilityMeters = dto.visibility,
            cloudCoverPercent = dto.clouds.all,
        )
    }
}
