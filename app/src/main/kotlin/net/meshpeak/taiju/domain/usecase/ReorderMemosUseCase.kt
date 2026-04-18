package net.meshpeak.taiju.domain.usecase

import net.meshpeak.taiju.domain.repository.MemoRepository
import javax.inject.Inject

class ReorderMemosUseCase
    @Inject
    constructor(
        private val repo: MemoRepository,
    ) {
        suspend operator fun invoke(orderedIds: List<Long>) = repo.reorder(orderedIds)
    }
