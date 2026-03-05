package dk.itu.moapd.x9.mhiv.ui.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dk.itu.moapd.x9.mhiv.domain.model.DummyModel

class DataViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    companion object {
        private const val CONT_KEY = "CONT_KEY"
        private const val STATUS_KEY = "STATUS_KEY"
    }

    private val _cont: MutableLiveData<List<DummyModel>> by lazy {
        savedStateHandle.getLiveData(CONT_KEY, emptyList<DummyModel>())
    }

    var status: Boolean
        get() = savedStateHandle.get<Boolean>(STATUS_KEY) ?: false
        set(value) = savedStateHandle.set(STATUS_KEY, value)

    val cont: LiveData<List<DummyModel>>
        get() = _cont

    fun resetCont() {
        _cont.value = emptyList<DummyModel>()
    }

    fun appendCont(item: DummyModel) {
        val currentList = _cont.value ?: emptyList()
        val updatedList = currentList + item
        _cont.value = updatedList
    }
}