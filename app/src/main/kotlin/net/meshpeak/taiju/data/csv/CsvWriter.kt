package net.meshpeak.taiju.data.csv

object CsvWriter {
    private const val LF = "\n"

    fun write(
        header: List<String>,
        rows: List<List<String>>,
    ): String =
        buildString {
            appendRow(header)
            rows.forEach { appendRow(it) }
        }

    private fun StringBuilder.appendRow(row: List<String>) {
        row.forEachIndexed { index, value ->
            if (index > 0) append(',')
            append(escape(value))
        }
        append(LF)
    }

    private fun escape(value: String): String {
        val needsQuote = value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }
        if (!needsQuote) return value
        val inner = value.replace("\"", "\"\"")
        return "\"$inner\""
    }
}
