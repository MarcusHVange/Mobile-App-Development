package dk.itu.moapd.x9.mhiv.ui.repositories

import android.net.Uri
import androidx.annotation.WorkerThread
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import dk.itu.moapd.x9.mhiv.BuildConfig
import dk.itu.moapd.x9.mhiv.domain.model.TrafficReport
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume


class TrafficReportRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val root: DatabaseReference = Firebase.database(BuildConfig.DATABASEURL).reference
) {
    companion object {
        private const val PATH_TRAFFIC_REPORTS = "trafficReports"
        private const val PATH_TRAFFIC_REPORT_PHOTOS = "traffic_report_photos"
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
        latitude: Double?,
        longitude: Double?,
        photoUri: Uri?,
        photoCaption: String,
        now: Long = System.currentTimeMillis()
    ): DatabaseError? {
        val userId = getCurrentUserId() ?: return null
        val key = root
            .child(PATH_TRAFFIC_REPORTS)
            .push()
            .key ?: return null

        val photoPath = if (photoUri != null) uploadTrafficReportPhoto(photoUri, key) else ""

        val report = TrafficReport(
            id = key,
            userId = userId,
            reportTitle = reportTitle,
            reportType = reportType,
            reportDescription = reportDescription,
            reportPriority = reportPriority,
            photoUri = photoPath,
            photoCaption = photoCaption,
            latitude = latitude,
            longitude = longitude,
            createdAt = now,
            updatedAt = now
        )

        return root.child(PATH_TRAFFIC_REPORTS).child(key).awaitSetValue(report)
    }

    @WorkerThread
    suspend fun deleteTrafficReport(reportId: String): DatabaseError? {
        val error = root
            .child(PATH_TRAFFIC_REPORTS)
            .child(reportId)
            .awaitRemoveValue()

        if (error == null) {
            deleteTrafficReportPhoto(reportId)
        }

        return error
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

    private suspend fun uploadTrafficReportPhoto(photoUri: Uri, reportId: String): String {
        val photoPath = photoUri.let { uri ->
            val path = "$PATH_TRAFFIC_REPORT_PHOTOS/$reportId.jpg"
            Firebase.storage(BuildConfig.STORAGEBUCKETURL).reference
                .child(path)
                .putFile(uri)
                .await()
            path
        }

        return photoPath
    }

    suspend fun getTrafficReportPhotoUrl(path: String): Uri? =
        suspendCancellableCoroutine { continuation ->
            Firebase.storage(BuildConfig.STORAGEBUCKETURL).reference
                .child(path)
                .downloadUrl
                .addOnSuccessListener { downloadUrl ->
                    if (continuation.isActive) {
                        continuation.resume(downloadUrl)
                    }
                }
                .addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }
        }

    private fun deleteTrafficReportPhoto(reportId: String) {
        Firebase.storage(BuildConfig.STORAGEBUCKETURL).reference
            .child("$PATH_TRAFFIC_REPORT_PHOTOS/$reportId.jpg")
            .delete()
    }
}
