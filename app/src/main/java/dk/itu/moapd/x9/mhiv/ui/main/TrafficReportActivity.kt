package dk.itu.moapd.x9.mhiv.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import dk.itu.moapd.x9.mhiv.R
import dk.itu.moapd.x9.mhiv.databinding.ActivityTrafficReportBinding

class TrafficReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrafficReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrafficReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        with(binding) {
            val reportTypes = resources.getStringArray(R.array.form_report_types)
            val reportTypeAdapter = ArrayAdapter(
                this@TrafficReportActivity,
                android.R.layout.simple_list_item_1,
                reportTypes
            )
            formReportTypeDropdown.setAdapter(reportTypeAdapter)

            // Set form submit listener
            submitBtn.setOnClickListener {
                if (!isFormValid()) return@setOnClickListener

                val reportTitle = formReportTitle.text?.toString()?.trim().orEmpty()
                val reportType = formReportTypeDropdown.text?.toString()?.trim().orEmpty()
                val reportDescription = formReportDescription.text?.toString()?.trim().orEmpty()
                val reportPriority = formReportPriority.text?.toString()?.trim().orEmpty()

                val formData =
                    "Report Title: $reportTitle\nReport Type: $reportType\nReport Description: $reportDescription\nReport Priority: $reportPriority"

                val intent = Intent(this@TrafficReportActivity, MainActivity::class.java).apply {
                    putExtra("reportData", formData)
                }
                startActivity(intent)
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
}