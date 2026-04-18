package net.meshpeak.taiju.domain.usecase

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import net.meshpeak.taiju.data.csv.CsvExporter
import net.meshpeak.taiju.di.IoDispatcher
import net.meshpeak.taiju.domain.repository.MemoRepository
import net.meshpeak.taiju.domain.repository.WeightEntryRepository
import javax.inject.Inject

class ExportCsvUseCase
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val weightRepo: WeightEntryRepository,
        private val memoRepo: MemoRepository,
        private val exporter: CsvExporter,
        @IoDispatcher private val io: CoroutineDispatcher,
    ) {
        suspend operator fun invoke(uri: Uri): Result<Unit> =
            withContext(io) {
                runCatching {
                    val weights = weightRepo.observeAll().first()
                    val memos = memoRepo.getAll()
                    context.contentResolver.openOutputStream(uri, "w")?.use { out ->
                        exporter.writeZip(out, weights, memos)
                    } ?: error("出力先を開けませんでした")
                }
            }
    }
