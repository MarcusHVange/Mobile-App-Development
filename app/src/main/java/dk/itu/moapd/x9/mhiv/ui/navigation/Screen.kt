package dk.itu.moapd.x9.mhiv.ui.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object Maps : Screen("maps")
    data object TrafficReport : Screen("traffic_report")
    data object TrafficReportDetails : Screen("traffic_report_details/{reportId}") {
        fun createRoute(reportId: String) = "traffic_report_details/${Uri.encode(reportId)}"
    }
}