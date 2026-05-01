package dk.itu.moapd.x9.mhiv.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import dk.itu.moapd.x9.mhiv.R
import kotlinx.coroutines.delay

private const val LOCATION_FETCH_TIMEOUT_MS = 15_000L

@Composable
fun LocationWrapper(
    onBack: () -> Unit,
    onStartTracking: () -> Unit,
    onStopTracking: () -> Unit,
    onCollectLocations: (onLocation: (Location) -> Unit) -> Unit,
    content: @Composable (Location) -> Unit
) {
    val context = LocalContext.current

    var hasLocationPermission by remember {
        mutableStateOf(hasLocationPermission(context))
    }

    var location by remember {
        mutableStateOf<Location?>(null)
    }
    var locationTimedOut by remember {
        mutableStateOf(false)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        if (granted) {
            locationTimedOut = false
            onStartTracking()
        }
    }

    LaunchedEffect(Unit) {
        requestOrStartTracking(
            context = context,
            onHasPermission = {
                hasLocationPermission = true
                locationTimedOut = false
                onStartTracking()
            },
            onRequestPermission = {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        )
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            onCollectLocations { currentLocation ->
                locationTimedOut = false
                location = currentLocation
            }
        }
    }

    LaunchedEffect(hasLocationPermission, location) {
        if (hasLocationPermission && location == null) {
            delay(LOCATION_FETCH_TIMEOUT_MS)
            if (location == null) {
                locationTimedOut = true
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            onStopTracking()
        }
    }

    when {
        !hasLocationPermission -> {
            LocationPermissionDeniedScreen(
                onBack = onBack
            )
        }

        locationTimedOut -> {
            Text(stringResource(R.string.location_fetch_timeout_message))
        }

        location == null -> {
            Text(stringResource(R.string.location_fetching_message))
        }

        else -> {
            content(location!!)
        }
    }
}

private fun requestOrStartTracking(
    context: Context,
    onHasPermission: () -> Unit,
    onRequestPermission: () -> Unit
) {
    if (hasLocationPermission(context)) {
        onHasPermission()
    } else {
        onRequestPermission()
    }
}

private fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED
}

@Composable
private fun LocationPermissionDeniedScreen(
    onBack: () -> Unit
) {
    val screenBackground = colorResource(R.color.background_light)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackground)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(8.dp)
                .size(48.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_arrow_back_24),
                contentDescription = stringResource(R.string.back_button_content_description),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.horizontal_padding))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.location_permission_required_message),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
