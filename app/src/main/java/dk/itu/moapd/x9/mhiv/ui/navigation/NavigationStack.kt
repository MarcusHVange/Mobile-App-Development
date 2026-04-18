package dk.itu.moapd.x9.mhiv.ui.navigation

import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.mhiv.R
import dk.itu.moapd.x9.mhiv.ui.composables.BottomNavigationBar
import dk.itu.moapd.x9.mhiv.ui.composables.MainScreen
import dk.itu.moapd.x9.mhiv.ui.composables.MapsScreen
import dk.itu.moapd.x9.mhiv.ui.composables.TrafficReportDetails
import dk.itu.moapd.x9.mhiv.ui.composables.TrafficReportScreen
import dk.itu.moapd.x9.mhiv.ui.main.LocationWrapper
import dk.itu.moapd.x9.mhiv.ui.shared.DataViewModel
import dk.itu.moapd.x9.mhiv.ui.shared.SessionViewModel

@Composable
fun NavigationStack(
    dataViewModel: DataViewModel,
    sessionViewModel: SessionViewModel,
    onStartLoginActivity: (Boolean) -> Unit,
    onStartTracking: () -> Unit,
    onStopTracking: () -> Unit,
    onCollectLocations: (onLocation: (Location) -> Unit) -> Unit,
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    var reportCreated by rememberSaveable { mutableStateOf(false) }

    val uiState by dataViewModel.uiState.collectAsStateWithLifecycle()
    val isLoggedIn by sessionViewModel.isLoggedIn.observeAsState(false)
    val databaseErrorMessage by dataViewModel.databaseErrorMessage.observeAsState()

    val screensWithBottomNav = listOf(Screen.Main.route, Screen.Maps.route)
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in screensWithBottomNav

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Main.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Main.route) {
                LaunchedEffect(reportCreated) {
                    if (reportCreated) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.report_created_message),
                            Toast.LENGTH_SHORT
                        ).show()

                        reportCreated = false
                    }
                }

                LaunchedEffect(databaseErrorMessage) {
                    val messageRes = databaseErrorMessage ?: return@LaunchedEffect

                    Toast.makeText(
                        context,
                        context.getString(messageRes),
                        Toast.LENGTH_SHORT
                    ).show()
                    dataViewModel.clearDatabaseErrorMessage()
                }

                MainScreen(
                    reports = uiState.reports,
                    userId = uiState.userId,
                    isLoggedIn = isLoggedIn,
                    onAddReportNavigate = {
                        navController.navigate(Screen.TrafficReport.route)
                    },
                    onReportClick = { report ->
                        navController.navigate(Screen.TrafficReportDetails.createRoute(report.id))
                    },
                    onDelete = dataViewModel::deleteTrafficReport,
                    authAction = { loggedIn ->
                        if (loggedIn) {
                            sessionViewModel.signOut()
                        } else {
                            onStartLoginActivity(false)
                        }
                    }
                )
            }

            composable(route = Screen.TrafficReportDetails.route) { backStackEntry ->
                val reportId = backStackEntry.arguments?.getString("reportId")
                val report = uiState.reports.firstOrNull { it.id == reportId }

                report?.let {
                    TrafficReportDetails(
                        report = it,
                        onBack = { navController.navigateUp() }
                    )
                }
            }

            composable(route = Screen.Maps.route) {
                LocationWrapper(
                    onBack = { navController.navigateUp() },
                    onStartTracking=onStartTracking,
                    onStopTracking=onStopTracking,
                    onCollectLocations=onCollectLocations
                ) { location ->
                    MapsScreen(
                        uiState.reports,
                        location
                    )
                }
            }

            composable(route = Screen.TrafficReport.route) {
                LaunchedEffect(Unit) {
                    if (FirebaseAuth.getInstance().currentUser == null) {
                        onStartLoginActivity(true)
                    }
                }

                LocationWrapper(
                    onBack = { navController.navigateUp() },
                    onStartTracking=onStartTracking,
                    onStopTracking=onStopTracking,
                    onCollectLocations=onCollectLocations
                ) { location ->
                    TrafficReportScreen(
                        onBack = { navController.navigateUp() },
                        openCameraOnStart = true,
                        onSubmit = { title, type, description, priority, photoUri ->
                            dataViewModel.insertTrafficReport(
                                context = context.applicationContext,
                                reportTitle = title,
                                reportType = type,
                                reportDescription = description,
                                reportPriority = priority,
                                latitude = location.latitude,
                                longitude = location.longitude,
                                photoUri = photoUri
                            )

                            reportCreated = true
                            navController.navigateUp()
                        }
                    )
                }
            }
        }
    }
}
