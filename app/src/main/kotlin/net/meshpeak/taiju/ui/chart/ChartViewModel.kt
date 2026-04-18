package net.meshpeak.taiju.ui.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.meshpeak.taiju.domain.model.Period
import net.meshpeak.taiju.domain.usecase.GetUserSettingsUseCase
import net.meshpeak.taiju.domain.usecase.GetWeightHistoryUseCase
import javax.inject.Inject

@HiltViewModel
class ChartViewModel
    @Inject
    constructor(
        private val getWeightHistory: GetWeightHistoryUseCase,
        getUserSettings: GetUserSettingsUseCase,
    ) : ViewModel() {
        private val _state = MutableStateFlow(ChartUiState())
        val state = _state.asStateFlow()

        private val periodFlow = MutableStateFlow(Period.MONTH)

        init {
            @Suppress("OPT_IN_USAGE")
            periodFlow
                .flatMapLatest { period ->
                    combine(
                        getWeightHistory(period),
                        getUserSettings(),
                    ) { points, settings ->
                        Triple(period, points, settings.targetWeightKg)
                    }
                }
                .onEach { (period, points, target) ->
                    _state.value =
                        _state.value.copy(
                            period = period,
                            points = points,
                            targetWeightKg = target,
                        )
                }
                .launchIn(viewModelScope)
        }

        fun onPeriodChange(period: Period) {
            periodFlow.value = period
        }
    }
