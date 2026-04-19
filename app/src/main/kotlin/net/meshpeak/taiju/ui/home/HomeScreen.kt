package net.meshpeak.taiju.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.domain.model.WeightEntry
import net.meshpeak.taiju.ui.components.WheelWeightPicker
import net.meshpeak.taiju.ui.home.components.HomeMiniChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenDetail: (LocalDate) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.messages.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("ホーム") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HomeMiniChart(points = state.recent)
            TodayWeightCard(
                today = state.today,
                input = state.inputWeightKg,
                saving = state.isSaving,
                currentWeight = state.todayWeight,
                onValueChange = viewModel::onValueChange,
                onSaveClick = viewModel::onSaveClicked,
                onOpenDetail = { onOpenDetail(state.today) },
            )
        }
    }
}

@Composable
private fun TodayWeightCard(
    today: LocalDate,
    input: Double,
    saving: Boolean,
    currentWeight: WeightEntry?,
    onValueChange: (Double) -> Unit,
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
            WheelWeightPicker(
                valueKg = input,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = onSaveClick,
                    enabled = !saving,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(if (currentWeight == null) "記録する" else "更新する")
                }
                IconButton(onClick = onOpenDetail) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "詳細を開く",
                    )
                }
            }
        }
    }
}
