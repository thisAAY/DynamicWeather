package today.thisaay.dynamicweather.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import today.thisaay.dynamicweather.presentation.renderer.DynamicUIRenderer
import today.thisaay.dynamicweather.presentation.viewmodel.WeatherViewModel

/**
 * Single-screen weather app root.
 *
 * The entire screen layout is driven by the JSON tree in [WeatherViewModel.layout].
 * A FAB opens [LayoutRequestDialog] so the user can ask the AI agent to redesign the layout.
 */
@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val layout by viewModel.layout.collectAsState()
    val weatherData by viewModel.weatherData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isAgentThinking by viewModel.isAgentThinking.collectAsState()
    val error by viewModel.error.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showLayoutDialog by remember { mutableStateOf(false) }

    // Show errors as Snackbar
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showLayoutDialog = true },
                icon = {
                    if (isAgentThinking) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.padding(2.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Redesign")
                    }
                },
                text = {
                    Text(if (isAgentThinking) "AI thinking…" else "Redesign")
                },
                expanded = !isAgentThinking,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(snackbarData = data)
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // Scrollable area for the dynamic layout
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                DynamicUIRenderer(
                    component = layout,
                    weatherData = weatherData,
                    onAction = { key, params -> viewModel.onAction(key, params) },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            // Loading overlay
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center),
            ) {
                CircularProgressIndicator()
            }
        }
    }

    // AI layout request dialog
    if (showLayoutDialog) {
        LayoutRequestDialog(
            onSubmit = { request ->
                showLayoutDialog = false
                viewModel.requestLayoutChange(request)
            },
            onDismiss = { showLayoutDialog = false },
        )
    }
}
