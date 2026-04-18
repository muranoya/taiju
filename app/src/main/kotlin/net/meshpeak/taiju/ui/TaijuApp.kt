package net.meshpeak.taiju.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TaijuApp() {
    Surface(modifier = Modifier.fillMaxSize()) {
        TaijuScaffold()
    }
}
