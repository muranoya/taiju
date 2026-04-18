package net.meshpeak.taiju.domain.usecase

import kotlinx.coroutines.flow.Flow
import net.meshpeak.taiju.domain.model.UserSettings
import net.meshpeak.taiju.domain.repository.SettingsRepository
import javax.inject.Inject

class GetUserSettingsUseCase
    @Inject
    constructor(
        private val repo: SettingsRepository,
    ) {
        operator fun invoke(): Flow<UserSettings> = repo.settings
    }
