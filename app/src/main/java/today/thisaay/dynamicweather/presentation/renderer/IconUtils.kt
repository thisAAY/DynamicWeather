package today.thisaay.dynamicweather.presentation.renderer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

/** Maps a string icon name (from JSON schema) to a Material [ImageVector]. */
internal fun iconNameToVector(name: String): ImageVector = when (name) {
    "search" -> Icons.Default.Search
    "air" -> Icons.Default.Air
    "water_drop" -> Icons.Default.WaterDrop
    "thermostat" -> Icons.Default.Thermostat
    "visibility" -> Icons.Default.Visibility
    "wb_sunny" -> Icons.Default.WbSunny
    "nights_stay" -> Icons.Default.NightsStay
    "thunderstorm" -> Icons.Default.Thunderstorm
    "grain" -> Icons.Default.Grain
    "ac_unit" -> Icons.Default.AcUnit
    "speed" -> Icons.Default.Speed
    else -> Icons.Default.Cloud
}

/** Maps an OpenWeatherMap icon code (e.g. "01d") to a weather emoji. */
internal fun weatherEmojiForCode(iconCode: String): String = when {
    iconCode.startsWith("01") -> "☀️"
    iconCode.startsWith("02") -> "⛅"
    iconCode.startsWith("03") -> "☁️"
    iconCode.startsWith("04") -> "☁️"
    iconCode.startsWith("09") -> "🌧️"
    iconCode.startsWith("10") -> "🌦️"
    iconCode.startsWith("11") -> "⛈️"
    iconCode.startsWith("13") -> "❄️"
    iconCode.startsWith("50") -> "🌫️"
    else -> "🌡️"
}

/** Maps a weather stat key to a human-readable label. */
internal fun statKeyToLabel(key: String): String = when (key) {
    "humidity" -> "Humidity"
    "wind_speed" -> "Wind Speed"
    "pressure" -> "Pressure"
    "feels_like" -> "Feels Like"
    "visibility" -> "Visibility"
    "cloud_cover" -> "Cloud Cover"
    else -> key.replace("_", " ").replaceFirstChar { it.uppercase() }
}

/** Maps a weather stat key to its Material icon. */
internal fun statKeyToIcon(key: String): ImageVector = when (key) {
    "humidity" -> Icons.Default.WaterDrop
    "wind_speed" -> Icons.Default.Air
    "pressure" -> Icons.Default.Speed
    "feels_like" -> Icons.Default.Thermostat
    "visibility" -> Icons.Default.Visibility
    "cloud_cover" -> Icons.Default.Cloud
    else -> Icons.Default.Thermostat
}
