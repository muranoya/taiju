package net.meshpeak.taiju.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.data.time.TimeProvider
import net.meshpeak.taiju.domain.usecase.GetMonthlyWeightsUseCase
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel
    @Inject
    constructor(
        timeProvider: TimeProvider,
        private val getMonthlyWeights: GetMonthlyWeightsUseCase,
    ) : ViewModel() {
        private val today = timeProvider.today()
        private val _state = MutableStateFlow(CalendarUiState(today.year, today.monthNumber, today))
        val state = _state.asStateFlow()

        private val cursor = MutableStateFlow(today.year to today.monthNumber)

        init {
            @Suppress("OPT_IN_USAGE")
            cursor
                .flatMapLatest { (y, m) -> getMonthlyWeights(y, m).map { weights -> Triple(y, m, weights) } }
                .onEach { (y, m, weights) ->
                    _state.value =
                        _state.value.copy(
                            year = y,
                            month = m,
                            weights = weights,
                        )
                }
                .launchIn(viewModelScope)
        }

        fun onPrevMonth() {
            val (y, m) = cursor.value
            cursor.value =
                if (m == 1) (y - 1) to 12 else y to (m - 1)
        }

        fun onNextMonth() {
            val (y, m) = cursor.value
            cursor.value =
                if (m == 12) (y + 1) to 1 else y to (m + 1)
        }

        fun onBackToToday() {
            cursor.value = today.year to today.monthNumber
        }

        @Suppress("unused")
        private fun dateAt(day: Int): LocalDate = LocalDate(_state.value.year, _state.value.month, day)
    }
