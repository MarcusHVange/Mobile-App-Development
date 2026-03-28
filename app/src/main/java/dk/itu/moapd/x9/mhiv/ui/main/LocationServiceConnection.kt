package dk.itu.moapd.x9.mhiv.ui.main

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import dk.itu.moapd.x9.mhiv.service.LocationService

/**
 * Creates a `ServiceConnection` for the `LocationService`.
 *
 * @param onConnected The callback to be invoked when the service is connected.
 * @param onDisconnected The callback to be invoked when the service is disconnected
 *
 * @return A `ServiceConnection` for the `LocationService`.
 */
internal fun createLocationServiceConnection(
    onConnected: (LocationService) -> Unit,
    onDisconnected: () -> Unit,
): ServiceConnection {
    return object : ServiceConnection {

        /**
         * Called when a connection to the Service has been established, with the
         * `android.os.IBinder` of the communication channel to the Service.
         *
         * If the system has started to bind your client app to a service, it's possible that your
         * app will never receive this callback. Your app won't receive a callback if there's an
         * issue with the service, such as the service crashing while being created.
         *
         * @param name The concrete component name of the service that has been connected.
         * @param service The IBinder of the Service's communication channel, which you can now make
         *      calls on.
         */
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationService.LocalBinder
            onConnected(binder.service)
        }

        /**
         * Called when a connection to the Service has been lost. This typically happens when the
         * process hosting the service has crashed or been killed. This does not remove the
         * ServiceConnection itself -- this binding to the service will remain active, and you will
         * receive a call to `onServiceConnected()` when the Service is next running.
         *
         * @param name The concrete component name of the service whose connection has been lost.
         */
        override fun onServiceDisconnected(name: ComponentName) {
            onDisconnected()
        }
    }
}