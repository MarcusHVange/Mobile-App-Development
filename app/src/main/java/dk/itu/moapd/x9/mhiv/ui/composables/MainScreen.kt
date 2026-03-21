package dk.itu.moapd.x9.mhiv.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dk.itu.moapd.x9.mhiv.R
import dk.itu.moapd.x9.mhiv.domain.model.TrafficReportModel

@Composable
fun MainScreen(
    reports: List<TrafficReportModel>,
    isLoggedIn: Boolean,
    onAddReportNavigate: () -> Unit,
    onDelete: (String) -> Unit,
    authAction: (Boolean) -> Unit
) {
    val screenBackground = colorResource(R.color.background_light)
    val screenHorizontalPadding = dimensionResource(R.dimen.horizontal_padding)
    val screenVerticalPadding = dimensionResource(R.dimen.vertical_padding)
    val sectionSpacing = dimensionResource(R.dimen.section_spacing_medium)
    val smallSpacerHeight = dimensionResource(R.dimen.main_screen_title_spacing_small)
    val largeSpacerHeight = dimensionResource(R.dimen.main_screen_title_spacing_large)
    val iconSpacing = dimensionResource(R.dimen.main_screen_icon_spacing)
    val maxContentWidth = dimensionResource(R.dimen.main_screen_content_width)
    val pillCornerRadius = dimensionResource(R.dimen.main_screen_pill_corner_radius)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackground),
        contentPadding = PaddingValues(
            horizontal = screenHorizontalPadding,
            vertical = screenVerticalPadding
        ),
        verticalArrangement = Arrangement.spacedBy(sectionSpacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = maxContentWidth)
            ) {
                Button(
                    onClick = { authAction(isLoggedIn) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = stringResource(
                            if (isLoggedIn) R.string.log_out else R.string.log_in
                        )
                    )
                }

                Spacer(modifier = Modifier.height(smallSpacerHeight))
                Text(
                    text = stringResource(R.string.app_name),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(largeSpacerHeight))

                if (isLoggedIn) {
                    Button(
                        onClick = onAddReportNavigate,
                        modifier = Modifier.align(Alignment.Start),
                        shape = RoundedCornerShape(pillCornerRadius),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(text = stringResource(R.string.add_new))
                        Spacer(modifier = Modifier.width(iconSpacing))
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                    }
                }
            }
        }

        itemsIndexed(reports) { _, report ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TrafficReportItem(
                    reportData = report,
                    isLoggedIn  = isLoggedIn,
                    onDelete = { onDelete(report.id) }
                )
            }
        }
    }
}
