package net.meshpeak.taiju.ui.detail

import androidx.lifecycle.SavedStateHandle
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
import kotlin.math.round

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

        private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 1)
        val messages: SharedFlow<String> = _messages.asSharedFlow()

        private var userTouchedInput = false

        init {
            getDayDetail(date)
                .onEach { detail ->
                    val current = _state.value
                    _state.value =
                        current.copy(
                            weight = detail.weight,
                            memos = detail.memos,
                            inputWeightKg =
                                if (!userTouchedInput) {
                                    detail.weight?.weightKg?.roundTo1() ?: current.inputWeightKg
                                } else {
                                    current.inputWeightKg
                                },
                        )
                }
                .launchIn(viewModelScope)
        }

        fun onValueChange(value: Double) {
            userTouchedInput = true
            _state.value = _state.value.copy(inputWeightKg = value.roundTo1())
        }

        fun onSaveWeight() {
            val value = _state.value.inputWeightKg
            val hadExisting = _state.value.weight != null
            viewModelScope.launch {
                _state.value = _state.value.copy(saving = true)
                val res = upsertWeight(date, value)
                _state.value = _state.value.copy(saving = false)
                if (res.isSuccess) {
                    _messages.emit(if (hadExisting) "体重を更新しました" else "体重を記録しました")
                } else {
                    _messages.emit(res.exceptionOrNull()?.message ?: "保存に失敗しました")
                }
            }
        }

        fun onDeleteWeight() {
            viewModelScope.launch {
                deleteWeight(date)
                userTouchedInput = false
                _messages.emit("体重を削除しました")
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
                    _messages.emit(if (editing == null) "メモを追加しました" else "メモを更新しました")
                } else {
                    _messages.emit(res.exceptionOrNull()?.message ?: "メモの保存に失敗しました")
                }
            }
        }

        fun onDeleteMemo(id: Long) {
            viewModelScope.launch {
                deleteMemo(id)
                _messages.emit("メモを削除しました")
            }
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

        private fun Double.roundTo1(): Double = round(this * 10) / 10
    }
