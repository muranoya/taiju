package net.meshpeak.taiju.domain.usecase

import net.meshpeak.taiju.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateTargetWeightUseCase
    @Inject
    constructor(
        private val repo: SettingsRepository,
    ) {
        suspend operator fun invoke(kg: Double?) = repo.setTargetWeight(kg)
    }
