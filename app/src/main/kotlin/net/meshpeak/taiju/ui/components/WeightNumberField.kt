package net.meshpeak.taiju.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun WeightNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "体重 (kg)",
    isError: Boolean = false,
    enabled: Boolean = true,
    onImeDone: (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { next ->
            if (next.length <= 6 && next.all { it.isDigit() || it == '.' }) {
                onValueChange(next)
            }
        },
        label = { Text(label) },
        modifier = modifier,
        enabled = enabled,
        singleLine = true,
        isError = isError,
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = if (onImeDone != null) ImeAction.Done else ImeAction.Default,
            ),
        suffix = { Text("kg") },
    )
}
