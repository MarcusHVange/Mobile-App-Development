package dk.itu.moapd.x9.mhiv.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dk.itu.moapd.x9.mhiv.R
import dk.itu.moapd.x9.mhiv.domain.model.TrafficReportModel

@Composable
fun TrafficReportItem(
    reportData: TrafficReportModel,
    onDelete: () -> Unit,
    isLoggedIn: Boolean
) {
    val labelColor = MaterialTheme.colorScheme.onSurface
    val valueColor = MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(priorityIconCount(reportData.reportPriority)) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_error_24),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = labelColor
                        )
                    }
                }

                if(isLoggedIn) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            painter = painterResource(R.drawable.outline_delete_24),
                            contentDescription = "Delete report",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Text(
                text = reportData.reportTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = labelColor
            )

            Text(
                text = labeledText(
                    label = "Type",
                    value = reportData.reportType,
                    labelColor = labelColor
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = valueColor
            )

            Text(
                text = labeledText(
                    label = "Description",
                    value = reportData.reportDescription,
                    labelColor = labelColor
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = valueColor
            )
        }
    }
}

private fun priorityIconCount(priority: String): Int = when (priority.trim().lowercase()) {
    "minor" -> 1
    "moderate" -> 2
    "major" -> 3
    else -> 0
}

private fun labeledText(
    label: String,
    value: String,
    labelColor: Color
) = buildAnnotatedString {
    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, color = labelColor)) {
        append("$label: ")
    }
    append(value)
}
