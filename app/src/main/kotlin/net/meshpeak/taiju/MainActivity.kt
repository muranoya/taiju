package net.meshpeak.taiju

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import net.meshpeak.taiju.ui.TaijuApp
import net.meshpeak.taiju.ui.theme.TaijuTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaijuTheme {
                TaijuApp()
            }
        }
    }
}
