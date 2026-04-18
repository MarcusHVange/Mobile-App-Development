package dk.itu.moapd.x9.mhiv.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dk.itu.moapd.x9.mhiv.R
import dk.itu.moapd.x9.mhiv.core.preferences.LocationTrackingPreferences
import dk.itu.moapd.x9.mhiv.ui.main.MainActivity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * A service class with several methods to manage the location service of Geolocation application.
 */
class LocationService : Service() {
    /**
     * A set of private constants used in this class.
     */
    companion object {
        /**
         * The notification channel ID for the foreground service.
         */
        private const val NOTIFICATION_CHANNEL_ID = "location_tracking_channel"

        /**
         * The notification ID for the foreground service.
         */
        private const val NOTIFICATION_ID = 1

        /**
         * The interval for active location updates. Updates may be less frequent than this interval
         * if the app is not in the foreground.
         */
        private const val LOCATION_UPDATE_INTERVAL_MS = 60L

        /**
         * The fastest rate for active location updates. Updates will never be more frequent
         * than this value.
         */
        private const val MIN_UPDATE_INTERVAL_MS = 30L

        /**
         * The maximum time when batched location updates are delivered. Updates may be
         * delivered sooner than this interval.
         */
        private const val MAX_UPDATE_DELAY_MS = 2L
    }

    /**
     * Class used for the client Binder. Since this service runs in the same process as its clients,
     * we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        val service: LocationService
            get() = this@LocationService
    }

    /**
     * The binder instance for this service.
     */
    private val localBinder = LocalBinder()

    /**
     * The primary instance for receiving location updates.
     */
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    /**
     * This callback is called when `FusedLocationProviderClient` has a new `Location`.
     */
    private lateinit var locationCallback: LocationCallback

    /**
     * The flow for receiving location updates.
     */
    private val _locationUpdates = MutableSharedFlow<Location>(replay = 1)

    /**
     * The flow for receiving location updates.
     */
    val locationUpdates = _locationUpdates.asSharedFlow()

    /**
     * Called by the system when the service is first created. Do not call this method directly.
     */
    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {

            /**
             * This method will be executed when `FusedLocationProviderClient` has a new location.
             *
             * @param locationResult The last known location.
             */
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    _locationUpdates.tryEmit(it)
                }
            }
        }
    }

    /**
     * Called by the system every time a client explicitly starts the service by calling
     * `startService(Intent)`, providing the arguments it supplied and a unique integer token
     * representing the start request.
     *
     * @param intent The Intent supplied to `startService(Intent)`, as given. This may be null if
     *      the service is being restarted after its process has gone away.
     * @param flags Additional data about this start request.
     * @param startId A unique integer representing this specific request to start.
     *
     * @return The return value indicates what semantics the system should use for the service's
     *      current started state.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        return START_NOT_STICKY
    }

    /**
     * Return the communication channel to the service. May return `null` if clients can not bind to
     * the service. The returned `IBinder` is usually for a complex interface that has been
     * described using aidl.
     *
     * Note that unlike other application components, calls on to the `IBinder` interface returned
     * here may not happen on the main thread of the process. More information about the main thread
     * can be found in the official Android documentation (`Processes and Threads`).
     *
     * @param intent The `Intent` that was used to bind to this service, as given to
     *      `bindService()`. Note that any extras that were included with the `Intent` at that point
     *      will not be seen here.
     *
     * @return Return an `IBinder` through which clients can call on to the service.
     */
    override fun onBind(intent: Intent): IBinder = localBinder

    /**
     * Creates a notification channel for the foreground service.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_description)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Creates and returns a notification for the foreground service.
     *
     * @return The notification to display while the service is running.
     */
    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            flags
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    /**
     * Subscribes this application to get the location changes via the `locationCallback()`.
     */
    fun subscribeToLocationUpdates() {
        LocationTrackingPreferences.setTrackingEnabled(this, true)

        val hasFineLocation = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocation) {
            LocationTrackingPreferences.setTrackingEnabled(this, false)
            return
        }

        val locationRequest = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_INTERVAL_MS)
            .setMinUpdateIntervalMillis(MIN_UPDATE_INTERVAL_MS)
            .setMaxUpdateDelayMillis(MAX_UPDATE_DELAY_MS)
            .setWaitForAccurateLocation(false)
            .build()

        try {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let(_locationUpdates::tryEmit)
                }

            fusedLocationProviderClient
                .getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    CancellationTokenSource().token
                )
                .addOnSuccessListener { location ->
                    location?.let(_locationUpdates::tryEmit)
                }

            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper(),
            )
        } catch (unlikely: SecurityException) {
            LocationTrackingPreferences.setTrackingEnabled(this, false)
        }
    }

    /**
     * Unsubscribes this application from location changes.
     */
    fun unsubscribeToLocationUpdates() {
        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            LocationTrackingPreferences.setTrackingEnabled(this, false)
        } catch (_: SecurityException) {
            LocationTrackingPreferences.setTrackingEnabled(this, true)
        }
    }
}
