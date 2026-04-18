package net.meshpeak.taiju.ui.navigation

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
