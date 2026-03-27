package dk.itu.moapd.x9.mhiv.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import dk.itu.moapd.x9.mhiv.ui.theme.X9Theme

class MapsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            X9Theme {
                Text(
                    text = "Maps"
                )
            }
        }
    }
}