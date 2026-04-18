package net.meshpeak.taiju.domain.usecase

import net.meshpeak.taiju.domain.model.AppTheme
import net.meshpeak.taiju.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateThemeUseCase
    @Inject
    constructor(
        private val repo: SettingsRepository,
    ) {
        suspend operator fun invoke(theme: AppTheme) = repo.setTheme(theme)
    }
