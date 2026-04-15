package dk.itu.moapd.x9.mhiv.domain.model

data class TrafficReportModel(
    var id: String = "",
    var userId: String = "",
    var reportTitle: String = "",
    var reportType: String = "",
    var reportDescription: String = "",
    var reportPriority: String = "",
    var photoUri: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var createdAt: Long = 0L,
    var updatedAt: Long = 0L
)