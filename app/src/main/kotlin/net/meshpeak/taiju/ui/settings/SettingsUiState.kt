package net.meshpeak.taiju.ui.settings

import net.meshpeak.taiju.domain.model.AppTheme

data class SettingsUiState(
    val theme: AppTheme = AppTheme.SYSTEM,
    val targetWeightKg: Double? = null,
    val targetWeightInput: String = "",
    val targetErrorMessage: String? = null,
    val csvMessage: String? = null,
)
