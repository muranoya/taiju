package net.meshpeak.taiju.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.meshpeak.taiju.data.time.TimeProvider
import net.meshpeak.taiju.domain.model.Period
import net.meshpeak.taiju.domain.repository.MemoRepository
import net.meshpeak.taiju.domain.usecase.GetTodayWeightUseCase
import net.meshpeak.taiju.domain.usecase.GetWeightHistoryUseCase
import net.meshpeak.taiju.domain.usecase.UpsertWeightUseCase
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val time: TimeProvider,
        private val getTodayWeight: GetTodayWeightUseCase,
        private val getHistory: GetWeightHistoryUseCase,
        private val upsertWeight: UpsertWeightUseCase,
        memoRepo: MemoRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(HomeUiState(today = time.today()))
        val state = _state.asStateFlow()

        init {
            getTodayWeight()
                .onEach { entry ->
                    _state.value =
                        _state.value.copy(
                            todayWeight = entry,
                            inputWeight =
                                if (_state.value.inputWeight.isBlank()) {
                                    entry?.weightKg?.let { "%.1f".format(it) } ?: ""
                                } else {
                                    _state.value.inputWeight
                                },
                        )
                }
                .launchIn(viewModelScope)

            getHistory(Period.WEEK)
                .onEach { list -> _state.value = _state.value.copy(recent = list) }
                .launchIn(viewModelScope)

            memoRepo
                .observeRecent(5)
                .onEach { list -> _state.value = _state.value.copy(recentMemos = list) }
                .launchIn(viewModelScope)
        }

        fun onInputChange(value: String) {
            _state.value = _state.value.copy(inputWeight = value, errorMessage = null)
        }

        fun onSaveClicked() {
            val raw = _state.value.inputWeight.trim()
            val value = raw.toDoubleOrNull()
            if (value == null) {
                _state.value = _state.value.copy(errorMessage = "数値を入力してください")
                return
            }
            viewModelScope.launch {
                _state.value = _state.value.copy(isSaving = true)
                val result = upsertWeight(_state.value.today, value)
                _state.value =
                    _state.value.copy(
                        isSaving = false,
                        errorMessage = result.exceptionOrNull()?.message,
                    )
            }
        }

        fun onDismissError() {
            _state.value = _state.value.copy(errorMessage = null)
        }
    }
