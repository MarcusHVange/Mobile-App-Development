package dk.itu.moapd.x9.mhiv.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.mhiv.ui.composables.TrafficReportScreen
import dk.itu.moapd.x9.mhiv.ui.shared.DataViewModel
import dk.itu.moapd.x9.mhiv.ui.theme.X9Theme

class TrafficReportFragment : Fragment() {
    private val viewModel: DataViewModel by activityViewModels()

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
                X9Theme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        TrafficReportScreen(
                            onBack = { findNavController().navigateUp() },
                            onSubmit = { title, type, description, priority ->
                                viewModel.insertTrafficReport(
                                    reportTitle = title,
                                    reportType = type,
                                    reportDescription = description,
                                    reportPriority = priority
                                )

                                findNavController()
                                    .previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("report_created", true)

                                findNavController().navigateUp()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val auth = FirebaseAuth.getInstance()

        if(auth.currentUser == null) {
            startLoginActivity()
        }
    }

    private fun startLoginActivity() {
        Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let(::startActivity)
    }
}
