package today.thisaay.dynamicweather.presentation.renderer

import androidx.compose.ui.graphics.Color

/** Parses a hex color string ("#RRGGBB" or "#AARRGGBB") into a Compose [Color]. */
internal fun String.toComposeColor(): Color {
    return try {
        val hex = removePrefix("#")
        when (hex.length) {
            6 -> Color(("FF$hex").toLong(16).toInt())
            8 -> Color(hex.toLong(16).toInt())
            else -> Color.Unspecified
        }
    } catch (_: NumberFormatException) {
        Color.Unspecified
    }
}
