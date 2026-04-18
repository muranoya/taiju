package net.meshpeak.taiju.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class TaijuDestination(val route: String) {
    data object Home : TaijuDestination("home")

    data object Calendar : TaijuDestination("calendar")

    data object Chart : TaijuDestination("chart")

    data object Settings : TaijuDestination("settings")

    data object Detail : TaijuDestination("detail/{date}") {
        const val ARG_DATE = "date"

        fun routeFor(isoDate: String): String = "detail/$isoDate"
    }
}

data class TopLevelDestination(
    val destination: TaijuDestination,
    val label: String,
    val icon: ImageVector,
)

val topLevelDestinations: List<TopLevelDestination> =
    listOf(
        TopLevelDestination(TaijuDestination.Home, "ホーム", Icons.Outlined.Home),
        TopLevelDestination(TaijuDestination.Calendar, "カレンダー", Icons.Outlined.CalendarMonth),
        TopLevelDestination(TaijuDestination.Chart, "グラフ", Icons.AutoMirrored.Outlined.ShowChart),
        TopLevelDestination(TaijuDestination.Settings, "設定", Icons.Outlined.Settings),
    )
