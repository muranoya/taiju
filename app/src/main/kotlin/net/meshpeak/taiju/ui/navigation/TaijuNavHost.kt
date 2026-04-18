package net.meshpeak.taiju.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun TaijuNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = TaijuDestination.Home.route,
        modifier = modifier,
    ) {
        composable(TaijuDestination.Home.route) {
            PlaceholderScreen(label = "Home")
        }
        composable(TaijuDestination.Calendar.route) {
            PlaceholderScreen(label = "Calendar")
        }
        composable(TaijuDestination.Chart.route) {
            PlaceholderScreen(label = "Chart")
        }
        composable(
            route = TaijuDestination.Detail.route,
            arguments = listOf(navArgument(TaijuDestination.Detail.ARG_DATE) { type = NavType.StringType }),
        ) { backStack ->
            val date = backStack.arguments?.getString(TaijuDestination.Detail.ARG_DATE).orEmpty()
            PlaceholderScreen(label = "Detail: $date")
        }
        composable(TaijuDestination.Settings.route) {
            PlaceholderScreen(label = "Settings")
        }
    }
}

@Composable
private fun PlaceholderScreen(label: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = label)
    }
}
