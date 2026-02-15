package dk.itu.moapd.x9.mhiv.app

import android.app.Application
import com.google.android.material.color.DynamicColors

class MaterialDesignApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // Apply dynamic colors to activities if available.
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}