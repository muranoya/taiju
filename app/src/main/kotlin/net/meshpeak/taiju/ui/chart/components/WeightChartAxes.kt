package net.meshpeak.taiju.ui.chart.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.common.component.TextComponent
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.max

private val MonthDayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d")

/**
 * 直近データの平均を画面中央に置き、min/max からの最大偏差に 0.5kg のパディングを足した
 * 対称レンジで Y 軸を切る。データが少ない (2 点未満) 場合は Vico のデフォルト (auto) を返す。
 * [extraValues] には目標体重など、レンジに含めたい追加値を渡す。
 */
fun weightRangeProvider(
    values: List<Double>,
    extraValues: List<Double> = emptyList(),
): CartesianLayerRangeProvider {
    if (values.size < 2) return CartesianLayerRangeProvider.auto()
    val combined = values + extraValues
    val avg = values.average()
    val halfSpan = max(avg - combined.min(), combined.max() - avg) + 0.5
    return CartesianLayerRangeProvider.fixed(
        minY = avg - halfSpan,
        maxY = avg + halfSpan,
    )
}

/** X 軸 index → dates[index] を M/d 形式にフォーマット。範囲外は空文字。 */
fun dateAxisFormatter(dates: List<LocalDate>): CartesianValueFormatter =
    CartesianValueFormatter { _, value, _ ->
        val idx = value.toInt()
        dates.getOrNull(idx)?.toJavaLocalDate()?.format(MonthDayFormatter) ?: ""
    }

/** 軸ラベルにテーマの onSurface を当て、ライト/ダーク双方で読める色にする。 */
@Composable
fun rememberWeightAxisLabel(): TextComponent =
    rememberTextComponent(
        color = MaterialTheme.colorScheme.onSurface,
        textSize = 12.sp,
    )
