package net.meshpeak.taiju.domain.model

data class UserSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val targetWeightKg: Double? = null,
)
