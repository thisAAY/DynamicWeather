package today.thisaay.dynamicweather.presentation.renderer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import today.thisaay.dynamicweather.domain.model.UIComponent
import today.thisaay.dynamicweather.domain.model.WeatherData

// ─── SearchBar ────────────────────────────────────────────────────────────────

@Composable
fun SearchBarComponent(
    component: UIComponent.SearchBar,
    modifier: Modifier = Modifier,
    onAction: (key: String, params: Map<String, String>) -> Unit,
) {
    var query by remember { mutableStateOf("") }

    OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        modifier = modifier,
        placeholder = { Text(component.placeholder) },
        leadingIcon = {
            Icon(iconNameToVector("search"), contentDescription = "Search")
        },
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                if (query.isNotBlank()) {
                    onAction(component.actionKey, mapOf("query" to query))
                    query = ""
                }
            }
        ),
    )
}

// ─── WeatherCard ──────────────────────────────────────────────────────────────

@Composable
fun WeatherCardComponent(
    component: UIComponent.WeatherCard,
    weatherData: WeatherData?,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        when (component.style) {
            "medium" -> WeatherCardMedium(weatherData, component, modifier = Modifier.padding(20.dp))
            "compact" -> WeatherCardCompact(weatherData, component, modifier = Modifier.padding(16.dp))
            "minimal" -> WeatherCardMinimal(weatherData, modifier = Modifier.padding(12.dp))
            else -> WeatherCardLarge(weatherData, component, modifier = Modifier.padding(24.dp))
        }
    }
}

@Composable
private fun WeatherCardLarge(
    weatherData: WeatherData?,
    component: UIComponent.WeatherCard,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column {
                Text(
                    text = weatherData?.city ?: "—",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = weatherData?.country ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                )
            }
            if (component.showIcon && weatherData != null) {
                WeatherIconDisplay(iconCode = weatherData.iconCode, sizeDp = 56)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = weatherData?.let { "%.0f°C".format(it.temperatureCelsius) } ?: "—°C",
            fontSize = 64.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Text(
            text = weatherData?.description ?: "Loading...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
        )
        if (component.showFeelsLike && weatherData != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Feels like %.0f°C".format(weatherData.feelsLikeCelsius),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
private fun WeatherCardMedium(
    weatherData: WeatherData?,
    component: UIComponent.WeatherCard,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = weatherData?.city ?: "—",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            if (component.showIcon && weatherData != null) {
                WeatherIconDisplay(iconCode = weatherData.iconCode, sizeDp = 44)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = weatherData?.let { "%.0f°C".format(it.temperatureCelsius) } ?: "—°C",
            fontSize = 48.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = weatherData?.description ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
            )
            if (component.showFeelsLike && weatherData != null) {
                Text(
                    text = "Feels %.0f°C".format(weatherData.feelsLikeCelsius),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.65f),
                )
            }
        }
    }
}

@Composable
private fun WeatherCardCompact(
    weatherData: WeatherData?,
    component: UIComponent.WeatherCard,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = weatherData?.city ?: "—",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = weatherData?.let { "%.0f°C".format(it.temperatureCelsius) } ?: "—°C",
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = weatherData?.description ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            )
        }
        if (component.showIcon && weatherData != null) {
            WeatherIconDisplay(iconCode = weatherData.iconCode, sizeDp = 48)
        }
    }
}

@Composable
private fun WeatherCardMinimal(
    weatherData: WeatherData?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = weatherData?.let { "${it.city}: %.0f°C".format(it.temperatureCelsius) } ?: "Loading...",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

/** Renders a weather icon as an emoji inside a Box, sized in dp. */
@Composable
fun WeatherIconDisplay(iconCode: String, sizeDp: Int = 48) {
    Box(
        modifier = Modifier.size(sizeDp.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = weatherEmojiForCode(iconCode), fontSize = (sizeDp * 0.6).sp)
    }
}

// ─── WeatherDetails ───────────────────────────────────────────────────────────

@Composable
fun WeatherDetailsComponent(
    component: UIComponent.WeatherDetails,
    weatherData: WeatherData?,
    modifier: Modifier = Modifier,
) {
    val detailItems = component.items.mapNotNull { key ->
        weatherData?.let { buildStatValue(key, it)?.let { v -> key to v } }
    }
    if (detailItems.isEmpty()) return

    val chunked = detailItems.chunked(2)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        chunked.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowItems.forEach { (key, value) ->
                    DetailChip(
                        label = statKeyToLabel(key),
                        value = value,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DetailChip(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ─── TemperatureHero ──────────────────────────────────────────────────────────

@Composable
fun TemperatureHeroComponent(
    component: UIComponent.TemperatureHero,
    weatherData: WeatherData?,
    modifier: Modifier = Modifier,
) {
    when (component.size) {
        "medium" -> TemperatureHeroMedium(component, weatherData, modifier)
        "small" -> TemperatureHeroSmall(component, weatherData, modifier)
        else -> TemperatureHeroLarge(component, weatherData, modifier)
    }
}

@Composable
private fun TemperatureHeroLarge(
    component: UIComponent.TemperatureHero,
    weatherData: WeatherData?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (component.showIcon && weatherData != null) {
            WeatherIconDisplay(iconCode = weatherData.iconCode, sizeDp = 96)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Text(
            text = weatherData?.let { "%.0f°".format(it.temperatureCelsius) } ?: "—°",
            fontSize = 96.sp,
            fontWeight = FontWeight.Thin,
            color = MaterialTheme.colorScheme.onBackground,
        )
        if (component.showCity) {
            Text(
                text = weatherData?.city ?: "—",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
            )
        }
        if (component.showCondition && weatherData != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = weatherData.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
            )
        }
    }
}

@Composable
private fun TemperatureHeroMedium(
    component: UIComponent.TemperatureHero,
    weatherData: WeatherData?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (component.showIcon && weatherData != null) {
                WeatherIconDisplay(iconCode = weatherData.iconCode, sizeDp = 64)
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column {
                Text(
                    text = weatherData?.let { "%.0f°C".format(it.temperatureCelsius) } ?: "—°C",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                if (component.showCondition && weatherData != null) {
                    Text(
                        text = weatherData.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                    )
                }
            }
        }
        if (component.showCity && weatherData != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = weatherData.city,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            )
        }
    }
}

@Composable
private fun TemperatureHeroSmall(
    component: UIComponent.TemperatureHero,
    weatherData: WeatherData?,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        if (component.showIcon && weatherData != null) {
            WeatherIconDisplay(iconCode = weatherData.iconCode, sizeDp = 40)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = weatherData?.let { "%.0f°C".format(it.temperatureCelsius) } ?: "—°C",
            fontSize = 40.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onBackground,
        )
        if (component.showCity && weatherData != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = weatherData.city,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            )
        }
    }
}

// ─── WeatherSummary ───────────────────────────────────────────────────────────

@Composable
fun WeatherSummaryComponent(
    component: UIComponent.WeatherSummary,
    weatherData: WeatherData?,
    modifier: Modifier = Modifier,
) {
    when (component.size) {
        "large" -> WeatherSummaryLarge(component, weatherData, modifier)
        "small" -> WeatherSummarySmall(component, weatherData, modifier)
        else -> WeatherSummaryMedium(component, weatherData, modifier)
    }
}

@Composable
private fun WeatherSummaryLarge(
    component: UIComponent.WeatherSummary,
    weatherData: WeatherData?,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        if (component.showIcon && weatherData != null) {
            WeatherIconDisplay(iconCode = weatherData.iconCode, sizeDp = 56)
            Spacer(modifier = Modifier.width(16.dp))
        }
        Column {
            Text(
                text = weatherData?.city ?: "—",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = weatherData?.let { "%.0f°C".format(it.temperatureCelsius) } ?: "—°C",
                fontSize = 48.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = weatherData?.description ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
            )
        }
    }
}

@Composable
private fun WeatherSummaryMedium(
    component: UIComponent.WeatherSummary,
    weatherData: WeatherData?,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        if (component.showIcon && weatherData != null) {
            WeatherIconDisplay(iconCode = weatherData.iconCode, sizeDp = 40)
            Spacer(modifier = Modifier.width(12.dp))
        }
        Column {
            Text(
                text = weatherData?.city ?: "—",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = weatherData?.let { "%.0f°C".format(it.temperatureCelsius) } ?: "—°C",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = weatherData?.description ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                )
            }
        }
    }
}

@Composable
private fun WeatherSummarySmall(
    component: UIComponent.WeatherSummary,
    weatherData: WeatherData?,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        if (component.showIcon && weatherData != null) {
            WeatherIconDisplay(iconCode = weatherData.iconCode, sizeDp = 24)
            Spacer(modifier = Modifier.width(6.dp))
        }
        val city = weatherData?.city ?: "—"
        val temp = weatherData?.let { "%.0f°C".format(it.temperatureCelsius) } ?: "—"
        val desc = weatherData?.description ?: ""
        Text(
            text = if (desc.isNotBlank()) "$city • $temp • $desc" else "$city • $temp",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

// ─── WeatherStat ──────────────────────────────────────────────────────────────

@Composable
fun WeatherStatComponent(
    component: UIComponent.WeatherStat,
    weatherData: WeatherData?,
    modifier: Modifier = Modifier,
) {
    val value = weatherData?.let { buildStatValue(component.key, it) } ?: "—"
    val icon = statKeyToIcon(component.key)
    val label = statKeyToLabel(component.key)

    when (component.size) {
        "large" -> WeatherStatLarge(
            value = value, icon = icon, label = label,
            showIcon = component.showIcon, showLabel = component.showLabel, modifier = modifier,
        )
        "small" -> WeatherStatSmall(
            value = value, icon = icon,
            showIcon = component.showIcon, modifier = modifier,
        )
        else -> WeatherStatMedium(
            value = value, icon = icon, label = label,
            showIcon = component.showIcon, showLabel = component.showLabel, modifier = modifier,
        )
    }
}

@Composable
private fun WeatherStatLarge(
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    showIcon: Boolean,
    showLabel: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (showIcon) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (showLabel) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                )
            }
        }
    }
}

@Composable
private fun WeatherStatMedium(
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    showIcon: Boolean,
    showLabel: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (showIcon || showLabel) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (showIcon) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    if (showLabel) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun WeatherStatSmall(
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    showIcon: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showIcon) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ─── ConditionBadge ───────────────────────────────────────────────────────────

@Composable
fun ConditionBadgeComponent(
    weatherData: WeatherData?,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.tertiaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (weatherData != null) {
                Text(
                    text = weatherEmojiForCode(weatherData.iconCode),
                    fontSize = 18.sp,
                )
            }
            Text(
                text = weatherData?.description ?: "Loading...",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }
    }
}

// ─── HumidityBar ──────────────────────────────────────────────────────────────

@Composable
fun HumidityBarComponent(
    component: UIComponent.HumidityBar,
    weatherData: WeatherData?,
    modifier: Modifier = Modifier,
) {
    val humidity = weatherData?.humidity ?: 0
    Column(modifier = modifier) {
        if (component.showLabel || component.showValue) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (component.showLabel) {
                    Text(
                        text = "Humidity",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (component.showValue) {
                    Text(
                        text = "$humidity%",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
        LinearProgressIndicator(
            progress = { humidity / 100f },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

// ─── WindDisplay ──────────────────────────────────────────────────────────────

@Composable
fun WindDisplayComponent(
    component: UIComponent.WindDisplay,
    weatherData: WeatherData?,
    modifier: Modifier = Modifier,
) {
    val speedText = weatherData?.let { "%.1f m/s".format(it.windSpeedMps) } ?: "— m/s"
    when (component.size) {
        "large" -> WindDisplayLarge(speedText = speedText, modifier = modifier)
        "small" -> WindDisplaySmall(speedText = speedText, modifier = modifier)
        else -> WindDisplayMedium(speedText = speedText, modifier = modifier)
    }
}

@Composable
private fun WindDisplayLarge(speedText: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = iconNameToVector("air"),
                contentDescription = "Wind",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = speedText,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Wind Speed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
            )
        }
    }
}

@Composable
private fun WindDisplayMedium(speedText: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = iconNameToVector("air"),
                    contentDescription = "Wind",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Wind",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                )
            }
            Text(
                text = speedText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun WindDisplaySmall(speedText: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = iconNameToVector("air"),
            contentDescription = "Wind",
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "💨 $speedText",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

// ─── Badge & Chip ─────────────────────────────────────────────────────────────

@Composable
fun BadgeComponent(
    text: String,
    bgColor: androidx.compose.ui.graphics.Color,
    txtColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = bgColor,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = txtColor,
        )
    }
}

@Composable
fun ChipComponent(
    text: String,
    leadingIconName: String?,
    modifier: Modifier = Modifier,
) {
    AssistChip(
        onClick = {},
        label = { Text(text) },
        leadingIcon = if (leadingIconName != null) {
            {
                Icon(
                    imageVector = iconNameToVector(leadingIconName),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            }
        } else null,
        modifier = modifier,
    )
}

// ─── Shared helpers ───────────────────────────────────────────────────────────

/** Returns the formatted value string for a stat key, or null if key is unknown. */
internal fun buildStatValue(key: String, data: WeatherData): String? = when (key) {
    "humidity" -> "${data.humidity}%"
    "wind_speed" -> "%.1f m/s".format(data.windSpeedMps)
    "pressure" -> "${data.pressureHpa} hPa"
    "feels_like" -> "%.0f°C".format(data.feelsLikeCelsius)
    "visibility" -> "%.1f km".format(data.visibilityMeters / 1000.0)
    "cloud_cover" -> "${data.cloudCoverPercent}%"
    else -> null
}
