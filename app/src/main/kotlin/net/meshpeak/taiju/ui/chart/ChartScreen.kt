package net.meshpeak.taiju.ui.chart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.meshpeak.taiju.ui.chart.components.PeriodSelector
import net.meshpeak.taiju.ui.chart.components.WeightLineChart
import net.meshpeak.taiju.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(viewModel: ChartViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("グラフ") }) },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PeriodSelector(
                selected = state.period,
                onChange = viewModel::onPeriodChange,
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text("体重の推移", style = MaterialTheme.typography.titleMedium)
                    if (state.points.size < 2) {
                        EmptyState(
                            title = "データが足りません",
                            description = "表示するには2日分以上の記録が必要です",
                        )
                    } else {
                        WeightLineChart(
                            points = state.points,
                            targetWeightKg = state.targetWeightKg,
                        )
                        if (state.targetWeightKg != null) {
                            Text(
                                text = "目標: %.1f kg".format(state.targetWeightKg),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }
}
