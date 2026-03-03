package today.thisaay.dynamicweather.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/** Ktor-based client for the OpenWeatherMap current weather API. */
class WeatherApiService(private val apiKey: String) {

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    /**
     * Fetches current weather data for [city].
     * @throws Exception on network or API error.
     */
    suspend fun fetchWeather(city: String): WeatherResponseDto {
        return client.get("https://api.openweathermap.org/data/2.5/weather") {
            parameter("q", city)
            parameter("appid", apiKey)
            parameter("units", "metric")
        }.body()
    }
}
