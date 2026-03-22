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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import dk.itu.moapd.x9.mhiv.R
import dk.itu.moapd.x9.mhiv.domain.model.TrafficReportModel

@Composable
fun TrafficReportItem(
    reportData: TrafficReportModel,
    onDelete: () -> Unit,
    isLoggedIn: Boolean,
    isReportOwnedByUser: Boolean
) {
    val cardCornerRadius = dimensionResource(R.dimen.card_corner_radius)
    val cardElevation = dimensionResource(R.dimen.card_elevation)
    val cardPadding = dimensionResource(R.dimen.card_padding)
    val cardItemSpacing = dimensionResource(R.dimen.form_report_margin_bottom)
    val iconRowSpacing = dimensionResource(R.dimen.form_report_label_margin_bottom)
    val iconSize = dimensionResource(R.dimen.traffic_report_item_icon_size)
    val labelColor = MaterialTheme.colorScheme.onSurface
    val valueColor = MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
    ) {
        Column(
            modifier = Modifier.padding(cardPadding),
            verticalArrangement = Arrangement.spacedBy(cardItemSpacing)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(iconRowSpacing),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(priorityIconCount(reportData.reportPriority)) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_error_24),
                            contentDescription = null,
                            modifier = Modifier.size(iconSize),
                            tint = labelColor
                        )
                    }
                }

                if (isLoggedIn && isReportOwnedByUser) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            painter = painterResource(R.drawable.outline_delete_24),
                            contentDescription = stringResource(R.string.delete_report),
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
                    label = stringResource(
                        R.string.report_field_label,
                        stringResource(R.string.report_type_label)
                    ),
                    value = reportData.reportType,
                    labelColor = labelColor
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = valueColor
            )

            Text(
                text = labeledText(
                    label = stringResource(
                        R.string.report_field_label,
                        stringResource(R.string.report_description_label)
                    ),
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
        append(label)
    }
    append(" ")
    append(value)
}
