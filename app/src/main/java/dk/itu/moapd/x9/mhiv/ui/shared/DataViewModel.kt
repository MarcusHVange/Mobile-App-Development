package dk.itu.moapd.x9.mhiv.ui.shared

import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import dk.itu.moapd.x9.mhiv.R
import dk.itu.moapd.x9.mhiv.domain.model.TrafficReportModel
import dk.itu.moapd.x9.mhiv.ui.repositories.TrafficReportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MainUIState(
    val userId: String? = null,
    val reports: List<TrafficReportModel> = emptyList()
)

class DataViewModel(
    private val trafficReportRepository: TrafficReportRepository = TrafficReportRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUIState(userId = trafficReportRepository.getCurrentUserId()))

    val uiState: StateFlow<MainUIState> = _uiState

    private val _databaseErrorMessage = MutableLiveData<Int?>()
    val databaseErrorMessage: LiveData<Int?> = _databaseErrorMessage

    private var reportsQuery: Query? = null
    private var listener: ValueEventListener? = null

    init {
        observeReports()
    }

    private fun observeReports() {
        _uiState.update { it.copy(userId = trafficReportRepository.getCurrentUserId()) }

        val query = trafficReportRepository.trafficReportsQuery()

        // Create a listener to receive events from the database.
        val valueListener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { child ->
                    val report = child.getValue(TrafficReportModel::class.java) ?: return@mapNotNull null
                    val id = child.key ?: return@mapNotNull null

                    report.copy(id = id)
                }.sortedBy { it.createdAt }

                _uiState.update { it.copy(reports = items) }
            }

            override fun onCancelled(error: DatabaseError) {
                _databaseErrorMessage.value = databaseErrorMessageRes(error)
            }
        }

        // Update the listener and add it to the query.
        reportsQuery = query
        listener = valueListener
        query.addValueEventListener(valueListener)
    }

    override fun onCleared() {
        super.onCleared()
        val query = reportsQuery
        val l = listener
        if (query != null && l != null) {
            query.removeEventListener(l)
        }
    }

    fun insertTrafficReport(
        context: Context,
        reportTitle: String,
        reportType: String,
        reportDescription: String,
        reportPriority: String,
        latitude: Double,
        longitude: Double,
        photoUri: Uri?,
        onComplete: () -> Unit = {},
    ) {
        viewModelScope.launch {
            val photoCaption = photoUri
                ?.let { createPhotoCaption(context.applicationContext, it) }
                .orEmpty()

            val error = withContext(Dispatchers.IO) {
                trafficReportRepository.insertTrafficReport(
                    reportTitle = reportTitle,
                    reportType = reportType,
                    reportDescription = reportDescription,
                    reportPriority = reportPriority,
                    latitude = latitude,
                    longitude = longitude,
                    photoUri = photoUri,
                    photoCaption = photoCaption
                )
            }

            if (error != null) {
                _databaseErrorMessage.value = databaseErrorMessageRes(error)
            }

            onComplete()
        }
    }

    fun updateTrafficReport(report: TrafficReportModel) {
        viewModelScope.launch(Dispatchers.IO) {
            trafficReportRepository.updateTrafficReport(
                reportId = report.id,
                userId = report.userId,
                reportTitle = report.reportTitle,
                reportType = report.reportType,
                reportDescription = report.reportDescription,
                reportPriority = report.reportPriority,
                photoUri = report.photoUri,
                photoCaption = report.photoCaption,
                latitude = report.latitude,
                longitude = report.longitude,
                createdAt = report.createdAt,
            )
        }
    }

    fun deleteTrafficReport(reportId: String) {
        viewModelScope.launch {
            val error = withContext(Dispatchers.IO) {
                trafficReportRepository.deleteTrafficReport(reportId)
            }

            if (error != null) {
                _databaseErrorMessage.value = databaseErrorMessageRes(error)
            }
        }
    }

    fun clearDatabaseErrorMessage() {
        _databaseErrorMessage.value = null
    }

    suspend fun getTrafficReportPhotoUrl(photoPath: String): Uri? =
        withContext(Dispatchers.IO) {
            trafficReportRepository.getTrafficReportPhotoUrl(photoPath)
        }

    private suspend fun createPhotoCaption(context: Context, photoUri: Uri): String =
        withContext(Dispatchers.IO) {
            runCatching {
                val image = InputImage.fromFilePath(context, photoUri)
                val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
                val labels = Tasks.await(labeler.process(image))

                labels.sortedByDescending { it.confidence }
                    .take(PHOTO_CAPTION_LABEL_COUNT)
                    .joinToString(", ") { it.text }
            }.getOrDefault("")
        }

    @StringRes
    private fun databaseErrorMessageRes(error: DatabaseError?): Int {
        return when (error?.code) {
            DatabaseError.PERMISSION_DENIED -> R.string.database_error_permission_denied
            DatabaseError.DISCONNECTED,
            DatabaseError.NETWORK_ERROR,
            DatabaseError.UNAVAILABLE -> R.string.database_error_network
            else -> R.string.database_error_generic
        }
    }
}

private const val PHOTO_CAPTION_LABEL_COUNT = 3
