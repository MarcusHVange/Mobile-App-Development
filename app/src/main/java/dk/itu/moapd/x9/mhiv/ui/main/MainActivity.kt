package dk.itu.moapd.x9.mhiv.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import dk.itu.moapd.x9.mhiv.R
import dk.itu.moapd.x9.mhiv.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
                .findFragmentById(
                    R.id.fragment_container_view
                ) as NavHostFragment
        val navController = navHostFragment.navController

//        val reportData = intent.getStringExtra("reportData")
//
//        if(reportData != null) {
//            Log.i("Report info", reportData)
//        }
//
//        setupUI()
    }

//    private fun setupUI() {
//        with(binding) {
//            goToReportBtn.setOnClickListener {
//                val intent = Intent(this@MainActivity, TrafficReportActivity::class.java)
//                startActivity(intent)
//            }
//        }
//    }
}