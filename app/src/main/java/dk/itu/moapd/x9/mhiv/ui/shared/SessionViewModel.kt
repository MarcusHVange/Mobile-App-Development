package dk.itu.moapd.x9.mhiv.ui.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SessionViewModel: ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _isLoggedIn = MutableLiveData(auth.currentUser != null)

    val isLoggedIn: LiveData<Boolean>
        get() = _isLoggedIn

    val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _isLoggedIn.value = firebaseAuth.currentUser != null
    }

    init {
        auth.addAuthStateListener(authListener)
    }

    fun signOut() {
        auth.signOut()
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        super.onCleared()
    }
}