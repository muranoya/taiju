package net.meshpeak.taiju.ui.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.meshpeak.taiju.ui.components.WeightNumberField

@Composable
fun TargetWeightSection(
    input: String,
    current: Double?,
    errorMessage: String?,
    onInputChange: (String) -> Unit,
    onSave: () -> Unit,
    onClear: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("目標体重", style = MaterialTheme.typography.titleMedium)
            if (current != null) {
                Text(
                    text = "現在の目標: %.1f kg".format(current),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            WeightNumberField(
                value = input,
                onValueChange = onInputChange,
                isError = errorMessage != null,
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
                Button(onClick = onSave) { Text("保存") }
                if (current != null) {
                    OutlinedButton(onClick = onClear) { Text("解除") }
                }
            }
        }
    }
}
