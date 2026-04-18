package net.meshpeak.taiju.ui.chart.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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

@Composable
fun WeightLineChart(
    points: List<WeightEntry>,
    targetWeightKg: Double?,
    modifier: Modifier = Modifier,
) {
    val producer = remember { CartesianChartModelProducer() }
    LaunchedEffect(points, targetWeightKg) {
        if (points.isEmpty()) return@LaunchedEffect
        producer.runTransaction {
            lineSeries {
                series(points.map { it.weightKg })
                if (targetWeightKg != null) {
                    series(List(points.size) { targetWeightKg })
                }
            }
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
            modifier
                .fillMaxWidth()
                .height(280.dp),
        scrollState = rememberVicoScrollState(scrollEnabled = false),
    )
}
