package net.meshpeak.taiju.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.ui.calendar.components.MonthGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onOpenDetail: (LocalDate) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("カレンダー") })
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                IconButton(onClick = viewModel::onPrevMonth) {
                    Icon(Icons.Outlined.ChevronLeft, contentDescription = "前の月")
                }
                Text(
                    text = "%d年 %d月".format(state.year, state.month),
                    style = MaterialTheme.typography.titleLarge,
                )
                IconButton(onClick = viewModel::onNextMonth) {
                    Icon(Icons.Outlined.ChevronRight, contentDescription = "次の月")
                }
                TextButton(onClick = viewModel::onBackToToday) { Text("今月") }
            }
            MonthGrid(
                year = state.year,
                month = state.month,
                today = state.today,
                weights = state.weights,
                onDayClick = onOpenDetail,
            )
        }
    }
}
