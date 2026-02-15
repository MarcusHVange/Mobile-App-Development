package dk.itu.moapd.x9.mhiv

import android.content.Intent
import android.os.Bundle
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
            goToReportBtn.setOnClickListener {
                val intent = Intent(this@MainActivity, TrafficReportActivity::class.java)
                startActivity(intent)
            }
        }
    }
}

