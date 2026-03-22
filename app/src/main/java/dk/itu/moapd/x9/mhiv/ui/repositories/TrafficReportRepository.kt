package dk.itu.moapd.x9.mhiv.ui.repositories

import androidx.annotation.WorkerThread
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.database
import dk.itu.moapd.x9.mhiv.domain.model.TrafficReportModel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

const val DATABASEURL = "https://moapd-2026-fce63-default-rtdb.europe-west1.firebasedatabase.app/"

class TrafficReportRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val root: DatabaseReference = Firebase.database(DATABASEURL).reference
) {
    companion object {
        private const val PATH_TRAFFIC_REPORTS = "trafficReports"
        private const val CHILD_CREATED_AT = "createdAt"
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun trafficReportsQuery(): Query = root
        .child(PATH_TRAFFIC_REPORTS)
        .orderByChild(CHILD_CREATED_AT)

    @WorkerThread
    suspend fun insertTrafficReport(
        reportTitle: String,
        reportType: String,
        reportDescription: String,
        reportPriority: String,
        now: Long = System.currentTimeMillis()
    ): DatabaseError? {
        val userId = getCurrentUserId() ?: return null
        val key = root
            .child(PATH_TRAFFIC_REPORTS)
            .push()
            .key ?: return null

        val report = TrafficReportModel(
            id = key,
            userId = userId,
            reportTitle = reportTitle,
            reportType = reportType,
            reportDescription = reportDescription,
            reportPriority = reportPriority,
            createdAt = now,
            updatedAt = now
        )

        return root
            .child(PATH_TRAFFIC_REPORTS)
            .child(key)
            .awaitSetValue(report)
    }

    @WorkerThread
    suspend fun updateTrafficReport(
        reportId: String,
        userId: String,
        reportTitle: String,
        reportType: String,
        reportDescription: String,
        reportPriority: String,
        createdAt: Long,
        now: Long = System.currentTimeMillis()
    ): DatabaseError? {
        val report = TrafficReportModel(
            id = reportId,
            userId = userId,
            reportTitle = reportTitle,
            reportType = reportType,
            reportDescription = reportDescription,
            reportPriority = reportPriority,
            createdAt = createdAt,
            updatedAt = now
        )

        return root
            .child(PATH_TRAFFIC_REPORTS)
            .child(reportId)
            .awaitSetValue(report)
    }

    @WorkerThread
    suspend fun deleteTrafficReport(reportId: String): DatabaseError? {
        return root
            .child(PATH_TRAFFIC_REPORTS)
            .child(reportId)
            .awaitRemoveValue()
    }

    private suspend fun DatabaseReference.awaitSetValue(value: Any): DatabaseError? =
        suspendCancellableCoroutine { continuation ->
            setValue(value) { error, _ ->
                if (continuation.isActive) {
                    continuation.resume(error)
                }
            }
        }

    private suspend fun DatabaseReference.awaitRemoveValue(): DatabaseError? =
        suspendCancellableCoroutine { continuation ->
            removeValue { error, _ ->
                if (continuation.isActive) {
                    continuation.resume(error)
                }
            }
        }
}
