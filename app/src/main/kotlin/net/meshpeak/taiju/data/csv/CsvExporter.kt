package net.meshpeak.taiju.data.csv

import net.meshpeak.taiju.domain.model.Memo
import net.meshpeak.taiju.domain.model.WeightEntry
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject

class CsvExporter
    @Inject
    constructor() {
        fun writeZip(
            output: OutputStream,
            weights: List<WeightEntry>,
            memos: List<Memo>,
        ) {
            ZipOutputStream(output).use { zip ->
                writeEntry(zip, CsvSchema.WEIGHTS_FILE, buildWeightsCsv(weights))
                writeEntry(zip, CsvSchema.MEMOS_FILE, buildMemosCsv(memos))
            }
        }

        private fun buildWeightsCsv(weights: List<WeightEntry>): String =
            CsvWriter.write(
                header = CsvSchema.WEIGHTS_HEADER,
                rows =
                    weights
                        .sortedBy { it.date }
                        .map { entry ->
                            listOf(entry.date.toString(), "%.1f".format(entry.weightKg))
                        },
            )

        private fun buildMemosCsv(memos: List<Memo>): String =
            CsvWriter.write(
                header = CsvSchema.MEMOS_HEADER,
                rows =
                    memos
                        .sortedWith(compareBy(Memo::date, Memo::sortOrder, Memo::id))
                        .map { memo ->
                            listOf(memo.date.toString(), memo.sortOrder.toString(), memo.content)
                        },
            )

        private fun writeEntry(
            zip: ZipOutputStream,
            name: String,
            content: String,
        ) {
            zip.putNextEntry(ZipEntry(name))
            zip.write(content.toByteArray(Charsets.UTF_8))
            zip.closeEntry()
        }
    }
