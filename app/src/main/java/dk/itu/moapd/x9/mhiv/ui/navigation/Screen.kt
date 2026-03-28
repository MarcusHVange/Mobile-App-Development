package dk.itu.moapd.x9.mhiv.ui.navigation

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object Maps : Screen("maps")
    data object TrafficReport : Screen("traffic_report")
}