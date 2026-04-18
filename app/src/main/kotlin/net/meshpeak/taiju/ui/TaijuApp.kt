package net.meshpeak.taiju.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import net.meshpeak.taiju.ui.navigation.TaijuNavHost

@Composable
fun TaijuApp() {
    val navController = rememberNavController()
    Surface(modifier = Modifier.fillMaxSize()) {
        TaijuNavHost(navController = navController)
    }
}
