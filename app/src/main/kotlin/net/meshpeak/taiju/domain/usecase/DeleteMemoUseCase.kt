package net.meshpeak.taiju.domain.usecase

import net.meshpeak.taiju.domain.repository.MemoRepository
import javax.inject.Inject

class DeleteMemoUseCase
    @Inject
    constructor(
        private val repo: MemoRepository,
    ) {
        suspend operator fun invoke(id: Long) = repo.delete(id)
    }
