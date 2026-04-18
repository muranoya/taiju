package net.meshpeak.taiju.data.csv

import org.junit.Assert.assertEquals
import org.junit.Test

class CsvRoundTripTest {
    @Test
    fun `writer escapes comma quote and newline`() {
        val csv =
            CsvWriter.write(
                header = listOf("a", "b"),
                rows =
                    listOf(
                        listOf("plain", "with,comma"),
                        listOf("with\"quote", "line1\nline2"),
                    ),
            )
        val expected =
            "a,b\n" +
                "plain,\"with,comma\"\n" +
                "\"with\"\"quote\",\"line1\nline2\"\n"
        assertEquals(expected, csv)
    }

    @Test
    fun `reader round trips writer output`() {
        val header = listOf("date", "sort_order", "content")
        val rows =
            listOf(
                listOf("2026-04-18", "0", "朝ランニング 5km"),
                listOf("2026-04-18", "1", "夜は \"和食\", 鍋"),
                listOf("2026-04-19", "0", "line1\nline2"),
            )
        val text = CsvWriter.write(header, rows)
        val parsed = CsvReader.parse(text)
        assertEquals(listOf(header) + rows, parsed)
    }

    @Test
    fun `reader tolerates trailing newline absence`() {
        val text = "a,b\n1,2\n3,4"
        val parsed = CsvReader.parse(text)
        assertEquals(listOf(listOf("a", "b"), listOf("1", "2"), listOf("3", "4")), parsed)
    }
}
