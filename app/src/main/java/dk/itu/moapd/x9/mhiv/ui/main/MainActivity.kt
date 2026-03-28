package dk.itu.moapd.x9.mhiv.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import dk.itu.moapd.x9.mhiv.ui.navigation.NavigationStack
import dk.itu.moapd.x9.mhiv.ui.shared.DataViewModel
import dk.itu.moapd.x9.mhiv.ui.shared.SessionViewModel
import dk.itu.moapd.x9.mhiv.ui.theme.X9Theme

class MainActivity : AppCompatActivity() {

    private val dataViewModel: DataViewModel by lazy {
        ViewModelProvider(this)[DataViewModel::class.java]
    }

    private val sessionViewModel: SessionViewModel by lazy {
        ViewModelProvider(this)[SessionViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            X9Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationStack(
                        dataViewModel = dataViewModel,
                        sessionViewModel = sessionViewModel,
                        onStartLoginActivity = ::startLoginActivity
                    )
                }
            }
        }
    }

    private fun startLoginActivity(clearTask: Boolean = false) {
        Intent(this, LoginActivity::class.java).apply {
            if (clearTask) {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }.let(::startActivity)
    }
}
