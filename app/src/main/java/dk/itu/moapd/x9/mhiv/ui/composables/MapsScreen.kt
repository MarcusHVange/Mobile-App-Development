package dk.itu.moapd.x9.mhiv.ui.composables

import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import dk.itu.moapd.x9.mhiv.domain.model.TrafficReportModel

@Composable
fun MapsScreen(
    reports: List<TrafficReportModel>,
    location: Location
) {
    val userLatLng = LatLng(location.latitude, location.longitude)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLatLng, 16f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
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