package dk.itu.moapd.x9.mhiv.ui.composables

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import dk.itu.moapd.x9.mhiv.R
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrafficReportScreen(
    onBack: () -> Unit,
    openCameraOnStart: Boolean = false,
    onSubmit: (String, String, String, String, Uri?, () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val screenBackground = colorResource(R.color.background_light)
    val fieldBackground = colorResource(R.color.white)
    val fieldSpacing = dimensionResource(R.dimen.form_report_margin_bottom)
    val labelSpacing = dimensionResource(R.dimen.form_report_label_margin_bottom)
    val reportTypes = stringArrayResource(R.array.form_report_types)
    val allowedPriorities = stringArrayResource(R.array.form_report_priority_values).toSet()
    val titleRequiredError = stringResource(R.string.form_report_title_error_required)
    val typeRequiredError = stringResource(R.string.form_report_type_error_required)
    val descriptionRequiredError = stringResource(R.string.form_report_description_error_required)
    val priorityRequiredError = stringResource(R.string.form_report_priority_error_required)
    val priorityInvalidError = stringResource(R.string.form_report_priority_error_invalid)
    val openingCameraMessage = stringResource(R.string.opening_camera)
    val photoCaptureSuccess = stringResource(R.string.photo_capture_success)
    val photoCaptureCancelled = stringResource(R.string.photo_capture_cancelled)
    val photoCaptureUnavailable = stringResource(R.string.photo_capture_unavailable)
    val photoCaptureError = stringResource(R.string.photo_capture_error)
    val submitButtonText = stringResource(R.string.submit_btn_text)
    val submitLoadingText = stringResource(R.string.submit_btn_loading)

    var title by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var priority by rememberSaveable { mutableStateOf("") }
    var titleError by rememberSaveable { mutableStateOf<String?>(null) }
    var typeError by rememberSaveable { mutableStateOf<String?>(null) }
    var descriptionError by rememberSaveable { mutableStateOf<String?>(null) }
    var priorityError by rememberSaveable { mutableStateOf<String?>(null) }
    var isTypeMenuExpanded by remember { mutableStateOf(false) }

    var isOpeningCamera by rememberSaveable { mutableStateOf(openCameraOnStart) }
    var cameraLaunched by rememberSaveable { mutableStateOf(false) }
    var pendingPhotoUri by rememberSaveable { mutableStateOf<String?>(null) }
    var capturedPhotoUri by rememberSaveable { mutableStateOf<String?>(null) }
    var photoMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isSubmitting by rememberSaveable { mutableStateOf(false) }

    val titleFocusRequester = remember { FocusRequester() }
    val typeFocusRequester = remember { FocusRequester() }
    val descriptionFocusRequester = remember { FocusRequester() }
    val priorityFocusRequester = remember { FocusRequester() }

    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { didTakePhoto ->
        isOpeningCamera = false
        if (didTakePhoto) {
            capturedPhotoUri = pendingPhotoUri
            photoMessage = photoCaptureSuccess
        } else {
            pendingPhotoUri = null
            photoMessage = photoCaptureCancelled
        }
    }

    LaunchedEffect(openCameraOnStart) {
        if (!openCameraOnStart || cameraLaunched) return@LaunchedEffect

        cameraLaunched = true

        if (!canTakePhoto(context)) {
            isOpeningCamera = false
            photoMessage = photoCaptureUnavailable
            return@LaunchedEffect
        }

        val photoUri = runCatching {
            createTrafficReportPhotoUri(context)
        }.getOrElse {
            isOpeningCamera = false
            photoMessage = photoCaptureError
            return@LaunchedEffect
        }

        pendingPhotoUri = photoUri.toString()
        runCatching {
            takePhotoLauncher.launch(photoUri)
        }.onFailure {
            isOpeningCamera = false
            photoMessage = photoCaptureError
        }
    }

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

        if (isOpeningCamera) {
            Text(
                text = openingCameraMessage,
                modifier = Modifier.padding(16.dp)
            )
            return@Column
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.report_title),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            photoMessage?.let { message ->
                Text(
                    text = message,
                    modifier = Modifier.padding(bottom = fieldSpacing)
                )
            }

            Text(
                text = stringResource(R.string.form_report_title),
                modifier = Modifier.padding(bottom = labelSpacing)
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = fieldSpacing)
                    .focusRequester(titleFocusRequester),
                placeholder = { Text(stringResource(R.string.form_report_title_hint)) },
                singleLine = true,
                isError = titleError != null,
                supportingText = titleError?.let { { Text(it) } },
                colors = trafficReportTextFieldColors(fieldBackground)
            )

            Text(
                text = stringResource(R.string.form_report_type),
                modifier = Modifier.padding(bottom = labelSpacing)
            )
            ExposedDropdownMenuBox(
                expanded = isTypeMenuExpanded,
                onExpandedChange = { isTypeMenuExpanded = it },
                modifier = Modifier.padding(bottom = fieldSpacing)
            ) {
                OutlinedTextField(
                    value = type,
                    onValueChange = {},
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .focusRequester(typeFocusRequester),
                    placeholder = { Text(stringResource(R.string.form_report_type)) },
                    readOnly = true,
                    singleLine = true,
                    isError = typeError != null,
                    supportingText = typeError?.let { { Text(it) } },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTypeMenuExpanded) },
                    colors = trafficReportTextFieldColors(fieldBackground)
                )

                ExposedDropdownMenu(
                    expanded = isTypeMenuExpanded,
                    onDismissRequest = { isTypeMenuExpanded = false }
                ) {
                    reportTypes.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                type = option
                                isTypeMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Text(
                text = stringResource(R.string.form_report_description),
                modifier = Modifier.padding(bottom = labelSpacing)
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = fieldSpacing)
                    .focusRequester(descriptionFocusRequester),
                placeholder = { Text(stringResource(R.string.form_report_description_hint)) },
                isError = descriptionError != null,
                supportingText = descriptionError?.let { { Text(it) } },
                colors = trafficReportTextFieldColors(fieldBackground)
            )

            Text(
                text = stringResource(R.string.form_report_priority),
                modifier = Modifier.padding(bottom = labelSpacing)
            )
            OutlinedTextField(
                value = priority,
                onValueChange = { priority = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = fieldSpacing)
                    .focusRequester(priorityFocusRequester),
                placeholder = { Text(stringResource(R.string.form_report_priority_hint)) },
                singleLine = true,
                isError = priorityError != null,
                supportingText = priorityError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                colors = trafficReportTextFieldColors(fieldBackground)
            )

            Button(
                onClick = {
                    titleError = null
                    typeError = null
                    descriptionError = null
                    priorityError = null

                    val trimmedTitle = title.trim()
                    val trimmedType = type.trim()
                    val trimmedDescription = description.trim()
                    val trimmedPriority = priority.trim()

                    when {
                        trimmedTitle.isEmpty() -> {
                            titleError = titleRequiredError
                            titleFocusRequester.requestFocus()
                        }

                        trimmedType.isEmpty() || trimmedType == reportTypes.firstOrNull() -> {
                            typeError = typeRequiredError
                            typeFocusRequester.requestFocus()
                        }

                        trimmedDescription.isEmpty() -> {
                            descriptionError = descriptionRequiredError
                            descriptionFocusRequester.requestFocus()
                        }

                        trimmedPriority.isEmpty() -> {
                            priorityError = priorityRequiredError
                            priorityFocusRequester.requestFocus()
                        }

                        trimmedPriority.lowercase() !in allowedPriorities -> {
                            priorityError = priorityInvalidError
                            priorityFocusRequester.requestFocus()
                        }

                        else -> {
                            val photoUri = capturedPhotoUri?.let(Uri::parse)

                            isSubmitting = true
                            onSubmit(
                                trimmedTitle,
                                trimmedType,
                                trimmedDescription,
                                trimmedPriority,
                                photoUri
                            ) {
                                isSubmitting = false
                            }
                        }
                    }
                },
                modifier = Modifier.padding(top = 4.dp),
                enabled = !isSubmitting
            ) {
                Text(if (isSubmitting) submitLoadingText else submitButtonText)
            }
        }
    }
}

@Composable
private fun trafficReportTextFieldColors(containerColor: Color) = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = containerColor,
    unfocusedContainerColor = containerColor,
    errorContainerColor = containerColor
)

private fun canTakePhoto(context: Context): Boolean {
    val captureImageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    return captureImageIntent.resolveActivity(context.packageManager) != null
}

private fun createTrafficReportPhotoUri(context: Context): Uri {
    val photoDirectory = File(context.filesDir, TRAFFIC_REPORT_PHOTOS_DIRECTORY).apply {
        mkdirs()
    }
    val photoFile = File(photoDirectory, "traffic_report_${System.currentTimeMillis()}.jpg")

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        photoFile
    )
}

private const val TRAFFIC_REPORT_PHOTOS_DIRECTORY = "traffic_report_photos"
