package today.thisaay.dynamicweather.presentation.renderer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import today.thisaay.dynamicweather.domain.model.ModifierConfig

/** Applies a [ModifierConfig] to a Compose [Modifier], respecting priority of padding fields. */
fun Modifier.applyConfig(config: ModifierConfig): Modifier {
    var m = this
    if (config.fillMaxSize) m = m.fillMaxSize()
    else if (config.fillMaxWidth) m = m.fillMaxWidth()

    // Apply padding in order of specificity: paddingAll → horizontal/vertical → individual sides
    val hasSide = config.paddingTop > 0 || config.paddingBottom > 0 ||
        config.paddingStart > 0 || config.paddingEnd > 0
    val hasHV = config.paddingHorizontal > 0 || config.paddingVertical > 0
    when {
        hasSide -> m = m.padding(
            start = config.paddingStart.dp,
            top = config.paddingTop.dp,
            end = config.paddingEnd.dp,
            bottom = config.paddingBottom.dp,
        )
        hasHV -> m = m.padding(
            horizontal = config.paddingHorizontal.dp,
            vertical = config.paddingVertical.dp,
        )
        config.paddingAll > 0 -> m = m.padding(config.paddingAll.dp)
    }
    return m
}
