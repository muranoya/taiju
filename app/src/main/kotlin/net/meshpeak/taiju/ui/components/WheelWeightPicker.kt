package net.meshpeak.taiju.ui.components

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlin.math.roundToInt

private const val VISIBLE_ROW_COUNT = 5
private val RowHeight = 40.dp

@Composable
fun WheelWeightPicker(
    valueKg: Double,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    minKg: Int = 10,
    maxKg: Int = 300,
) {
    val integers = remember(minKg, maxKg) { (minKg..maxKg).toList() }
    val fractions = remember { (0..9).toList() }

    val tenth = (valueKg * 10).roundToInt().coerceIn(minKg * 10, maxKg * 10 + 9)
    val currentInt = (tenth / 10).coerceIn(minKg, maxKg)
    val currentFrac = tenth % 10

    val intState = rememberLazyListState(initialFirstVisibleItemIndex = currentInt - minKg)
    val fracState = rememberLazyListState(initialFirstVisibleItemIndex = currentFrac)

    LaunchedEffect(currentInt) {
        if (intState.firstVisibleItemIndex != currentInt - minKg || intState.firstVisibleItemScrollOffset != 0) {
            intState.scrollToItem(currentInt - minKg)
        }
    }
    LaunchedEffect(currentFrac) {
        if (fracState.firstVisibleItemIndex != currentFrac || fracState.firstVisibleItemScrollOffset != 0) {
            fracState.scrollToItem(currentFrac)
        }
    }

    LaunchedEffect(intState, fracState, minKg) {
        snapshotFlow {
            if (intState.isScrollInProgress || fracState.isScrollInProgress) {
                null
            } else {
                (intState.firstVisibleItemIndex + minKg) to fracState.firstVisibleItemIndex
            }
        }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { (i, f) ->
                val next = i + f / 10.0
                if (kotlin.math.abs(next - valueKg) > 1e-6) {
                    onValueChange(next)
                }
            }
    }

    Row(
        modifier =
            modifier
                .height(RowHeight * VISIBLE_ROW_COUNT)
                .semantics { contentDescription = "体重 %.1f kg".format(valueKg) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        WheelColumn(
            state = intState,
            items = integers,
            label = { it.toString() },
            modifier = Modifier.weight(1f),
        )
        Text(
            text = ".",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
        WheelColumn(
            state = fracState,
            items = fractions,
            label = { it.toString() },
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "kg",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Composable
private fun <T> WheelColumn(
    state: LazyListState,
    items: List<T>,
    label: (T) -> String,
    modifier: Modifier = Modifier,
) {
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = state)
    Box(
        modifier = modifier.height(RowHeight * VISIBLE_ROW_COUNT),
        contentAlignment = Alignment.Center,
    ) {
        LazyColumn(
            state = state,
            flingBehavior = flingBehavior,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = RowHeight * 2),
        ) {
            items(items, key = { it.hashCode() }) { item ->
                val centerIndex = state.firstVisibleItemIndex
                val itemIndex = items.indexOf(item)
                val distance = kotlin.math.abs(itemIndex - centerIndex)
                Box(
                    modifier = Modifier.fillMaxWidth().height(RowHeight),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label(item),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium,
                        color =
                            if (distance == 0) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = (1f - (distance * 0.28f)).coerceIn(0.25f, 1f),
                                )
                            },
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = RowHeight * 2),
            color = MaterialTheme.colorScheme.outlineVariant,
        )
        HorizontalDivider(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = RowHeight * 3),
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}
