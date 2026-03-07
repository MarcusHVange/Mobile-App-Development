package dk.itu.moapd.x9.mhiv.ui.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dk.itu.moapd.x9.mhiv.domain.model.TrafficReportModel

class DataViewModel : ViewModel() {
    private val _cont = MutableLiveData<List<TrafficReportModel>>(emptyList())

    private val _reportCreated = MutableLiveData(false)

    val cont: LiveData<List<TrafficReportModel>>
        get() = _cont

    val reportCreated: LiveData<Boolean>
        get() = _reportCreated

    fun appendCont(item: TrafficReportModel) {
        val currentList = _cont.value ?: emptyList()
        val updatedList = currentList + item
        _cont.value = updatedList
        _reportCreated.value = true
    }

    fun deleteCont(index: Int) {
        val current = _cont.value ?: emptyList()
        _cont.value = current.toMutableList().apply { removeAt(index) }
    }

    fun onReportCreatedToastShown() {
        _reportCreated.value = false
    }
}
