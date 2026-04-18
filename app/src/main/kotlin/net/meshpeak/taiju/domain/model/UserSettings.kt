package net.meshpeak.taiju.domain.model

data class UserSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val useDynamicColor: Boolean = true,
    val targetWeightKg: Double? = null,
)
