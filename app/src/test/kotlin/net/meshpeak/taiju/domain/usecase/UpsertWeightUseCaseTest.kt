package net.meshpeak.taiju.domain.usecase

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.domain.model.WeightEntry
import net.meshpeak.taiju.domain.repository.WeightEntryRepository
import org.junit.Assert.assertTrue
import org.junit.Test

class UpsertWeightUseCaseTest {
    private val date = LocalDate(2026, 4, 18)

    private fun useCase(repo: WeightEntryRepository = stubRepo()): UpsertWeightUseCase = UpsertWeightUseCase(repo)

    private fun stubRepo(): WeightEntryRepository =
        mockk(relaxed = true) {
            io.mockk.every { observeAll() } returns emptyFlow<List<WeightEntry>>()
        }

    @Test
    fun `rejects weight below minimum`() =
        runTest {
            val result = useCase().invoke(date, 5.0)
            assertTrue(result.isFailure)
        }

    @Test
    fun `rejects weight above maximum`() =
        runTest {
            val result = useCase().invoke(date, 350.0)
            assertTrue(result.isFailure)
        }

    @Test
    fun `accepts weight at lower bound`() =
        runTest {
            val repo = stubRepo()
            val result = useCase(repo).invoke(date, 10.0)
            assertTrue(result.isSuccess)
            coVerify { repo.upsert(date, 10.0) }
        }

    @Test
    fun `accepts typical weight`() =
        runTest {
            val repo = stubRepo()
            val result = useCase(repo).invoke(date, 68.4)
            assertTrue(result.isSuccess)
            coVerify { repo.upsert(date, 68.4) }
        }
}
