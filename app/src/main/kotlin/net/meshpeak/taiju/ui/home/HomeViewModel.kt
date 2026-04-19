package net.meshpeak.taiju.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.meshpeak.taiju.data.time.TimeProvider
import net.meshpeak.taiju.domain.model.Period
import net.meshpeak.taiju.domain.usecase.GetLatestWeightUseCase
import net.meshpeak.taiju.domain.usecase.GetTodayWeightUseCase
import net.meshpeak.taiju.domain.usecase.GetWeightHistoryUseCase
import net.meshpeak.taiju.domain.usecase.UpsertWeightUseCase
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val time: TimeProvider,
        private val getTodayWeight: GetTodayWeightUseCase,
        private val getLatestWeight: GetLatestWeightUseCase,
        private val getHistory: GetWeightHistoryUseCase,
        private val upsertWeight: UpsertWeightUseCase,
    ) : ViewModel() {
        private val _state = MutableStateFlow(HomeUiState(today = time.today()))
        val state = _state.asStateFlow()

        private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 1)
        val messages: SharedFlow<String> = _messages.asSharedFlow()

        private var userTouchedInput = false

        init {
            viewModelScope.launch {
                val latest = getLatestWeight()
                if (!userTouchedInput && _state.value.todayWeight == null) {
                    _state.update { current ->
                        current.copy(
                            inputWeightKg =
                                latest?.weightKg?.roundTo1()
                                    ?: HomeUiState.DEFAULT_WEIGHT_KG,
                        )
                    }
                }
            }

            getTodayWeight()
                .onEach { entry ->
                    _state.update { current ->
                        current.copy(
                            todayWeight = entry,
                            inputWeightKg =
                                if (entry != null && !userTouchedInput) {
                                    entry.weightKg.roundTo1()
                                } else {
                                    current.inputWeightKg
                                },
                        )
                    }
                }
                .launchIn(viewModelScope)

            getHistory(Period.WEEK)
                .onEach { list -> _state.update { it.copy(recent = list) } }
                .launchIn(viewModelScope)
        }

        fun onValueChange(value: Double) {
            userTouchedInput = true
            _state.update { it.copy(inputWeightKg = value.roundTo1()) }
        }

        fun onSaveClicked() {
            val value = _state.value.inputWeightKg
            val today = _state.value.today
            val hadExisting = _state.value.todayWeight != null
            viewModelScope.launch {
                _state.update { it.copy(isSaving = true) }
                val result = upsertWeight(today, value)
                _state.update { it.copy(isSaving = false) }
                if (result.isSuccess) {
                    _messages.emit(if (hadExisting) "体重を更新しました" else "体重を記録しました")
                } else {
                    _messages.emit(result.exceptionOrNull()?.message ?: "保存に失敗しました")
                }
            }
        }

        private fun Double.roundTo1(): Double = round(this * 10) / 10
    }
