package today.thisaay.dynamicweather.domain.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

/**
 * Sealed class hierarchy representing the server-side-driven UI tree.
 * Each subclass maps to a JSON object with a "type" discriminator field.
 *
 * Low-level primitives : column, row, box, card, text, spacer, icon, divider,
 *                        badge, chip, progress_bar, gradient_box
 * High-level smart     : weather_card, search_bar, weather_details, button,
 *                        temperature_hero, weather_summary, weather_stat,
 *                        condition_badge, humidity_bar, wind_display
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed class UIComponent {

    // ─── Low-Level Layout Primitives ──────────────────────────────────────────

    @Serializable
    @SerialName("column")
    data class Column(
        val children: List<UIComponent> = emptyList(),
        /** top | bottom | center | spaceBetween | spaceEvenly | spaceAround */
        val verticalArrangement: String = "top",
        /** start | center | end */
        val horizontalAlignment: String = "start",
        val modifier: ModifierConfig = ModifierConfig(),
    ) : UIComponent()

    @Serializable
    @SerialName("row")
    data class Row(
        val children: List<UIComponent> = emptyList(),
        /** start | end | center | spaceBetween | spaceEvenly | spaceAround */
        val horizontalArrangement: String = "start",
        /** top | center | bottom */
        val verticalAlignment: String = "center",
        val modifier: ModifierConfig = ModifierConfig(),
    ) : UIComponent()

    /**
     * Stacking container — children are layered on top of each other.
     */
    @Serializable
    @SerialName("box")
    data class Box(
        val children: List<UIComponent> = emptyList(),
        /**
         * topStart | topCenter | topEnd |
         * centerStart | center | centerEnd |
         * bottomStart | bottomCenter | bottomEnd
         */
        val contentAlignment: String = "topStart",
        val modifier: ModifierConfig = ModifierConfig(),
    ) : UIComponent()

    @Serializable
    @SerialName("card")
    data class Card(
        val children: List<UIComponent> = emptyList(),
        val elevation: Int = 2,
        val modifier: ModifierConfig = ModifierConfig(),
    ) : UIComponent()

    @Serializable
    @SerialName("text")
    data class Text(
        val text: String = "",
        /**
         * displayLarge | displayMedium | headlineLarge | headlineMedium | headlineSmall |
         * titleLarge | titleMedium | titleSmall |
         * bodyLarge | bodyMedium | bodySmall |
         * labelLarge | labelMedium | labelSmall
         */
        val style: String = "bodyMedium",
        /** Hex color e.g. "#FFFFFF". Null = use theme default. */
        val color: String? = null,
        /** start | center | end | justify */
        val textAlign: String = "start",
        val modifier: ModifierConfig = ModifierConfig(),
    ) : UIComponent()

    @Serializable
    @SerialName("spacer")
    data class Spacer(
        /** Fixed height in dp. */
        val height: Int = 8,
        /** If set, spacer takes a fraction of remaining space (0.0–1.0). Overrides height. */
        val weight: Float? = null,
    ) : UIComponent()

    @Serializable
    @SerialName("icon")
    data class Icon(
        /**
         * cloud | air | water_drop | thermostat | visibility | wb_sunny |
         * nights_stay | thunderstorm | grain | ac_unit | speed
         */
        val name: String = "cloud",
        val size: Int = 48,
        /** Hex color e.g. "#FFFFFF". Null = use theme default. */
        val tint: String? = null,
        val modifier: ModifierConfig = ModifierConfig(),
    ) : UIComponent()

    @Serializable
    @SerialName("divider")
    data class Divider(
        val thickness: Int = 1,
        /** Hex color. Null = use theme default. */
        val color: String? = null,
        val modifier: ModifierConfig = ModifierConfig(),
    ) : UIComponent()

    /**
     * A small colored pill label — great for tags, status indicators, temperature ranges.
     */
    @Serializable
    @SerialName("badge")
    data class Badge(
        val text: String = "",
        /** Hex background color. Null = tertiaryContainer. */
        val backgroundColor: String? = null,
        /** Hex text color. Null = onTertiaryContainer. */
        val textColor: String? = null,
        val modifier: ModifierConfig = ModifierConfig(),
    ) : UIComponent()

    /**
     * A tappable chip with an optional leading icon. Purely decorative (no action fired).
     */
    @Serializable
    @SerialName("chip")
    data class Chip(
        val text: String = "",
        /**
         * Optional leading icon name (same names as "icon" component).
         * e.g. "cloud", "air", "water_drop"
         */
        val leadingIconName: String? = null,
        val modifier: ModifierConfig = ModifierConfig(),
    ) : UIComponent()

    /**
     * A linear progress indicator. Use for static/AI-set values (e.g. a battery bar).
     * For live humidity data use [HumidityBar] instead.
     */
    @Serializable
    @SerialName("progress_bar")
    data class ProgressBar(
        /** Value between 0.0 and 1.0. */
        val progress: Float = 0.5f,
        /** Hex color for the filled portion. Null = primary. */
        val color: String? = null,
        /** Hex color for the track. Null = surfaceVariant. */
        val trackColor: String? = null,
        val modifier: ModifierConfig = ModifierConfig(),
    ) : UIComponent()

    /**
     * Box with a two-stop linear gradient background. Children are laid inside the box.
     */
    @Serializable
    @SerialName("gradient_box")
    data class GradientBox(
        val children: List<UIComponent> = emptyList(),
        /** Hex start color. */
        val startColor: String = "#4FC3F7",
        /** Hex end color. */
        val endColor: String = "#0277BD",
        /** horizontal | vertical | diagonal */
        val direction: String = "vertical",
        val cornerRadius: Int = 0,
        val modifier: ModifierConfig = ModifierConfig(),
    ) : UIComponent()

    // ─── High-Level Smart Components ──────────────────────────────────────────

    /**
     * Main weather card. Binds to live weather data.
     * Styles:
     *  - large   : Hero card — giant temp (64sp), city/country header, description, feels like
     *  - medium  : Balanced card — 48sp temp, city header, icon, condition summary
     *  - compact : Row layout — 32sp temp, city + condition in one line
     *  - minimal : Inline text "City: X°C"
     */
    @Serializable
    @SerialName("weather_card")
    data class WeatherCard(
        /** large | medium | compact | minimal */
        val style: String = "large",
        val showIcon: Boolean = true,
        val showFeelsLike: Boolean = true,
        val modifier: ModifierConfig = ModifierConfig(),
    ) : UIComponent()

    /**
     * City search input. Fires action key "search_city" with param "query".
     */
    @Serializable
    @SerialName("search_bar")
    data class SearchBar(
        val placeholder: String = "Search city...",
        val actionKey: String = "search_city",
        val modifier: ModifierConfig = ModifierConfig(),
    ) : UIComponent()

    /**
     * Grid of weather detail chips. Each item key maps to a weatherData field.
     * Supported keys: humidity | wind_speed | pressure | feels_like | visibility | cloud_cover
     */
    @Serializable
    @SerialName("weather_details")
    data class WeatherDetails(
        val items: List<String> = listOf("humidity", "wind_speed", "pressure", "feels_like"),
        val modifier: ModifierConfig = ModifierConfig(),
    ) : UIComponent()

    /**
     * Tappable button. Fires the given actionKey to the ViewModel.
     */
    @Serializable
    @SerialName("button")
    data class Button(
        val text: String = "Button",
        val actionKey: String = "refresh",
        /** filled | outlined | text */
        val style: String = "filled",
        val modifier: ModifierConfig = ModifierConfig(),
    ) : UIComponent()

    /**
     * Full-screen hero temperature display — the centerpiece of the screen.
     * Binds to live weather data. Great for bold, creative layouts.
     * Sizes:
     *  - large  : Enormous temp (96sp), centered icon above, city + condition below
     *  - medium : 64sp temp side-by-side with icon, city below
     *  - small  : Row with icon + 40sp temp + city inline
     */
    @Serializable
    @SerialName("temperature_hero")
    data class TemperatureHero(
        val showCity: Boolean = true,
        val showCondition: Boolean = true,
        val showIcon: Boolean = true,
        /** large | medium | small */
        val size: String = "large",
        val modifier: ModifierConfig = ModifierConfig(),
    ) : UIComponent()

    /**
     * Compact horizontal weather summary. Binds to live data.
     * Sizes:
     *  - large  : Icon + city (headlineMedium) + 48sp temp + condition
     *  - medium : Icon + city (titleLarge) + 32sp temp
     *  - small  : Icon + single-line "Cairo • 22°C • Partly cloudy"
     */
    @Serializable
    @SerialName("weather_summary")
    data class WeatherSummary(
        /** large | medium | small */
        val size: String = "medium",
        val showIcon: Boolean = true,
        val modifier: ModifierConfig = ModifierConfig(),
    ) : UIComponent()

    /**
     * A single prominent weather stat display. Binds to live data.
     * Supported keys: humidity | wind_speed | pressure | feels_like | visibility | cloud_cover
     * Sizes:
     *  - large  : Centered icon (40dp) + big value (headlineLarge) + label
     *  - medium : Icon + label (row) + large value below
     *  - small  : Compact inline icon + value
     */
    @Serializable
    @SerialName("weather_stat")
    data class WeatherStat(
        /** humidity | wind_speed | pressure | feels_like | visibility | cloud_cover */
        val key: String = "humidity",
        /** large | medium | small */
        val size: String = "medium",
        val showIcon: Boolean = true,
        val showLabel: Boolean = true,
        val modifier: ModifierConfig = ModifierConfig(),
    ) : UIComponent()

    /**
     * A badge pill showing the current weather condition text + emoji.
     * Binds to live weather data.
     */
    @Serializable
    @SerialName("condition_badge")
    data class ConditionBadge(
        val modifier: ModifierConfig = ModifierConfig(),
    ) : UIComponent()

    /**
     * Visual humidity display — label, percentage value, and a linear progress bar.
     * Binds to live weather data.
     */
    @Serializable
    @SerialName("humidity_bar")
    data class HumidityBar(
        val showLabel: Boolean = true,
        val showValue: Boolean = true,
        val modifier: ModifierConfig = ModifierConfig(),
    ) : UIComponent()

    /**
     * Wind speed display with icon. Binds to live weather data.
     * Sizes:
     *  - large  : Surface card — centered Air icon + big speed value + "m/s" label
     *  - medium : Row — icon + "Wind" label on left, speed value on right
     *  - small  : Inline "💨 5.2 m/s" row
     */
    @Serializable
    @SerialName("wind_display")
    data class WindDisplay(
        /** large | medium | small */
        val size: String = "medium",
        val modifier: ModifierConfig = ModifierConfig(),
    ) : UIComponent()
}

/** Shared layout modifier config applied to any component. */
@Serializable
data class ModifierConfig(
    val fillMaxWidth: Boolean = false,
    val fillMaxSize: Boolean = false,
    val paddingAll: Int = 0,
    val paddingHorizontal: Int = 0,
    val paddingVertical: Int = 0,
    val paddingTop: Int = 0,
    val paddingBottom: Int = 0,
    val paddingStart: Int = 0,
    val paddingEnd: Int = 0,
)
