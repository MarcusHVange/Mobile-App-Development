package dk.itu.moapd.x9.mhiv.ui.state

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dk.itu.moapd.x9.mhiv.core.preferences.LocationTrackingPreferences

/**
 * A composable function that observes the tracking enabled state of the application.
 *
 * @param sharedPreferences The shared preferences of the application.
 * @param context The context of the application.
 *
 * @return A mutable state of the tracking enabled state.
 */
@Composable
fun rememberTrackingEnabledState(
    sharedPreferences: SharedPreferences,
    context: Context,
): MutableState<Boolean> {
    val state = remember {
        mutableStateOf(LocationTrackingPreferences.isTrackingEnabled(context))
    }

    DisposableEffect(sharedPreferences) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == LocationTrackingPreferences.KEY_TRACKING_ENABLED) {
                state.value = LocationTrackingPreferences.isTrackingEnabled(context)
            }
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        onDispose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    return state
}