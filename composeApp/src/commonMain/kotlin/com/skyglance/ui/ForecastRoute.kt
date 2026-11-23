package com.skyglance.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.skyglance.presentation.ForecastViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Thin connector: resolves [ForecastViewModel] via Koin and feeds its state into the stateless
 * [ForecastContent]. Requesting the system permission and deep-linking to Settings are an Activity
 * result launcher — the next phase ("Permission flow") — so both callbacks just ask the ViewModel
 * to re-check for now.
 */
@Composable
fun ForecastRoute() {
    val viewModel = koinViewModel<ForecastViewModel>()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.onScreenStarted() }

    ForecastContent(
        state = state,
        onDaySelected = viewModel::onDaySelected,
        onUnitSelected = viewModel::onUnitSelected,
        onRetry = viewModel::onRetry,
        onRequestPermission = viewModel::onPermissionResult,
        onOpenSettings = viewModel::onPermissionResult,
    )
}
