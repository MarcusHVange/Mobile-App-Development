package dk.itu.moapd.x9.mhiv.ui.composables

import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import dk.itu.moapd.x9.mhiv.domain.model.TrafficReportModel

@Composable
fun MapsScreen(
    reports: List<TrafficReportModel>,
    location: Location
) {
    val userLatLng = LatLng(location.latitude, location.longitude)
    var locationListener by remember {
        mutableStateOf<LocationSource.OnLocationChangedListener?>(null)
    }
    val locationSource = remember {
        object : LocationSource {
            override fun activate(listener: LocationSource.OnLocationChangedListener) {
                locationListener = listener
            }

            override fun deactivate() {
                locationListener = null
            }
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLatLng, 16f)
    }

    LaunchedEffect(locationListener, location) {
        locationListener?.onLocationChanged(location)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        locationSource = locationSource,
        properties = MapProperties(
            isMyLocationEnabled = true
        ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = true
        )
    ) {
        reports.forEach { report ->
            key(report.id) {
                val reportPosition = LatLng(report.latitude, report.longitude)

                Marker(
                    state = MarkerState(position = reportPosition),
                    title = report.reportTitle,
                    snippet = "Type: ${report.reportType}"
                )
            }
        }
    }
}