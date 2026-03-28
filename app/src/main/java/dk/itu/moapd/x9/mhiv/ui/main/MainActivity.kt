package dk.itu.moapd.x9.mhiv.ui.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dk.itu.moapd.x9.mhiv.R
import dk.itu.moapd.x9.mhiv.core.preferences.LocationTrackingPreferences
import dk.itu.moapd.x9.mhiv.service.LocationService
import dk.itu.moapd.x9.mhiv.ui.navigation.NavigationStack
import dk.itu.moapd.x9.mhiv.ui.shared.DataViewModel
import dk.itu.moapd.x9.mhiv.ui.shared.SessionViewModel
import dk.itu.moapd.x9.mhiv.ui.theme.X9Theme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val dataViewModel: DataViewModel by lazy {
        ViewModelProvider(this)[DataViewModel::class.java]
    }

    private val sessionViewModel: SessionViewModel by lazy {
        ViewModelProvider(this)[SessionViewModel::class.java]
    }

    /**
     * The SharedPreferences instance that can be used to save and retrieve data.
     */
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
    }

    /**
     * Provides location updates for while-in-use feature.
     */
    private var locationService: LocationService? = null

    /**
     * A flag to indicate whether a bound to the service.
     */
    private var locationServiceBound: Boolean = false

    /**
     * A job for collecting location updates.
     */
    private var collectJob: Job? = null

    /**
     * Holds the latest onLocation callback provided by the composable. This allows starting
     * collection after the activity is recreated (e.g. on rotation) once the service bind
     * completes.
     */
    private var onLocationCallback: ((Location) -> Unit)? = null

    /**
     * When the user toggles tracking, we may need to start the service even if it's not yet bound.
     * This flag indicates a pending request to subscribe to location updates.
     */
    private var pendingStartTracking: Boolean = false

    /**
     * Defines callbacks for service binding, passed to `bindService()`.
     */
    private val serviceConnection = createLocationServiceConnection(
        onConnected = { service ->
            locationService = service
            locationServiceBound = true

            if (pendingStartTracking) {
                service.subscribeToLocationUpdates()
                pendingStartTracking = false
            }

            startCollectingIfReady()
        },
        onDisconnected = {
            locationService = null
            locationServiceBound = false
            collectJob?.cancel()
            collectJob = null
        },
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            X9Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationStack(
                        dataViewModel = dataViewModel,
                        sessionViewModel = sessionViewModel,
                        onStartLoginActivity = ::startLoginActivity,
                        sharedPreferences = sharedPreferences,
                        onStartTracking = {
                            pendingStartTracking = true
                            startLocationService()
                            if (locationServiceBound) {
                                locationService?.subscribeToLocationUpdates()
                                pendingStartTracking = false
                            }
                        },
                        onStopTracking = {
                            pendingStartTracking = false
                            locationService?.unsubscribeToLocationUpdates()
                            stopService(Intent(this, LocationService::class.java))
                        },
                        onCollectLocations = { onLocation ->
                            onLocationCallback = onLocation
                            collectJob?.cancel()
                            startCollectingIfReady()
                        }
                    )
                }
            }
        }

        val alreadyEnabledOnCreate = LocationTrackingPreferences.isTrackingEnabled(this)
        if (alreadyEnabledOnCreate) {
            pendingStartTracking = true
            startLocationService()
        }
    }

    private fun startLoginActivity(clearTask: Boolean = false) {
        Intent(this, LoginActivity::class.java).apply {
            if (clearTask) {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }.let(::startActivity)
    }

    /**
     * Called after `onCreate()` or after `onRestart()` when the activity had been stopped, but is
     * now again being displayed to the user. It will usually be followed by `onResume()`. This is a
     * good place to begin drawing visual elements, running animations, etc.
     *
     * You can call `finish()` from within this function, in which case `onStop()` will be
     * immediately called after `onStart()` without the lifecycle transitions in-between
     * (`onResume()`, `onPause()`, etc) executing.
     *
     * Derived classes must call through to the super class's implementation of this method. If they
     * do not, an exception will be thrown.
     */
    override fun onStart() {
        super.onStart()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        bindService(
            Intent(
                this,
                LocationService::class.java
            ),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )

        val alreadyEnabled = LocationTrackingPreferences.isTrackingEnabled(this)
        if (alreadyEnabled) {
            pendingStartTracking = true
            startLocationService()
            if (locationServiceBound) {
                locationService?.subscribeToLocationUpdates()
                pendingStartTracking = false
            }
        }
    }

    /**
     * Called when you are no longer visible to the user. You will next receive either
     * `onRestart()`, `onDestroy()`, or nothing, depending on later user activity. This is a good
     * place to stop refreshing UI, running animations and other visual things.
     *
     * Derived classes must call through to the super class's implementation of this method. If they
     * do not, an exception will be thrown.
     */
    override fun onStop() {
        if (locationServiceBound) {
            unbindService(serviceConnection)
            locationServiceBound = false
        }

        collectJob?.cancel()
        collectJob = null

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
    }

    /**
     * Called when a shared preference is changed, added, or removed. This may be called even if a
     * preference is set to its existing value. This callback will be run on your main thread.
     *
     * @param sharedPreferences The `SharedPreferences` that received the change.
     * @param key The key of the preference that was changed, added, or removed. Apps targeting
     *      android.os.Build.VERSION_CODES#R on devices running OS versions
     *      android.os.Build.VERSION_CODES#R Android R} or later, will receive a `null` value when
     *      preferences are cleared.
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        if (key == LocationTrackingPreferences.KEY_TRACKING_ENABLED) {
            val enabled = LocationTrackingPreferences.isTrackingEnabled(this)
            if (!enabled) {
                collectJob?.cancel()
                collectJob = null
            } else {
                startCollectingIfReady()
            }
        }
    }

    /**
     * Starts the LocationService as a foreground service.
     * Uses startForegroundService() on Android O+ and startService() on older versions.
     */
    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    /**
     * Starts the collector if we have both the service bound and the composable's onLocation
     * callback available and tracking is enabled.
     */
    private fun startCollectingIfReady() {
        val isReady = onLocationCallback != null &&
                locationService != null &&
                LocationTrackingPreferences.isTrackingEnabled(this)

        if (!isReady) return

        collectJob?.cancel()
        collectJob = lifecycleScope.launch {
            locationService?.locationUpdates?.collect { location ->
                onLocationCallback?.invoke(location)
            }
        }
    }
}
