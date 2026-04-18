package net.meshpeak.taiju.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.domain.model.Memo
import net.meshpeak.taiju.domain.usecase.DeleteMemoUseCase
import net.meshpeak.taiju.domain.usecase.DeleteWeightUseCase
import net.meshpeak.taiju.domain.usecase.GetDayDetailUseCase
import net.meshpeak.taiju.domain.usecase.ReorderMemosUseCase
import net.meshpeak.taiju.domain.usecase.UpsertMemoUseCase
import net.meshpeak.taiju.domain.usecase.UpsertWeightUseCase
import net.meshpeak.taiju.ui.navigation.TaijuDestination
import javax.inject.Inject

@HiltViewModel
class DetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        getDayDetail: GetDayDetailUseCase,
        private val upsertWeight: UpsertWeightUseCase,
        private val deleteWeight: DeleteWeightUseCase,
        private val upsertMemo: UpsertMemoUseCase,
        private val deleteMemo: DeleteMemoUseCase,
        private val reorderMemos: ReorderMemosUseCase,
    ) : ViewModel() {
        private val date: LocalDate =
            LocalDate.parse(
                savedStateHandle.get<String>(TaijuDestination.Detail.ARG_DATE)
                    ?: error("date argument required"),
            )

        private val _state = MutableStateFlow(DetailUiState(date = date))
        val state = _state.asStateFlow()

        init {
            getDayDetail(date)
                .onEach { detail ->
                    val current = _state.value
                    val inputLooksFresh = current.weightInput.isBlank() || current.weight == null
                    _state.value =
                        current.copy(
                            weight = detail.weight,
                            memos = detail.memos,
                            weightInput =
                                if (inputLooksFresh) {
                                    detail.weight?.weightKg?.let { "%.1f".format(it) } ?: ""
                                } else {
                                    current.weightInput
                                },
                        )
                }
                .launchIn(viewModelScope)
        }

        fun onWeightInputChange(value: String) {
            _state.value = _state.value.copy(weightInput = value, errorMessage = null)
        }

        fun onSaveWeight() {
            val raw = _state.value.weightInput.trim()
            val value = raw.toDoubleOrNull()
            if (value == null) {
                _state.value = _state.value.copy(errorMessage = "数値を入力してください")
                return
            }
            viewModelScope.launch {
                _state.value = _state.value.copy(saving = true)
                val res = upsertWeight(date, value)
                _state.value =
                    _state.value.copy(
                        saving = false,
                        errorMessage = res.exceptionOrNull()?.message,
                    )
            }
        }

        fun onDeleteWeight() {
            viewModelScope.launch {
                deleteWeight(date)
                _state.value = _state.value.copy(weightInput = "")
            }
        }

        fun onStartAddMemo() {
            _state.value =
                _state.value.copy(
                    isBottomSheetOpen = true,
                    editingMemo = null,
                    memoInput = "",
                )
        }

        fun onStartEditMemo(memo: Memo) {
            _state.value =
                _state.value.copy(
                    isBottomSheetOpen = true,
                    editingMemo = memo,
                    memoInput = memo.content,
                )
        }

        fun onMemoInputChange(value: String) {
            _state.value = _state.value.copy(memoInput = value)
        }

        fun onDismissSheet() {
            _state.value =
                _state.value.copy(
                    isBottomSheetOpen = false,
                    memoInput = "",
                    editingMemo = null,
                )
        }

        fun onSubmitMemo() {
            val content = _state.value.memoInput
            val editing = _state.value.editingMemo
            viewModelScope.launch {
                val res =
                    if (editing == null) {
                        upsertMemo.append(date, content)
                    } else {
                        upsertMemo.update(editing.id, content)
                    }
                if (res.isSuccess) {
                    onDismissSheet()
                } else {
                    _state.value =
                        _state.value.copy(errorMessage = res.exceptionOrNull()?.message)
                }
            }
        }

        fun onDeleteMemo(id: Long) {
            viewModelScope.launch { deleteMemo(id) }
        }

        fun onMoveMemo(
            fromIndex: Int,
            toIndex: Int,
        ) {
            val current = _state.value.memos.toMutableList()
            if (fromIndex !in current.indices || toIndex !in current.indices) return
            val item = current.removeAt(fromIndex)
            current.add(toIndex, item)
            val orderedIds = current.map { it.id }
            _state.value = _state.value.copy(memos = current)
            viewModelScope.launch { reorderMemos(orderedIds) }
        }

        fun onDismissError() {
            _state.value = _state.value.copy(errorMessage = null)
        }
    }
