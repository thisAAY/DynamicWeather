package today.thisaay.dynamicweather.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Root response from OpenWeatherMap current weather endpoint. */
@Serializable
data class WeatherResponseDto(
    val name: String,
    val sys: SysDto,
    val main: MainDto,
    val weather: List<WeatherConditionDto>,
    val wind: WindDto,
    val clouds: CloudsDto,
    val visibility: Int = 0,
)

@Serializable
data class SysDto(
    val country: String = "",
)

@Serializable
data class MainDto(
    val temp: Double,
    @SerialName("feels_like") val feelsLike: Double,
    val humidity: Int,
    val pressure: Int,
)

@Serializable
data class WeatherConditionDto(
    val description: String,
    val icon: String,
)

@Serializable
data class WindDto(
    val speed: Double,
)

@Serializable
data class CloudsDto(
    val all: Int,
)
