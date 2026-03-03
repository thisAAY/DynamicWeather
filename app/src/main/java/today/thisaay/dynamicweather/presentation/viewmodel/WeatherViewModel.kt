package today.thisaay.dynamicweather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import today.thisaay.dynamicweather.ApiConstants
import today.thisaay.dynamicweather.agent.LayoutAgent
import today.thisaay.dynamicweather.data.remote.WeatherApiService
import today.thisaay.dynamicweather.data.repository.WeatherRepositoryImpl
import today.thisaay.dynamicweather.domain.LayoutDefaults
import today.thisaay.dynamicweather.domain.model.UIComponent
import today.thisaay.dynamicweather.domain.model.WeatherData
import today.thisaay.dynamicweather.domain.repository.WeatherRepository

class WeatherViewModel(
    private val weatherRepository: WeatherRepository = WeatherRepositoryImpl(
        WeatherApiService(ApiConstants.OPEN_WEATHER_MAP_API_KEY)
    ),
    private val layoutAgent: LayoutAgent = LayoutAgent(ApiConstants.OPENAI_API_KEY),
) : ViewModel() {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        classDiscriminator = "type"
    }

    // ─── Layout State ─────────────────────────────────────────────────────────

    /** Raw JSON string of the current layout — kept as source of truth for the AI agent context. */
    private val _layoutJson = MutableStateFlow(LayoutDefaults.DEFAULT_LAYOUT_JSON)
    val layoutJson: StateFlow<String> = _layoutJson.asStateFlow()

    /** Parsed UIComponent tree ready for rendering. */
    private val _layout = MutableStateFlow<UIComponent>(parseLayout(LayoutDefaults.DEFAULT_LAYOUT_JSON))
    val layout: StateFlow<UIComponent> = _layout.asStateFlow()

    // ─── Weather State ────────────────────────────────────────────────────────

    private val _weatherData = MutableStateFlow<WeatherData?>(null)
    val weatherData: StateFlow<WeatherData?> = _weatherData.asStateFlow()

    private val _currentCity = MutableStateFlow(ApiConstants.DEFAULT_CITY)
    val currentCity: StateFlow<String> = _currentCity.asStateFlow()

    // ─── UI State ─────────────────────────────────────────────────────────────

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isAgentThinking = MutableStateFlow(false)
    val isAgentThinking: StateFlow<Boolean> = _isAgentThinking.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadWeather(ApiConstants.DEFAULT_CITY)
    }

    // ─── Public Actions ───────────────────────────────────────────────────────

    /**
     * Called by the DynamicUIRenderer when a component fires an action key.
     * @param key  The action identifier (e.g. "search_city", "refresh").
     * @param params  Optional key-value parameters (e.g. "query" for search_city).
     */
    fun onAction(key: String, params: Map<String, String> = emptyMap()) {
        when (key) {
            "search_city" -> {
                val query = params["query"] ?: return
                if (query.isNotBlank()) loadWeather(query)
            }
            "refresh" -> loadWeather(_currentCity.value)
        }
    }

    /**
     * Sends the current layout JSON + [userRequest] to the AI agent.
     * On success the layout is updated atomically.
     */
    fun requestLayoutChange(userRequest: String) {
        if (userRequest.isBlank()) return
        viewModelScope.launch {
            _isAgentThinking.value = true
            _error.value = null
            runCatching {
                layoutAgent.requestLayoutChange(
                    currentLayoutJson = _layoutJson.value,
                    userRequest = userRequest,
                )
            }.onSuccess { newJson ->
                // Validate the JSON before accepting it
                val parsed = runCatching { parseLayout(newJson) }.getOrNull()
                if (parsed != null) {
                    _layoutJson.value = newJson
                    _layout.value = parsed
                } else {
                    _error.value = "AI returned an invalid layout. Keeping current layout."
                }
            }.onFailure { e ->
                _error.value = "Agent error: ${e.message}"
            }
            _isAgentThinking.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }

    // ─── Internal ─────────────────────────────────────────────────────────────

    private fun loadWeather(city: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            weatherRepository.getWeather(city)
                .onSuccess { data ->
                    _weatherData.value = data
                    _currentCity.value = data.city
                }
                .onFailure { e ->
                    _error.value = "Could not load weather for \"$city\": ${e.message}"
                }
            _isLoading.value = false
        }
    }

    private fun parseLayout(jsonString: String): UIComponent {
        return json.decodeFromString(UIComponent.serializer(), jsonString.trim())
    }
}
