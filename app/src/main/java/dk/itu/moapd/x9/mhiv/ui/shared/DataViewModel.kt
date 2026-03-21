package dk.itu.moapd.x9.mhiv.ui.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dk.itu.moapd.x9.mhiv.domain.model.TrafficReportModel
import dk.itu.moapd.x9.mhiv.ui.repositories.TrafficReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class MainUIState(
    val userId: String? = null,
    val reports: List<TrafficReportModel> = emptyList()
)

class DataViewModel(
    private val trafficReportRepository: TrafficReportRepository = TrafficReportRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUIState(userId = trafficReportRepository.getCurrentUserId()))

    val uiState: StateFlow<MainUIState> = _uiState

    private var listener: ValueEventListener? = null

    init {
        observeDummies()
    }

    private fun observeDummies() {

        // Get the current user ID.
        val userId = trafficReportRepository.getCurrentUserId() ?: return
        _uiState.update { it.copy(userId = userId) }

        // Create a query to retrieve all dummies for the current user.
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
                // Keep previous state; errors will be handled by Firebase SDK logs.
            }
        }

        // Update the listener and add it to the query.
        listener = valueListener
        query.addValueEventListener(valueListener)
    }

    override fun onCleared() {
        super.onCleared()
        val userId = trafficReportRepository.getCurrentUserId()
        val l = listener
        if (userId != null && l != null) {
            trafficReportRepository.trafficReportsQuery().removeEventListener(l)
        }
    }

    fun insertTrafficReport(
        reportTitle: String,
        reportType: String,
        reportDescription: String,
        reportPriority: String,
    ) {
        trafficReportRepository.insertTrafficReport(
            reportTitle = reportTitle,
            reportType = reportType,
            reportDescription = reportDescription,
            reportPriority = reportPriority
        )
    }

    fun updateTrafficReport(report: TrafficReportModel) {
        trafficReportRepository.updateTrafficReport(
            reportId = report.id,
            userId = report.userId,
            reportTitle = report.reportTitle,
            reportType = report.reportType,
            reportDescription = report.reportDescription,
            reportPriority = report.reportPriority,
            createdAt = report.createdAt,
        )
    }

    fun deleteTrafficReport(reportId: String) {
        trafficReportRepository.deleteTrafficReport(reportId)
    }
}
