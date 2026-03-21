package dk.itu.moapd.x9.mhiv.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import dk.itu.moapd.x9.mhiv.R
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.mhiv.databinding.FragmentTrafficReportBinding
import dk.itu.moapd.x9.mhiv.domain.model.TrafficReportModel
import dk.itu.moapd.x9.mhiv.ui.shared.DataViewModel
import dk.itu.moapd.x9.mhiv.ui.utils.viewBinding

class TrafficReportFragment : Fragment(R.layout.fragment_traffic_report) {
    private val binding by viewBinding(FragmentTrafficReportBinding::bind)

    private val viewModel: DataViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restoreState(savedInstanceState)
        setupUI()
    }

    override fun onStart() {
        super.onStart()
        val auth = FirebaseAuth.getInstance()

        if(auth.currentUser == null) {
            startLoginActivity()
        }
    }

    companion object {
        private const val KEY_REPORT_TITLE = "key_report_title"
        private const val KEY_REPORT_TYPE = "key_report_type"
        private const val KEY_REPORT_DESCRIPTION = "key_report_description"
        private const val KEY_REPORT_PRIORITY = "key_report_priority"
        private const val KEY_TITLE_ERROR = "key_title_error"
        private const val KEY_TYPE_ERROR = "key_type_error"
        private const val KEY_DESCRIPTION_ERROR = "key_description_error"
        private const val KEY_PRIORITY_ERROR = "key_priority_error"
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

                viewModel.insertTrafficReport(
                    reportTitle = reportTitle,
                    reportType = reportType,
                    reportDescription = reportDescription,
                    reportPriority = reportPriority
                )

                findNavController()
                    .previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("report_created", true)

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

    private fun startLoginActivity() {
        Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let(::startActivity)
    }
}