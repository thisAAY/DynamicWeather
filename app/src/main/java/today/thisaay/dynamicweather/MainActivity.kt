package today.thisaay.dynamicweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import today.thisaay.dynamicweather.presentation.screen.WeatherScreen
import today.thisaay.dynamicweather.presentation.viewmodel.WeatherViewModel
import today.thisaay.dynamicweather.ui.theme.DynamicWeatherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DynamicWeatherTheme {
                val viewModel: WeatherViewModel = viewModel()
                WeatherScreen(viewModel = viewModel)
            }
        }
    }
}
