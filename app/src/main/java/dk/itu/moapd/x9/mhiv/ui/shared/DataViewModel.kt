package dk.itu.moapd.x9.mhiv.ui.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dk.itu.moapd.x9.mhiv.domain.model.TrafficReportModel

class DataViewModel : ViewModel() {
    private val _reports = MutableLiveData<List<TrafficReportModel>>(emptyList())

    private val _reportCreated = MutableLiveData(false)

    val reports: LiveData<List<TrafficReportModel>>
        get() = _reports

    val reportCreated: LiveData<Boolean>
        get() = _reportCreated

    fun addReport(report: TrafficReportModel) {
        val currentList = _reports.value ?: emptyList()
        val updatedList = currentList + report
        _reports.value = updatedList
        _reportCreated.value = true
    }

    fun deleteReport(index: Int) {
        val current = _reports.value ?: emptyList()
        _reports.value = current.toMutableList().apply { removeAt(index) }
    }

    fun onReportCreatedToastShown() {
        _reportCreated.value = false
    }
}
