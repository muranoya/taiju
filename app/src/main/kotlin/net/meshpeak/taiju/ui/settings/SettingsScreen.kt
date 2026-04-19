package net.meshpeak.taiju.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.meshpeak.taiju.ui.settings.components.AppVersionSection
import net.meshpeak.taiju.ui.settings.components.CsvSection
import net.meshpeak.taiju.ui.settings.components.TargetWeightSection
import net.meshpeak.taiju.ui.settings.components.ThemePickerSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val exportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri ->
            viewModel.onExportResolved(uri)
        }
    val importLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            viewModel.onImportResolved(uri)
        }

    Scaffold(
        topBar = { TopAppBar(title = { Text("設定") }) },
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
            ThemePickerSection(
                theme = state.theme,
                onThemeChange = viewModel::onThemeChange,
            )
            TargetWeightSection(
                input = state.targetWeightInput,
                current = state.targetWeightKg,
                errorMessage = state.targetErrorMessage,
                onInputChange = viewModel::onTargetWeightInputChange,
                onSave = viewModel::onSaveTargetWeight,
                onClear = viewModel::onClearTargetWeight,
            )
            CsvSection(
                message = state.csvMessage,
                onExport = { exportLauncher.launch("taiju-backup.zip") },
                onImport = {
                    importLauncher.launch(arrayOf("application/zip", "application/octet-stream"))
                },
            )
            AppVersionSection()
        }
    }
}
