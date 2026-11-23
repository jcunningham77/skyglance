@file:OptIn(ExperimentalCoroutinesApi::class)

package com.skyglance.presentation

import com.skyglance.data.ForecastRepository
import com.skyglance.data.PreferencesRepository
import com.skyglance.data.RefreshFailure
import com.skyglance.data.RefreshResult
import com.skyglance.domain.Condition
import com.skyglance.domain.DayForecast
import com.skyglance.domain.Forecast
import com.skyglance.domain.TempUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

private class FakeForecastRepository : ForecastRepository {
    // Replay 1, per the plan's own description of how these tests should be built.
    val forecastFlow = MutableSharedFlow<Forecast>(replay = 1)
    override val forecast: Flow<Forecast> = forecastFlow

    var refreshResult: RefreshResult = RefreshResult.Success
    var refreshCallCount = 0
        private set

    override suspend fun refresh(): RefreshResult {
        refreshCallCount++
        return refreshResult
    }
}

private class FakePreferencesRepository(
    initial: TempUnit = TempUnit.Fahrenheit,
) : PreferencesRepository {
    private val state = MutableStateFlow(initial)
    override val unit: Flow<TempUnit> = state

    override suspend fun setUnit(unit: TempUnit) {
        state.value = unit
    }
}

private class FixedClock(
    private val instant: Instant,
) : Clock {
    override fun now(): Instant = instant
}

private fun day(date: LocalDate) = DayForecast(date, Condition.Clear, 20.0, 10.0, 20, 10.0, null, null)

private fun forecastOf(vararg dates: LocalDate) =
    Forecast(
        placeName = "Boulder, Colorado",
        lat = 40.0,
        lon = -105.0,
        fetchedAt = Instant.parse("2026-11-24T12:00:00Z"),
        days = dates.map(::day),
    )

class ForecastViewModelTest {
    private val today = LocalDate(2026, 11, 24)
    private val clock = FixedClock(Instant.parse("2026-11-24T12:00:00Z"))

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun TestScope.viewModelCollecting(
        repository: ForecastRepository,
        preferences: PreferencesRepository = FakePreferencesRepository(),
    ): Pair<ForecastViewModel, MutableList<ForecastUiState>> {
        val viewModel = ForecastViewModel(repository, preferences, clock)
        val collected = mutableListOf<ForecastUiState>()
        backgroundScope.launch { viewModel.uiState.collect { collected.add(it) } }
        return viewModel to collected
    }

    @Test
    fun permissionDeniedWithNoCacheShowsPermissionNeeded() =
        runTest {
            val repository = FakeForecastRepository()
            repository.refreshResult = RefreshResult.Failure(RefreshFailure.PermissionDenied(permanently = false))
            val (viewModel, states) = viewModelCollecting(repository)

            viewModel.onScreenStarted()
            runCurrent()

            assertIs<ForecastUiState.PermissionNeeded>(states.last())
        }

    @Test
    fun onPermissionResultOverridesTheRepositorysAlwaysFalsePermanentlyFlag() =
        runTest {
            // FusedLocationProvider can never actually know "permanently" (see its own comment),
            // so the fake mirrors that by always reporting false — the real answer has to come
            // from the live permission callback via onPermissionResult, not the repository.
            val repository = FakeForecastRepository()
            repository.refreshResult = RefreshResult.Failure(RefreshFailure.PermissionDenied(permanently = false))
            val (viewModel, states) = viewModelCollecting(repository)

            viewModel.onPermissionResult(permanentlyDenied = true)
            runCurrent()

            val state = assertIs<ForecastUiState.PermissionNeeded>(states.last())
            assertTrue(state.permanently)
        }

    @Test
    fun permissionDeniedWithExistingCacheShowsContentWithStaleBanner() =
        runTest {
            val repository = FakeForecastRepository()
            repository.refreshResult = RefreshResult.Failure(RefreshFailure.PermissionDenied(permanently = false))
            val (viewModel, states) = viewModelCollecting(repository)
            repository.forecastFlow.emit(forecastOf(today))
            runCurrent()

            viewModel.onScreenStarted()
            runCurrent()

            val state = assertIs<ForecastUiState.Content>(states.last())
            assertTrue(state.staleSince != null)
            assertEquals("Location permission needed to show a forecast.", state.staleReason)
        }

    @Test
    fun retryCallsRefreshAgain() =
        runTest {
            val repository = FakeForecastRepository()
            repository.refreshResult = RefreshResult.Failure(RefreshFailure.Network)
            val (viewModel, _) = viewModelCollecting(repository)

            viewModel.onScreenStarted()
            runCurrent()
            viewModel.onRetry()
            runCurrent()

            assertEquals(2, repository.refreshCallCount)
        }

    @Test
    fun togglingUnitsNeverCallsRefresh() =
        runTest {
            val repository = FakeForecastRepository()
            val (viewModel, states) = viewModelCollecting(repository)
            repository.forecastFlow.emit(forecastOf(today))
            runCurrent()

            viewModel.onUnitSelected(TempUnit.Celsius)
            runCurrent()

            val state = assertIs<ForecastUiState.Content>(states.last())
            assertEquals(TempUnit.Celsius, state.unit)
            assertEquals(0, repository.refreshCallCount)
        }

    @Test
    fun selectingADayNeverCallsRefresh() =
        runTest {
            val repository = FakeForecastRepository()
            val (viewModel, states) = viewModelCollecting(repository)
            repository.forecastFlow.emit(forecastOf(today, today.plus(1, DateTimeUnit.DAY)))
            runCurrent()

            viewModel.onDaySelected(1)
            runCurrent()

            val state = assertIs<ForecastUiState.Content>(states.last())
            assertEquals(1, state.selectedIndex)
            assertEquals(0, repository.refreshCallCount)
        }

    @Test
    fun selectionIsClampedToTheListSize() =
        runTest {
            val repository = FakeForecastRepository()
            val (viewModel, states) = viewModelCollecting(repository)
            repository.forecastFlow.emit(forecastOf(today))
            runCurrent()

            viewModel.onDaySelected(99)
            runCurrent()

            val state = assertIs<ForecastUiState.Content>(states.last())
            assertEquals(0, state.selectedIndex)
        }

    @Test
    fun selectionDefaultsToTodaysDate() =
        runTest {
            val repository = FakeForecastRepository()
            val (_, states) = viewModelCollecting(repository)
            val yesterday = today.minus(1, DateTimeUnit.DAY)

            repository.forecastFlow.emit(forecastOf(yesterday, today, today.plus(1, DateTimeUnit.DAY)))
            runCurrent()

            val state = assertIs<ForecastUiState.Content>(states.last())
            assertEquals(1, state.selectedIndex)
        }

    @Test
    fun selectionResetsWhenANewForecastArrives() =
        runTest {
            val repository = FakeForecastRepository()
            val (viewModel, states) = viewModelCollecting(repository)
            repository.forecastFlow.emit(forecastOf(today, today.plus(1, DateTimeUnit.DAY)))
            runCurrent()
            viewModel.onDaySelected(1)
            runCurrent()

            // A new forecast arrives (e.g. a later successful refresh) — selection resets to today.
            repository.forecastFlow.emit(forecastOf(today, today.plus(1, DateTimeUnit.DAY)))
            runCurrent()

            val state = assertIs<ForecastUiState.Content>(states.last())
            assertEquals(0, state.selectedIndex)
        }
}
