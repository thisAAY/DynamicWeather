package today.thisaay.dynamicweather.presentation.renderer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import today.thisaay.dynamicweather.domain.model.ModifierConfig
import today.thisaay.dynamicweather.domain.model.UIComponent
import today.thisaay.dynamicweather.domain.model.WeatherData
import androidx.compose.foundation.layout.Box as ComposeBox

/**
 * Recursively renders a [UIComponent] tree into Jetpack Compose UI.
 *
 * High-level components automatically bind to the provided [weatherData].
 * When any component fires an action, [onAction] is called with the action key and params.
 */
@Composable
fun DynamicUIRenderer(
    component: UIComponent,
    weatherData: WeatherData?,
    onAction: (key: String, params: Map<String, String>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val base = modifier.applyConfig(component.extractModifier())

    when (component) {

        // ── Low-Level Primitives ─────────────────────────────────────────────

        is UIComponent.Column -> {
            Column(
                modifier = base,
                verticalArrangement = component.verticalArrangement.toVerticalArrangement(),
                horizontalAlignment = component.horizontalAlignment.toHorizontalAlignment(),
            ) {
                component.children.forEach { child ->
                    if (child is UIComponent.Spacer && child.weight != null) {
                        Spacer(modifier = Modifier.weight(child.weight))
                    } else {
                        DynamicUIRenderer(child, weatherData, onAction)
                    }
                }
            }
        }

        is UIComponent.Row -> {
            Row(
                modifier = base,
                horizontalArrangement = component.horizontalArrangement.toHorizontalArrangement(),
                verticalAlignment = component.verticalAlignment.toVerticalAlignment(),
            ) {
                component.children.forEach { child ->
                    if (child is UIComponent.Spacer && child.weight != null) {
                        Spacer(modifier = Modifier.weight(child.weight))
                    } else {
                        DynamicUIRenderer(child, weatherData, onAction)
                    }
                }
            }
        }

        is UIComponent.Box -> {
            ComposeBox(
                modifier = base,
                contentAlignment = component.contentAlignment.toBoxAlignment(),
            ) {
                component.children.forEach { child ->
                    DynamicUIRenderer(child, weatherData, onAction)
                }
            }
        }

        is UIComponent.Card -> {
            Card(
                modifier = base,
                elevation = CardDefaults.cardElevation(defaultElevation = component.elevation.dp),
            ) {
                component.children.forEach { child ->
                    DynamicUIRenderer(child, weatherData, onAction)
                }
            }
        }

        is UIComponent.Text -> {
            Text(
                text = component.text,
                modifier = base,
                style = component.style.toTextStyle(),
                color = component.color?.toComposeColor() ?: Color.Unspecified,
                textAlign = component.textAlign.toTextAlign(),
            )
        }

        is UIComponent.Spacer -> {
            Spacer(modifier = Modifier.height(component.height.dp))
        }

        is UIComponent.Icon -> {
            Icon(
                imageVector = iconNameToVector(component.name),
                contentDescription = component.name,
                modifier = base.size(component.size.dp),
                tint = component.tint?.toComposeColor() ?: MaterialTheme.colorScheme.primary,
            )
        }

        is UIComponent.Divider -> {
            HorizontalDivider(
                modifier = base,
                thickness = component.thickness.dp,
                color = component.color?.toComposeColor()
                    ?: MaterialTheme.colorScheme.outlineVariant,
            )
        }

        is UIComponent.Badge -> {
            val bgColor = component.backgroundColor?.toComposeColor()
                ?: MaterialTheme.colorScheme.tertiaryContainer
            val txtColor = component.textColor?.toComposeColor()
                ?: MaterialTheme.colorScheme.onTertiaryContainer
            BadgeComponent(
                text = component.text,
                bgColor = bgColor,
                txtColor = txtColor,
                modifier = base,
            )
        }

        is UIComponent.Chip -> {
            ChipComponent(
                text = component.text,
                leadingIconName = component.leadingIconName,
                modifier = base,
            )
        }

        is UIComponent.ProgressBar -> {
            LinearProgressIndicator(
                progress = { component.progress.coerceIn(0f, 1f) },
                modifier = base.fillMaxWidth().height(8.dp),
                color = component.color?.toComposeColor() ?: MaterialTheme.colorScheme.primary,
                trackColor = component.trackColor?.toComposeColor()
                    ?: MaterialTheme.colorScheme.surfaceVariant,
            )
        }

        is UIComponent.GradientBox -> {
            val startColor = component.startColor.toComposeColor()
            val endColor = component.endColor.toComposeColor()
            val brush = when (component.direction) {
                "horizontal" -> Brush.horizontalGradient(listOf(startColor, endColor))
                "diagonal" -> Brush.linearGradient(listOf(startColor, endColor))
                else -> Brush.verticalGradient(listOf(startColor, endColor))
            }
            ComposeBox(
                modifier = base
                    .clip(RoundedCornerShape(component.cornerRadius.dp))
                    .background(brush),
            ) {
                component.children.forEach { child ->
                    DynamicUIRenderer(child, weatherData, onAction)
                }
            }
        }

        // ── High-Level Smart Components ──────────────────────────────────────

        is UIComponent.WeatherCard -> {
            WeatherCardComponent(
                component = component,
                weatherData = weatherData,
                modifier = base,
            )
        }

        is UIComponent.SearchBar -> {
            SearchBarComponent(
                component = component,
                modifier = base.fillMaxWidth(),
                onAction = onAction,
            )
        }

        is UIComponent.WeatherDetails -> {
            WeatherDetailsComponent(
                component = component,
                weatherData = weatherData,
                modifier = base,
            )
        }

        is UIComponent.Button -> {
            when (component.style) {
                "outlined" -> OutlinedButton(
                    onClick = { onAction(component.actionKey, emptyMap()) },
                    modifier = base,
                ) { Text(component.text) }
                "text" -> TextButton(
                    onClick = { onAction(component.actionKey, emptyMap()) },
                    modifier = base,
                ) { Text(component.text) }
                else -> Button(
                    onClick = { onAction(component.actionKey, emptyMap()) },
                    modifier = base,
                ) { Text(component.text) }
            }
        }

        is UIComponent.TemperatureHero -> {
            TemperatureHeroComponent(
                component = component,
                weatherData = weatherData,
                modifier = base,
            )
        }

        is UIComponent.WeatherSummary -> {
            WeatherSummaryComponent(
                component = component,
                weatherData = weatherData,
                modifier = base,
            )
        }

        is UIComponent.WeatherStat -> {
            WeatherStatComponent(
                component = component,
                weatherData = weatherData,
                modifier = base,
            )
        }

        is UIComponent.ConditionBadge -> {
            ConditionBadgeComponent(
                weatherData = weatherData,
                modifier = base,
            )
        }

        is UIComponent.HumidityBar -> {
            HumidityBarComponent(
                component = component,
                weatherData = weatherData,
                modifier = base,
            )
        }

        is UIComponent.WindDisplay -> {
            WindDisplayComponent(
                component = component,
                weatherData = weatherData,
                modifier = base,
            )
        }
    }
}

// ─── Extract modifier from any UIComponent ───────────────────────────────────

private fun UIComponent.extractModifier(): ModifierConfig = when (this) {
    is UIComponent.Column -> modifier
    is UIComponent.Row -> modifier
    is UIComponent.Box -> modifier
    is UIComponent.Card -> modifier
    is UIComponent.Text -> modifier
    is UIComponent.Icon -> modifier
    is UIComponent.Divider -> modifier
    is UIComponent.Badge -> modifier
    is UIComponent.Chip -> modifier
    is UIComponent.ProgressBar -> modifier
    is UIComponent.GradientBox -> modifier
    is UIComponent.WeatherCard -> modifier
    is UIComponent.SearchBar -> modifier
    is UIComponent.WeatherDetails -> modifier
    is UIComponent.Button -> modifier
    is UIComponent.TemperatureHero -> modifier
    is UIComponent.WeatherSummary -> modifier
    is UIComponent.WeatherStat -> modifier
    is UIComponent.ConditionBadge -> modifier
    is UIComponent.HumidityBar -> modifier
    is UIComponent.WindDisplay -> modifier
    is UIComponent.Spacer -> ModifierConfig()
}

// ─── String → Compose layout enum helpers ────────────────────────────────────

private fun String.toVerticalArrangement(): Arrangement.Vertical = when (this) {
    "bottom" -> Arrangement.Bottom
    "center" -> Arrangement.Center
    "spaceBetween" -> Arrangement.SpaceBetween
    "spaceEvenly" -> Arrangement.SpaceEvenly
    "spaceAround" -> Arrangement.SpaceAround
    else -> Arrangement.Top
}

private fun String.toHorizontalArrangement(): Arrangement.Horizontal = when (this) {
    "end" -> Arrangement.End
    "center" -> Arrangement.Center
    "spaceBetween" -> Arrangement.SpaceBetween
    "spaceEvenly" -> Arrangement.SpaceEvenly
    "spaceAround" -> Arrangement.SpaceAround
    else -> Arrangement.Start
}

private fun String.toHorizontalAlignment(): Alignment.Horizontal = when (this) {
    "center" -> Alignment.CenterHorizontally
    "end" -> Alignment.End
    else -> Alignment.Start
}

private fun String.toVerticalAlignment(): Alignment.Vertical = when (this) {
    "top" -> Alignment.Top
    "bottom" -> Alignment.Bottom
    else -> Alignment.CenterVertically
}

private fun String.toBoxAlignment(): Alignment = when (this) {
    "topCenter" -> Alignment.TopCenter
    "topEnd" -> Alignment.TopEnd
    "centerStart" -> Alignment.CenterStart
    "center" -> Alignment.Center
    "centerEnd" -> Alignment.CenterEnd
    "bottomStart" -> Alignment.BottomStart
    "bottomCenter" -> Alignment.BottomCenter
    "bottomEnd" -> Alignment.BottomEnd
    else -> Alignment.TopStart
}

private fun String.toTextAlign(): TextAlign = when (this) {
    "center" -> TextAlign.Center
    "end" -> TextAlign.End
    "justify" -> TextAlign.Justify
    else -> TextAlign.Start
}

@Composable
private fun String.toTextStyle() = when (this) {
    "displayLarge" -> MaterialTheme.typography.displayLarge
    "displayMedium" -> MaterialTheme.typography.displayMedium
    "headlineLarge" -> MaterialTheme.typography.headlineLarge
    "headlineMedium" -> MaterialTheme.typography.headlineMedium
    "headlineSmall" -> MaterialTheme.typography.headlineSmall
    "titleLarge" -> MaterialTheme.typography.titleLarge
    "titleMedium" -> MaterialTheme.typography.titleMedium
    "titleSmall" -> MaterialTheme.typography.titleSmall
    "bodyLarge" -> MaterialTheme.typography.bodyLarge
    "bodySmall" -> MaterialTheme.typography.bodySmall
    "labelLarge" -> MaterialTheme.typography.labelLarge
    "labelMedium" -> MaterialTheme.typography.labelMedium
    "labelSmall" -> MaterialTheme.typography.labelSmall
    else -> MaterialTheme.typography.bodyMedium
}
