package net.meshpeak.taiju.ui.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.meshpeak.taiju.BuildConfig

@Composable
fun AppVersionSection() {
    val versionText =
        buildString {
            append(BuildConfig.VERSION_NAME)
            append(" (")
            append(BuildConfig.VERSION_CODE)
            append(")")
            if (BuildConfig.DEBUG) {
                append(" — debug")
            }
        }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("バージョン情報", style = MaterialTheme.typography.titleMedium)
            Text(versionText, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
