package net.meshpeak.taiju.data.csv

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.domain.model.Memo
import net.meshpeak.taiju.domain.model.WeightEntry
import java.io.InputStream
import java.util.zip.ZipInputStream
import javax.inject.Inject

class CsvImporter
    @Inject
    constructor() {
        fun readZip(input: InputStream): ParsedBackup {
            var weightsText: String? = null
            var memosText: String? = null
            ZipInputStream(input).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    val name = entry.name
                    val bytes = zip.readBytes()
                    when (name) {
                        CsvSchema.WEIGHTS_FILE -> weightsText = bytes.toString(Charsets.UTF_8)
                        CsvSchema.MEMOS_FILE -> memosText = bytes.toString(Charsets.UTF_8)
                    }
                    zip.closeEntry()
                    entry = zip.nextEntry
                }
            }
            val weights = weightsText?.let(::parseWeights).orEmpty()
            val memos = memosText?.let(::parseMemos).orEmpty()
            return ParsedBackup(weights = weights, memos = memos)
        }

        private fun parseWeights(text: String): List<WeightEntry> {
            val rows = CsvReader.parse(text)
            if (rows.isEmpty()) return emptyList()
            val header = rows.first().map { it.trim() }
            require(header.size >= 2 && header[0] == CsvSchema.WEIGHTS_HEADER[0] && header[1] == CsvSchema.WEIGHTS_HEADER[1]) {
                "weights.csv header mismatch: $header"
            }
            val placeholder = Instant.fromEpochMilliseconds(0L)
            return rows.drop(1)
                .filter { it.size >= 2 && it[0].isNotBlank() }
                .map { row ->
                    WeightEntry(
                        id = 0,
                        date = LocalDate.parse(row[0].trim()),
                        weightKg =
                            row[1].trim().toDoubleOrNull()
                                ?: error("invalid weight value: ${row[1]}"),
                        createdAt = placeholder,
                        updatedAt = placeholder,
                    )
                }
        }

        private fun parseMemos(text: String): List<Memo> {
            val rows = CsvReader.parse(text)
            if (rows.isEmpty()) return emptyList()
            val header = rows.first().map { it.trim() }
            require(
                header.size >= 3 &&
                    header[0] == CsvSchema.MEMOS_HEADER[0] &&
                    header[1] == CsvSchema.MEMOS_HEADER[1] &&
                    header[2] == CsvSchema.MEMOS_HEADER[2],
            ) {
                "memos.csv header mismatch: $header"
            }
            val placeholder = Instant.fromEpochMilliseconds(0L)
            return rows.drop(1)
                .filter { it.size >= 3 && it[0].isNotBlank() }
                .map { row ->
                    Memo(
                        id = 0,
                        date = LocalDate.parse(row[0].trim()),
                        content = row[2],
                        sortOrder = row[1].trim().toIntOrNull() ?: 0,
                        createdAt = placeholder,
                        updatedAt = placeholder,
                    )
                }
        }
    }
