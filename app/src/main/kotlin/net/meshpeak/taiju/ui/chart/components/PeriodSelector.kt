package net.meshpeak.taiju.ui.chart.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.meshpeak.taiju.domain.model.Period

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodSelector(
    selected: Period,
    onChange: (Period) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = listOf(Period.WEEK to "1週間", Period.MONTH to "1ヶ月", Period.QUARTER to "3ヶ月", Period.ALL to "全期間")
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        options.forEachIndexed { index, (period, label) ->
            SegmentedButton(
                selected = period == selected,
                onClick = { onChange(period) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
            ) { Text(label) }
        }
    }
}
