package com.skyglance.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.skyglance.location.rememberLocationAccess
import com.skyglance.presentation.ForecastViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Thin connector: resolves [ForecastViewModel] via Koin and feeds its state into the stateless
 * [ForecastContent]. [rememberLocationAccess] wires the actual Activity-level permission request
 * (Android) or the unsupported stub (iOS); either way, the real outcome — including whether it was
 * denied forever — flows back into [ForecastViewModel.onPermissionResult]. Re-checks on every
 * resume (covers the initial launch and returning from the "Open settings" deep link, where the
 * permission may have just changed with no callback of its own to tell us).
 */
@Composable
fun ForecastRoute() {
    val viewModel = koinViewModel<ForecastViewModel>()
    val state by viewModel.uiState.collectAsState()
    val locationAccess =
        rememberLocationAccess { _, permanentlyDenied -> viewModel.onPermissionResult(permanentlyDenied) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.onScreenStarted() }

    ForecastContent(
        state = state,
        onDaySelected = viewModel::onDaySelected,
        onUnitSelected = viewModel::onUnitSelected,
        onRetry = viewModel::onRetry,
        onRequestPermission = locationAccess::request,
        onOpenSettings = locationAccess::openSettings,
    )
}
