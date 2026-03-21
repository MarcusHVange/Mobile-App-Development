package dk.itu.moapd.x9.mhiv.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.mhiv.R
import dk.itu.moapd.x9.mhiv.ui.composables.MainScreen
import dk.itu.moapd.x9.mhiv.ui.shared.DataViewModel
import dk.itu.moapd.x9.mhiv.ui.shared.SessionViewModel
import dk.itu.moapd.x9.mhiv.ui.theme.X9Theme

class MainFragment : Fragment() {

    private val dataViewModel: DataViewModel by activityViewModels()
    private val sessionViewModel: SessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {
                val uiState by dataViewModel.uiState.collectAsStateWithLifecycle()
                val reports = uiState.reports
                val isLoggedIn by sessionViewModel.isLoggedIn.observeAsState(false)

                X9Theme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainScreen(
                            reports = reports,
                            isLoggedIn=isLoggedIn,
                            onAddReportNavigate = {
                                findNavController().navigate(R.id.action_compose_main_to_traffic_report)
                            },
                            onDelete = { reportId ->
                                dataViewModel.deleteTrafficReport(reportId)
                            },
                            authAction={ isLoggedIn ->
                                authAction(isLoggedIn)
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle

        savedStateHandle
            ?.getLiveData<Boolean>("report_created")
            ?.observe(viewLifecycleOwner) { created ->
                if (created) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.report_created_message),
                        Toast.LENGTH_SHORT
                    ).show()

                    savedStateHandle.remove<Boolean>("report_created")
                }
            }
    }

    fun authAction(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            sessionViewModel.signOut()
        } else {
            Intent(requireContext(), LoginActivity::class.java).let(::startActivity)
        }
    }
}
