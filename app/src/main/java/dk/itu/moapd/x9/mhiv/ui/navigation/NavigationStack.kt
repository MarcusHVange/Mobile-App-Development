package dk.itu.moapd.x9.mhiv.ui.navigation

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
import dk.itu.moapd.x9.mhiv.ui.composables.TrafficReportScreen
import dk.itu.moapd.x9.mhiv.ui.shared.DataViewModel
import dk.itu.moapd.x9.mhiv.ui.shared.SessionViewModel

@Composable
fun NavigationStack(
    dataViewModel: DataViewModel,
    sessionViewModel: SessionViewModel,
    onStartLoginActivity: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    var reportCreated by rememberSaveable { mutableStateOf(false) }

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
                val uiState by dataViewModel.uiState.collectAsStateWithLifecycle()
                val isLoggedIn by sessionViewModel.isLoggedIn.observeAsState(false)
                val databaseErrorMessage by dataViewModel.databaseErrorMessage.observeAsState()

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

            composable(route = Screen.Maps.route) {
                MapsScreen()
            }

            composable(route = Screen.TrafficReport.route) {
                LaunchedEffect(Unit) {
                    if (FirebaseAuth.getInstance().currentUser == null) {
                        onStartLoginActivity(true)
                    }
                }

                TrafficReportScreen(
                    onBack = { navController.navigateUp() },
                    onSubmit = { title, type, description, priority ->
                        dataViewModel.insertTrafficReport(
                            reportTitle = title,
                            reportType = type,
                            reportDescription = description,
                            reportPriority = priority
                        )

                        reportCreated = true
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}
