package net.meshpeak.taiju

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import net.meshpeak.taiju.ui.RootViewModel
import net.meshpeak.taiju.ui.TaijuApp
import net.meshpeak.taiju.ui.theme.TaijuTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val rootViewModel: RootViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by rootViewModel.settings.collectAsStateWithLifecycle()
            TaijuTheme(
                appTheme = settings.theme,
            ) {
                TaijuApp()
            }
        }
    }
}
