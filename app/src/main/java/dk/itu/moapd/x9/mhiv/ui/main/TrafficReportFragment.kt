package dk.itu.moapd.x9.mhiv.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import dk.itu.moapd.x9.mhiv.R
import androidx.fragment.app.activityViewModels
import dk.itu.moapd.x9.mhiv.databinding.FragmentTrafficReportBinding
import dk.itu.moapd.x9.mhiv.domain.model.TrafficReportModel
import dk.itu.moapd.x9.mhiv.ui.shared.DataViewModel
import dk.itu.moapd.x9.mhiv.ui.utils.viewBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TrafficReportFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrafficReportFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val binding by viewBinding(FragmentTrafficReportBinding::bind)
    private val viewModel: DataViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_traffic_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restoreState(savedInstanceState)
        setupUI()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TrafficReportFragment.
         */
        // TODO: Rename and change types and number of parameters
        private const val KEY_REPORT_TITLE = "key_report_title"
        private const val KEY_REPORT_TYPE = "key_report_type"
        private const val KEY_REPORT_DESCRIPTION = "key_report_description"
        private const val KEY_REPORT_PRIORITY = "key_report_priority"
        private const val KEY_TITLE_ERROR = "key_title_error"
        private const val KEY_TYPE_ERROR = "key_type_error"
        private const val KEY_DESCRIPTION_ERROR = "key_description_error"
        private const val KEY_PRIORITY_ERROR = "key_priority_error"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TrafficReportFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun setupUI() {
        with(binding) {
            val reportTypes = resources.getStringArray(R.array.form_report_types)
            val reportTypeAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                reportTypes
            )
            formReportTypeDropdown.setAdapter(reportTypeAdapter)

            // Set back button listener
            backButton.setOnClickListener {
                findNavController().navigateUp()
            }

            // Set form submit listener
            submitBtn.setOnClickListener {
                if (!isFormValid()) return@setOnClickListener

                val reportTitle = formReportTitle.text?.toString()?.trim().orEmpty()
                val reportType = formReportTypeDropdown.text?.toString()?.trim().orEmpty()
                val reportDescription = formReportDescription.text?.toString()?.trim().orEmpty()
                val reportPriority = formReportPriority.text?.toString()?.trim().orEmpty()

                val newReport = TrafficReportModel(
                    reportTitle = reportTitle,
                    reportType = reportType,
                    reportDescription = reportDescription,
                    reportPriority = reportPriority
                )

                viewModel.appendCont(newReport)

                findNavController().navigateUp()
            }
        }
    }

    private fun isFormValid(): Boolean {
        with(binding) {
            // Clear old errors
            formReportTitleLayout.error = null
            formReportTypeLayout.error = null
            formReportDescriptionLayout.error = null
            formReportPriorityLayout.error = null

            val title = formReportTitle.text?.toString()?.trim().orEmpty()
            val type = formReportTypeDropdown.text?.toString()?.trim().orEmpty()
            val description = formReportDescription.text?.toString()?.trim().orEmpty()
            val priority = formReportPriority.text?.toString()?.trim().orEmpty()

            var valid = true

            if (title.isEmpty()) {
                formReportTitleLayout.error = getString(R.string.form_report_title_error_required)
                valid = false
            }

            if (type.isEmpty() || type == resources.getStringArray(R.array.form_report_types).firstOrNull()) {
                formReportTypeLayout.error = getString(R.string.form_report_type_error_required)
                valid = false
            }

            if (description.isEmpty()) {
                formReportDescriptionLayout.error = getString(R.string.form_report_description_error_required)
                valid = false
            }

            if (priority.isEmpty()) {
                formReportPriorityLayout.error = getString(R.string.form_report_priority_error_required)
                valid = false
            } else {
                val allowed = resources.getStringArray(R.array.form_report_priority_values).toSet()
                if (priority.lowercase() !in allowed) {
                    formReportPriorityLayout.error = getString(R.string.form_report_priority_error_invalid)
                    valid = false
                }
            }

            if (!valid) {
                when {
                    formReportTitleLayout.error != null -> formReportTitle.requestFocus()
                    formReportTypeLayout.error != null -> formReportTypeDropdown.requestFocus()
                    formReportDescriptionLayout.error != null -> formReportDescription.requestFocus()
                    formReportPriorityLayout.error != null -> formReportPriority.requestFocus()
                }
            }

            return valid
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        with(binding) {
            outState.putString(KEY_REPORT_TITLE, formReportTitle.text?.toString())
            outState.putString(KEY_REPORT_TYPE, formReportTypeDropdown.text?.toString())
            outState.putString(KEY_REPORT_DESCRIPTION, formReportDescription.text?.toString())
            outState.putString(KEY_REPORT_PRIORITY, formReportPriority.text?.toString())

            outState.putString(KEY_TITLE_ERROR, formReportTitleLayout.error?.toString())
            outState.putString(KEY_TYPE_ERROR, formReportTypeLayout.error?.toString())
            outState.putString(KEY_DESCRIPTION_ERROR, formReportDescriptionLayout.error?.toString())
            outState.putString(KEY_PRIORITY_ERROR, formReportPriorityLayout.error?.toString())
        }
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) return
        with(binding) {
            formReportTitle.setText(savedInstanceState.getString(KEY_REPORT_TITLE, ""))
            formReportTypeDropdown.setText(savedInstanceState.getString(KEY_REPORT_TYPE, ""), false)
            formReportDescription.setText(savedInstanceState.getString(KEY_REPORT_DESCRIPTION, ""))
            formReportPriority.setText(savedInstanceState.getString(KEY_REPORT_PRIORITY, ""))

            formReportTitleLayout.error = savedInstanceState.getString(KEY_TITLE_ERROR)
            formReportTypeLayout.error = savedInstanceState.getString(KEY_TYPE_ERROR)
            formReportDescriptionLayout.error = savedInstanceState.getString(KEY_DESCRIPTION_ERROR)
            formReportPriorityLayout.error = savedInstanceState.getString(KEY_PRIORITY_ERROR)
        }
    }
}