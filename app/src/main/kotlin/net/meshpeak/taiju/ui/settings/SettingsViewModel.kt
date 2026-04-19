package net.meshpeak.taiju.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.meshpeak.taiju.domain.model.AppTheme
import net.meshpeak.taiju.domain.usecase.ExportCsvUseCase
import net.meshpeak.taiju.domain.usecase.GetUserSettingsUseCase
import net.meshpeak.taiju.domain.usecase.ImportCsvUseCase
import net.meshpeak.taiju.domain.usecase.UpdateTargetWeightUseCase
import net.meshpeak.taiju.domain.usecase.UpdateThemeUseCase
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        getUserSettings: GetUserSettingsUseCase,
        private val updateTheme: UpdateThemeUseCase,
        private val updateTargetWeight: UpdateTargetWeightUseCase,
        private val exportCsv: ExportCsvUseCase,
        private val importCsv: ImportCsvUseCase,
    ) : ViewModel() {
        private val _state = MutableStateFlow(SettingsUiState())
        val state = _state.asStateFlow()

        init {
            getUserSettings()
                .onEach { settings ->
                    val current = _state.value
                    _state.value =
                        current.copy(
                            theme = settings.theme,
                            targetWeightKg = settings.targetWeightKg,
                            targetWeightInput =
                                if (current.targetWeightInput.isBlank()) {
                                    settings.targetWeightKg?.let { "%.1f".format(it) } ?: ""
                                } else {
                                    current.targetWeightInput
                                },
                        )
                }
                .launchIn(viewModelScope)
        }

        fun onThemeChange(theme: AppTheme) {
            viewModelScope.launch { updateTheme(theme) }
        }

        fun onTargetWeightInputChange(value: String) {
            _state.value = _state.value.copy(targetWeightInput = value, targetErrorMessage = null)
        }

        fun onSaveTargetWeight() {
            val raw = _state.value.targetWeightInput.trim()
            if (raw.isEmpty()) {
                viewModelScope.launch { updateTargetWeight(null) }
                return
            }
            val value = raw.toDoubleOrNull()
            if (value == null || value !in 10.0..300.0) {
                _state.value = _state.value.copy(targetErrorMessage = "10.0〜300.0の範囲で入力してください")
                return
            }
            viewModelScope.launch {
                updateTargetWeight(value)
                _state.value = _state.value.copy(targetErrorMessage = null)
            }
        }

        fun onClearTargetWeight() {
            _state.value = _state.value.copy(targetWeightInput = "")
            viewModelScope.launch { updateTargetWeight(null) }
        }

        fun onCsvMessage(message: String?) {
            _state.value = _state.value.copy(csvMessage = message)
        }

        fun onExportResolved(uri: Uri?) {
            if (uri == null) {
                _state.value = _state.value.copy(csvMessage = "エクスポートをキャンセルしました")
                return
            }
            viewModelScope.launch {
                _state.value = _state.value.copy(csvMessage = "エクスポート中…")
                val result = exportCsv(uri)
                _state.value =
                    _state.value.copy(
                        csvMessage =
                            if (result.isSuccess) {
                                "エクスポートが完了しました"
                            } else {
                                "エクスポートに失敗しました: ${result.exceptionOrNull()?.message}"
                            },
                    )
            }
        }

        fun onImportResolved(uri: Uri?) {
            if (uri == null) {
                _state.value = _state.value.copy(csvMessage = "インポートをキャンセルしました")
                return
            }
            viewModelScope.launch {
                _state.value = _state.value.copy(csvMessage = "インポート中…")
                val previewResult = importCsv.preview(uri)
                val parsed =
                    previewResult.getOrElse {
                        _state.value = _state.value.copy(csvMessage = "読み込みに失敗しました: ${it.message}")
                        return@launch
                    }
                val commitResult = importCsv.commit(parsed)
                _state.value =
                    _state.value.copy(
                        csvMessage =
                            commitResult.fold(
                                onSuccess = { s ->
                                    val conflictNote =
                                        if (s.conflicts.isNotEmpty()) {
                                            " (${s.conflicts.size}日分を上書き)"
                                        } else {
                                            ""
                                        }
                                    "インポート完了: 体重${s.importedWeights}件 / メモ${s.importedMemos}件$conflictNote"
                                },
                                onFailure = { "インポートに失敗しました: ${it.message}" },
                            ),
                    )
            }
        }
    }
