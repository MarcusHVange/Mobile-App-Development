package dk.itu.moapd.x9.mhiv.domain.model

data class TrafficReport(
    var id: String = "",
    var userId: String = "",
    var reportTitle: String = "",
    var reportType: String = "",
    var reportDescription: String = "",
    var reportPriority: String = "",
    var photoUri: String = "",
    var photoCaption: String = "",
    var latitude: Double? = null,
    var longitude: Double? = null,
    var createdAt: Long = 0L,
    var updatedAt: Long = 0L
)
