package us.jamesmorrisstudios.rrms2.location

import android.content.Context

data class GeoFence(
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val dwell: Long
)

data class GeoFenceState(
    val inside: Boolean,
    val duration: Long
)

/**
 * Location handler.
 *
 * This manages registered geofences state and change callbacks.
 *
 * This also provides a map UI that allows a user to choose a location for a geofence.
 *
 * https://developer.android.com/training/location/geofencing
 */
interface Location {

    companion object {

        /**
         * The Location handler instance.
         */
        val instance: Location by lazy { LocationImpl() }
    }

    /**
     * Initialize the location handler. Do this in the Application.onCreate method.
     */
    suspend fun initialize(context: Context)

    // TODO build this out.
    // This will be somewhat similar to the Alarm and Notif handlers.
    // GeoFences must be preserved and re-registered across system launches or if the GEOFENCE_NOT_AVAILABLE alert is fired.
    // On boot or at first GeoFence registered the app must determine its current location to resolve state.
    // The location must never be checked unless at least one GeoFence is currently registered.

    // add(guid, GeoFence)

    // remove(guid)

    // getState(guid): GeoFenceState

    // openGeofencePicker(CallbackHandler)

}

/**
 *
 */
private class LocationImpl : Location {

    /**
     * {inherited}
     */
    override suspend fun initialize(context: Context) {

    }


}