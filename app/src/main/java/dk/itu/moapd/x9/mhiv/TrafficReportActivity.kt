package dk.itu.moapd.x9.mhiv

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
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
            // Set dropdown items on Report type field
            ArrayAdapter.createFromResource(
                this@TrafficReportActivity,
                R.array.form_report_types,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                formReportTypeSpinner.adapter = adapter
            }

            // Set form submit listener
            submitBtn.setOnClickListener {
                if (!isFormValid()) return@setOnClickListener

                val reportTitle = formReportTitle.text.toString().trim()
                val reportType = formReportTypeSpinner.selectedItem.toString().trim()
                val reportDescription = formReportDescription.text.toString().trim()
                val reportPriority = formReportPriority.text.toString().trim()

                val formData =
                    "Report Title: $reportTitle\n Report Type: $reportType\n Report Description: $reportDescription\n Report Priority: $reportPriority"
                Log.i("Report Info", formData)
            }
        }
    }

    private fun isFormValid(): Boolean {
        with(binding) {
            // Clear old errors
            formReportTitle.error = null
            formReportDescription.error = null
            formReportPriority.error = null
            formReportTypeLabel.error = null

            val title = formReportTitle.text.toString().trim()
            val typePos = formReportTypeSpinner.selectedItemPosition
            val description = formReportDescription.text.toString().trim()
            val priority = formReportPriority.text.toString().trim()

            var valid = true

            if (title.isEmpty()) {
                formReportTitle.error = getString(R.string.form_report_title_error_required)
                valid = false
            }

            if (typePos == 0) {
                formReportTypeLabel.error = getString(R.string.form_report_type_error_required)
                valid = false
            }

            if (description.isEmpty()) {
                formReportDescription.error = getString(R.string.form_report_description_error_required)
                valid = false
            }

            if (priority.isEmpty()) {
                formReportPriority.error = getString(R.string.form_report_priority_error_required)
                valid = false
            } else {
                val allowed = resources.getStringArray(R.array.form_report_priority_values).toSet()
                if (priority.lowercase() !in allowed) {
                    formReportPriority.error = getString(R.string.form_report_priority_error_invalid)
                    valid = false
                }
            }

            if (!valid) {
                when {
                    formReportTitle.error != null -> formReportTitle.requestFocus()
                    formReportTypeLabel.error != null -> formReportTypeSpinner.requestFocus()
                    formReportDescription.error != null -> formReportDescription.requestFocus()
                    formReportPriority.error != null -> formReportPriority.requestFocus()
                }
            }

            return valid
        }
    }
}