package net.meshpeak.taiju.data.csv

object CsvSchema {
    const val WEIGHTS_FILE = "weights.csv"
    const val MEMOS_FILE = "memos.csv"
    val WEIGHTS_HEADER = listOf("date", "weight_kg")
    val MEMOS_HEADER = listOf("date", "sort_order", "content")
}
