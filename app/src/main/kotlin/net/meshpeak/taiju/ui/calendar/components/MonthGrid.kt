package net.meshpeak.taiju.ui.calendar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

@Composable
fun MonthGrid(
    year: Int,
    month: Int,
    today: LocalDate,
    weights: Map<LocalDate, Double>,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val firstDay = LocalDate(year, month, 1)
    val daysInMonth = daysInMonth(year, month)
    val leadingEmpty =
        when (firstDay.dayOfWeek) {
            DayOfWeek.SUNDAY -> 0
            DayOfWeek.MONDAY -> 1
            DayOfWeek.TUESDAY -> 2
            DayOfWeek.WEDNESDAY -> 3
            DayOfWeek.THURSDAY -> 4
            DayOfWeek.FRIDAY -> 5
            DayOfWeek.SATURDAY -> 6
        }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("日", "月", "火", "水", "木", "金", "土").forEach { label ->
                Text(
                    text = label,
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(28.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
            }
        }
        val totalCells = leadingEmpty + daysInMonth
        val rows = (totalCells + 6) / 7
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                for (col in 0..6) {
                    val cellIndex = row * 7 + col
                    val dayNumber = cellIndex - leadingEmpty + 1
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (dayNumber in 1..daysInMonth) {
                            val date = LocalDate(year, month, dayNumber)
                            DayCell(
                                date = date,
                                weight = weights[date],
                                isToday = date == today,
                                onClick = { onDayClick(date) },
                            )
                        } else {
                            Spacer(modifier = Modifier.height(64.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun daysInMonth(
    year: Int,
    month: Int,
): Int =
    when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> 30
    }

private fun isLeapYear(year: Int): Boolean = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
