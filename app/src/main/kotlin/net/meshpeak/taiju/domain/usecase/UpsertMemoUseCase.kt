package net.meshpeak.taiju.domain.usecase

import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.domain.repository.MemoRepository
import javax.inject.Inject

class UpsertMemoUseCase
    @Inject
    constructor(
        private val repo: MemoRepository,
    ) {
        suspend fun append(
            date: LocalDate,
            content: String,
        ): Result<Unit> =
            runCatching {
                val trimmed = content.trim()
                require(trimmed.isNotEmpty()) { "memo content must not be blank" }
                repo.append(date, trimmed)
            }

        suspend fun update(
            id: Long,
            content: String,
        ): Result<Unit> =
            runCatching {
                val trimmed = content.trim()
                require(trimmed.isNotEmpty()) { "memo content must not be blank" }
                repo.update(id, trimmed)
            }
    }
