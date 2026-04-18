package net.meshpeak.taiju.domain.usecase

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.meshpeak.taiju.data.csv.CsvImporter
import net.meshpeak.taiju.data.csv.ImportSummary
import net.meshpeak.taiju.data.csv.ParsedBackup
import net.meshpeak.taiju.di.IoDispatcher
import net.meshpeak.taiju.domain.repository.MemoRepository
import net.meshpeak.taiju.domain.repository.WeightEntryRepository
import javax.inject.Inject

class ImportCsvUseCase
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val weightRepo: WeightEntryRepository,
        private val memoRepo: MemoRepository,
        private val importer: CsvImporter,
        @IoDispatcher private val io: CoroutineDispatcher,
    ) {
        suspend fun preview(uri: Uri): Result<ParsedBackup> =
            withContext(io) {
                runCatching {
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        importer.readZip(input)
                    } ?: error("入力ファイルを開けませんでした")
                }
            }

        suspend fun commit(parsed: ParsedBackup): Result<ImportSummary> =
            withContext(io) {
                runCatching {
                    val existingDates = weightRepo.existingDates()
                    val conflicts = parsed.weights.map { it.date }.filter { it in existingDates }
                    weightRepo.upsertAll(parsed.weights)
                    memoRepo.replaceDatesWith(parsed.memos)
                    ImportSummary(
                        importedWeights = parsed.weights.size,
                        importedMemos = parsed.memos.size,
                        conflicts = conflicts,
                    )
                }
            }
    }
