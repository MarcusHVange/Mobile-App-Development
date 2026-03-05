package dk.itu.moapd.x9.mhiv.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.itu.moapd.x9.mhiv.domain.model.TrafficReportModel

@Composable
fun MainScreen(
    reports: List<TrafficReportModel>,
    onAddReportNavigate: () -> Unit,
    onDelete: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "X9 App")
        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onAddReportNavigate) {
            Text("Add Report")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TrafficReportList(reports, onDelete)
    }
}