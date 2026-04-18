package net.meshpeak.taiju.domain.usecase

import net.meshpeak.taiju.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateDynamicColorUseCase
    @Inject
    constructor(
        private val repo: SettingsRepository,
    ) {
        suspend operator fun invoke(enabled: Boolean) = repo.setUseDynamicColor(enabled)
    }
