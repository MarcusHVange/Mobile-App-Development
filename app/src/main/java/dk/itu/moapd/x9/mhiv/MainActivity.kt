package dk.itu.moapd.x9.mhiv

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import dk.itu.moapd.x9.mhiv.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        with(binding) {
            // Set dropdown items on Report type field
            ArrayAdapter.createFromResource(
                this@MainActivity,
                R.array.form_report_types,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                formReportTypeSpinner.adapter = adapter
            }

            // Set form submit listener
            submitBtn.setOnClickListener {
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
}
