package net.meshpeak.taiju.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import net.meshpeak.taiju.ui.calendar.CalendarScreen
import net.meshpeak.taiju.ui.chart.ChartScreen
import net.meshpeak.taiju.ui.detail.DetailScreen
import net.meshpeak.taiju.ui.home.HomeScreen
import net.meshpeak.taiju.ui.settings.SettingsScreen

@Composable
fun TaijuNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = TaijuDestination.Home.route,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        composable(TaijuDestination.Home.route) {
            HomeScreen(
                onOpenDetail = { date ->
                    navController.navigate(TaijuDestination.Detail.routeFor(date.toString()))
                },
            )
        }
        composable(TaijuDestination.Calendar.route) {
            CalendarScreen(
                onOpenDetail = { date ->
                    navController.navigate(TaijuDestination.Detail.routeFor(date.toString()))
                },
            )
        }
        composable(TaijuDestination.Chart.route) {
            ChartScreen()
        }
        composable(
            route = TaijuDestination.Detail.route,
            arguments = listOf(navArgument(TaijuDestination.Detail.ARG_DATE) { type = NavType.StringType }),
        ) {
            DetailScreen(onBack = { navController.popBackStack() })
        }
        composable(TaijuDestination.Settings.route) {
            SettingsScreen()
        }
    }
}
