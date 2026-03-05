package dk.itu.moapd.x9.mhiv.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.itu.moapd.x9.mhiv.domain.model.TrafficReportModel

@Composable
fun TrafficReportList(
    reports: List<TrafficReportModel>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(reports) { report ->
            TrafficReportItem(report)
        }
    }
}