package today.thisaay.dynamicweather.domain.model

/** Domain model representing current weather for a city. */
data class WeatherData(
    val city: String,
    val country: String,
    val temperatureCelsius: Double,
    val feelsLikeCelsius: Double,
    val description: String,
    val iconCode: String,
    val humidity: Int,
    val windSpeedMps: Double,
    val pressureHpa: Int,
    val visibilityMeters: Int,
    val cloudCoverPercent: Int,
)
