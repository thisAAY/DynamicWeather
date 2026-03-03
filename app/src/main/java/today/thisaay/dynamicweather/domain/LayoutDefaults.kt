package today.thisaay.dynamicweather.domain

/**
 * Default Cairo weather screen layout as a JSON string.
 * This is used as the initial UI on app start and as the starting context for the AI agent.
 */
object LayoutDefaults {
    val DEFAULT_LAYOUT_JSON = """
        {
          "type": "column",
          "verticalArrangement": "top",
          "horizontalAlignment": "start",
          "modifier": { "fillMaxSize": true, "paddingAll": 16 },
          "children": [
            {
              "type": "search_bar",
              "placeholder": "Search city...",
              "actionKey": "search_city",
              "modifier": { "fillMaxWidth": true }
            },
            { "type": "spacer", "height": 24 },
            {
              "type": "weather_card",
              "style": "large",
              "showIcon": true,
              "showFeelsLike": true,
              "modifier": { "fillMaxWidth": true }
            },
            { "type": "spacer", "height": 16 },
            {
              "type": "weather_details",
              "items": ["humidity", "wind_speed", "pressure", "feels_like", "visibility"],
              "modifier": { "fillMaxWidth": true }
            },
            { "type": "spacer", "weight": 1.0 },
            {
              "type": "button",
              "text": "Refresh",
              "actionKey": "refresh",
              "style": "outlined",
              "modifier": { "fillMaxWidth": true }
            }
          ]
        }
    """.trimIndent()
}
