package net.meshpeak.taiju.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.domain.model.Memo
import net.meshpeak.taiju.domain.model.WeightEntry
import net.meshpeak.taiju.ui.components.EmptyState
import net.meshpeak.taiju.ui.components.WeightNumberField
import net.meshpeak.taiju.ui.home.components.HomeMiniChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenDetail: (LocalDate) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("taiju") })
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TodayWeightCard(
                today = state.today,
                input = state.inputWeight,
                saving = state.isSaving,
                errorMessage = state.errorMessage,
                currentWeight = state.todayWeight,
                onInputChange = viewModel::onInputChange,
                onSaveClick = viewModel::onSaveClicked,
                onOpenDetail = { onOpenDetail(state.today) },
            )
            HomeMiniChart(points = state.recent)
            RecentMemoSection(
                memos = state.recentMemos,
                onOpenDetail = onOpenDetail,
            )
        }
    }
}

@Composable
private fun TodayWeightCard(
    today: LocalDate,
    input: String,
    saving: Boolean,
    errorMessage: String?,
    currentWeight: WeightEntry?,
    onInputChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onOpenDetail: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("今日の体重", style = MaterialTheme.typography.titleMedium)
            Text("$today", style = MaterialTheme.typography.bodySmall)
            WeightNumberField(
                value = input,
                onValueChange = onInputChange,
                isError = errorMessage != null,
                enabled = !saving,
                modifier = Modifier.fillMaxWidth(),
            )
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onSaveClick, enabled = !saving) {
                    Text(if (currentWeight == null) "記録する" else "更新する")
                }
                Button(onClick = onOpenDetail) {
                    Text("詳細を開く")
                }
            }
        }
    }
}

@Composable
private fun RecentMemoSection(
    memos: List<Memo>,
    onOpenDetail: (LocalDate) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("最近のメモ", style = MaterialTheme.typography.titleMedium)
            if (memos.isEmpty()) {
                EmptyState(
                    title = "メモはまだありません",
                    description = "体重の記録と合わせて日々の出来事を残しましょう",
                )
            } else {
                memos.forEachIndexed { idx, memo ->
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                    ) {
                        Text(
                            text = "${memo.date}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(memo.content, style = MaterialTheme.typography.bodyMedium)
                    }
                    if (idx < memos.lastIndex) HorizontalDivider()
                }
            }
        }
    }
}
