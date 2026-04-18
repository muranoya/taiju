package net.meshpeak.taiju.data.csv

object CsvReader {
    fun parse(text: String): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        val current = StringBuilder()
        val row = mutableListOf<String>()
        var inQuotes = false
        var i = 0
        while (i < text.length) {
            val ch = text[i]
            if (inQuotes) {
                when {
                    ch == '"' && i + 1 < text.length && text[i + 1] == '"' -> {
                        current.append('"')
                        i += 2
                        continue
                    }
                    ch == '"' -> {
                        inQuotes = false
                    }
                    else -> current.append(ch)
                }
            } else {
                when (ch) {
                    '"' -> inQuotes = true
                    ',' -> {
                        row.add(current.toString())
                        current.setLength(0)
                    }
                    '\n' -> {
                        row.add(current.toString())
                        current.setLength(0)
                        rows.add(row.toList())
                        row.clear()
                    }
                    '\r' -> {
                        // skip; LF will finalize the row
                    }
                    else -> current.append(ch)
                }
            }
            i++
        }
        if (current.isNotEmpty() || row.isNotEmpty()) {
            row.add(current.toString())
            rows.add(row.toList())
        }
        return rows
    }
}
