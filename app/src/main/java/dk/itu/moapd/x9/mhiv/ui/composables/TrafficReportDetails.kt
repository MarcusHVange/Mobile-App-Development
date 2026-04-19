package dk.itu.moapd.x9.mhiv.ui.composables

import android.net.Uri
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dk.itu.moapd.x9.mhiv.R
import dk.itu.moapd.x9.mhiv.domain.model.TrafficReportModel
import kotlinx.coroutines.delay

@Composable
fun TrafficReportDetails(
    report: TrafficReportModel,
    onBack: () -> Unit,
    loadPhotoUrl: suspend (String) -> Uri?
) {
    val screenBackground = colorResource(R.color.background_light)
    val contentPadding = dimensionResource(R.dimen.horizontal_padding)
    val fieldSpacing = dimensionResource(R.dimen.form_report_margin_bottom)

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
                contentDescription = stringResource(R.string.back_button_content_description)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(fieldSpacing)
        ) {
            if (report.photoUri.isNotBlank()) {
                FirebaseStoragePicassoImage(
                    path = report.photoUri,
                    loadPhotoUrl = loadPhotoUrl,
                    contentDescription = stringResource(R.string.content_description_report_image),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.9f)
                )

                if (report.photoCaption.isNotBlank()) {
                    Text(
                        text = stringResource(R.string.report_photo_caption, report.photoCaption),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(fieldSpacing))
            }

            Text(
                text = report.reportTitle,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            ReportDetail(
                label = stringResource(R.string.report_type_label),
                value = report.reportType
            )

            ReportDetail(
                label = stringResource(R.string.report_description_label),
                value = report.reportDescription
            )

            ReportDetail(
                label = stringResource(R.string.form_report_priority),
                value = report.reportPriority
            )
        }
    }
}

@Composable
private fun ReportDetail(
    label: String,
    value: String
) {
    Text(
        text = stringResource(R.string.report_field_label, label) + " " + value,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun FirebaseStoragePicassoImage(
    path: String,
    loadPhotoUrl: suspend (String) -> Uri?,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var url by remember(path) { mutableStateOf<String?>(null) }
    var isImageUnavailable by remember(path) { mutableStateOf(false) }
    var isImageLoaded by remember(path) { mutableStateOf(false) }
    var retryCount by remember(path) { mutableStateOf(0) }

    LaunchedEffect(path, retryCount) {
        if (retryCount > 0) {
            delay(IMAGE_DOWNLOAD_RETRY_DELAY_MS)
        }

        val downloadUrl = loadPhotoUrl(path)
        if (downloadUrl == null) {
            isImageUnavailable = true
            if (retryCount < IMAGE_DOWNLOAD_RETRIES) {
                retryCount += 1
            }
        } else {
            url = downloadUrl.toString()
            isImageUnavailable = true
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                ImageView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    this.contentDescription = contentDescription
                }
            },
            update = { imageView ->
                val resolvedUrl = url

                if (resolvedUrl == null) {
                    Picasso.get().cancelRequest(imageView)
                    imageView.setImageDrawable(null)
                } else {
                    Picasso.get()
                        .load(resolvedUrl)
                        .rotate(90f)
                        .fit()
                        .centerCrop()
                        .into(imageView, object : Callback {
                            override fun onSuccess() {
                                isImageLoaded = true
                                isImageUnavailable = false
                            }

                            override fun onError(e: Exception?) {
                                isImageLoaded = false
                                isImageUnavailable = true
                            }
                        })
                }
            }
        )

        if (isImageUnavailable && !isImageLoaded) {
            Text(
                text = stringResource(R.string.report_image_not_available),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private const val IMAGE_DOWNLOAD_RETRIES = 3
private const val IMAGE_DOWNLOAD_RETRY_DELAY_MS = 10_000L
