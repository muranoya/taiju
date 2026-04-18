package net.meshpeak.taiju.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import net.meshpeak.taiju.domain.model.WeightEntry
import net.meshpeak.taiju.ui.components.EmptyState

@Composable
fun HomeMiniChart(points: List<WeightEntry>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("直近7日の推移", style = MaterialTheme.typography.titleMedium)
            if (points.size < 2) {
                EmptyState(
                    title = "推移はまだ表示できません",
                    description = "数日分を記録するとミニグラフが表示されます",
                )
            } else {
                val producer = remember { CartesianChartModelProducer() }
                LaunchedEffect(points) {
                    producer.runTransaction {
                        lineSeries { series(points.map { it.weightKg }) }
                    }
                }
                CartesianChartHost(
                    chart =
                        rememberCartesianChart(
                            rememberLineCartesianLayer(),
                            startAxis = VerticalAxis.rememberStart(),
                            bottomAxis = HorizontalAxis.rememberBottom(),
                        ),
                    modelProducer = producer,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                    scrollState = rememberVicoScrollState(scrollEnabled = false),
                )
            }
        }
    }
}
