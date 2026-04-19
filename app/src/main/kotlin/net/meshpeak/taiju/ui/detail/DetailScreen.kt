package net.meshpeak.taiju.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DragHandle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import net.meshpeak.taiju.ui.components.WheelWeightPicker
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var pendingDeleteMemo by remember { mutableStateOf<Memo?>(null) }
    var showDeleteWeightDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.messages.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${state.date}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "戻る",
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                inputKg = state.inputWeightKg,
                saving = state.saving,
                hasExisting = state.weight != null,
                onValueChange = viewModel::onValueChange,
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
                MemoList(
                    memos = state.memos,
                    onMove = viewModel::onMoveMemo,
                    onEdit = viewModel::onStartEditMemo,
                    onDelete = { pendingDeleteMemo = it },
                )
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
    inputKg: Double,
    saving: Boolean,
    hasExisting: Boolean,
    onValueChange: (Double) -> Unit,
    onSave: () -> Unit,
    onRequestDelete: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("体重", style = MaterialTheme.typography.titleMedium)
            WheelWeightPicker(
                valueKg = inputKg,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
            )
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
private fun MemoList(
    memos: List<Memo>,
    onMove: (Int, Int) -> Unit,
    onEdit: (Memo) -> Unit,
    onDelete: (Memo) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val reorderState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            onMove(from.index, to.index)
        }
    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(memos, key = { it.id }) { memo ->
            ReorderableItem(reorderState, key = memo.id) { isDragging ->
                val elevation = if (isDragging) 6.dp else 0.dp
                Card(elevation = CardDefaults.cardElevation(defaultElevation = elevation)) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        IconButton(
                            onClick = {},
                            modifier = Modifier.longPressDraggableHandle(),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DragHandle,
                                contentDescription = "並び替えハンドル",
                            )
                        }
                        Text(
                            text = memo.content,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f).padding(vertical = 12.dp),
                        )
                        MemoOverflowMenu(
                            onEdit = { onEdit(memo) },
                            onDelete = { onDelete(memo) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MemoOverflowMenu(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = "メニューを開く",
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("編集") },
                leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
                onClick = {
                    expanded = false
                    onEdit()
                },
            )
            DropdownMenuItem(
                text = { Text("削除") },
                leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
                onClick = {
                    expanded = false
                    onDelete()
                },
            )
        }
    }
}
