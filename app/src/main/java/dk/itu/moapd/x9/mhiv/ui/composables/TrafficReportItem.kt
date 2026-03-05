package dk.itu.moapd.x9.mhiv.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.itu.moapd.x9.mhiv.domain.model.TrafficReportModel

@Composable
fun TrafficReportItem(
    reportData: TrafficReportModel
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(text = "reportTitle: " + reportData.reportTitle)
            Text(text = "reportType: " + reportData.reportType)
            Text(text = "reportDescription: " + reportData.reportDescription)
            Text(text = "reportPriority: " + reportData.reportPriority)
        }
    }
}