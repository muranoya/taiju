package net.meshpeak.taiju.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.meshpeak.taiju.domain.model.Memo
import net.meshpeak.taiju.ui.components.EmptyState
import net.meshpeak.taiju.ui.components.WeightNumberField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var pendingDeleteMemo by remember { mutableStateOf<Memo?>(null) }
    var showDeleteWeightDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${state.date}") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("戻る") }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::onStartAddMemo) {
                Text("＋")
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .imePadding()
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            WeightEditor(
                input = state.weightInput,
                errorMessage = state.errorMessage,
                saving = state.saving,
                hasExisting = state.weight != null,
                onInputChange = viewModel::onWeightInputChange,
                onSave = viewModel::onSaveWeight,
                onRequestDelete = { showDeleteWeightDialog = true },
            )

            Text("メモ", style = MaterialTheme.typography.titleMedium)
            if (state.memos.isEmpty()) {
                EmptyState(
                    title = "この日のメモはまだありません",
                    description = "右下の + ボタンからメモを追加できます",
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.memos, key = { it.id }) { memo ->
                        val idx = state.memos.indexOf(memo)
                        MemoRow(
                            memo = memo,
                            canMoveUp = idx > 0,
                            canMoveDown = idx < state.memos.lastIndex,
                            onMoveUp = { viewModel.onMoveMemo(idx, idx - 1) },
                            onMoveDown = { viewModel.onMoveMemo(idx, idx + 1) },
                            onEdit = { viewModel.onStartEditMemo(memo) },
                            onDelete = { pendingDeleteMemo = memo },
                        )
                    }
                }
            }
        }
    }

    if (state.isBottomSheetOpen) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = viewModel::onDismissSheet,
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp).imePadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = if (state.editingMemo == null) "メモを追加" else "メモを編集",
                    style = MaterialTheme.typography.titleMedium,
                )
                OutlinedTextField(
                    value = state.memoInput,
                    onValueChange = viewModel::onMemoInputChange,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = viewModel::onDismissSheet) { Text("キャンセル") }
                    Button(onClick = viewModel::onSubmitMemo) { Text("保存") }
                }
            }
        }
    }

    if (showDeleteWeightDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteWeightDialog = false },
            title = { Text("体重を削除しますか?") },
            text = { Text("この日の体重記録を削除します。") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onDeleteWeight()
                    showDeleteWeightDialog = false
                }) { Text("削除") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteWeightDialog = false }) { Text("キャンセル") }
            },
        )
    }

    pendingDeleteMemo?.let { target ->
        AlertDialog(
            onDismissRequest = { pendingDeleteMemo = null },
            title = { Text("メモを削除しますか?") },
            text = { Text(target.content.take(80)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onDeleteMemo(target.id)
                    pendingDeleteMemo = null
                }) { Text("削除") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteMemo = null }) { Text("キャンセル") }
            },
        )
    }
}

@Composable
private fun WeightEditor(
    input: String,
    errorMessage: String?,
    saving: Boolean,
    hasExisting: Boolean,
    onInputChange: (String) -> Unit,
    onSave: () -> Unit,
    onRequestDelete: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("体重", style = MaterialTheme.typography.titleMedium)
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
                Button(onClick = onSave, enabled = !saving) {
                    Text(if (hasExisting) "更新" else "保存")
                }
                if (hasExisting) {
                    OutlinedButton(onClick = onRequestDelete) { Text("削除") }
                }
            }
        }
    }
}

@Composable
private fun MemoRow(
    memo: Memo,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Card {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(memo.content, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onMoveUp, enabled = canMoveUp) {
                Icon(Icons.Outlined.ArrowUpward, contentDescription = "上へ")
            }
            IconButton(onClick = onMoveDown, enabled = canMoveDown) {
                Icon(Icons.Outlined.ArrowDownward, contentDescription = "下へ")
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Outlined.Edit, contentDescription = "編集")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, contentDescription = "削除")
            }
        }
    }
}
