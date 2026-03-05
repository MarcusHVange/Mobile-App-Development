package dk.itu.moapd.x9.mhiv.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import dk.itu.moapd.x9.mhiv.R
import dk.itu.moapd.x9.mhiv.ui.composables.MainScreen
import dk.itu.moapd.x9.mhiv.ui.shared.DataViewModel

class ComposeMainFragment : Fragment() {

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
                val reports by viewModel.cont.observeAsState(emptyList())

                MainScreen(
                    reports = reports,
                    onAddReportNavigate = {
                        findNavController().navigate(R.id.action_compose_main_to_traffic_report)
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.cont.observe(viewLifecycleOwner) { reportData ->
            if (reportData.isNotEmpty()) {
                Toast.makeText(requireContext(), "Report created", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
