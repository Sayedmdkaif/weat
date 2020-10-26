package com.example.recordedweather.activity.activity.location

import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes

/**
 * Created by Yash on 22/3/17.
 */
class NetworkUtil(private val activity: AppCompatActivity, private val informer: NetworkNotifier) :
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    LocationListener {
    private var googleApiClient: GoogleApiClient? = null
    private var locationRequest: LocationRequest? = null

    init {

        buildGoogleApiClient()
    }

    private fun buildGoogleApiClient() {
        googleApiClient = GoogleApiClient.Builder(activity)
            .addApi(LocationServices.API).addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).build()


        locationRequest = LocationRequest.create()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.interval = (30 * 1000).toLong()
        locationRequest!!.fastestInterval = (5 * 1000).toLong()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)

        builder.setAlwaysShow(true)

        val result = LocationServices.SettingsApi
            .checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status = result.status

            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS ->

                    Log.i(TAG, "LocationSettingsStatusCodes SUCCESS")
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->

                    try {
                        Log.i(TAG, "LocationSettingsStatusCodes RESOLUTION_REQUIRED")

                        status.startResolutionForResult(activity, 1000)

                    } catch (e: IntentSender.SendIntentException) {
                        Log.i(TAG, "LocationSettingsStatusCodes RESOLUTION_REQUIRED catch")

                        e.printStackTrace()
                    }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ->

                    Log.i(TAG, "LocationSettingsStatusCodes SETTINGS_CHANGE_UNAVAILABLE")
            }
        }
    }

    override fun onConnected(bundle: Bundle?) {
        Log.i(TAG, "onConnected")

        val location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        if (location == null) {
            Log.i(TAG, "onConnected if")

            LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient,
                locationRequest,
                this
            )
        } else {
            Log.i(TAG, "onConnected else")

            handleNewLocation(location)
        }
    }

    override fun onConnectionSuspended(i: Int) {
        Log.i(TAG, "onConnectionSuspended: $i")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.i(TAG, "onConnectionFailed: $connectionResult")

        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(
                    activity,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST
                )

            } catch (e: IntentSender.SendIntentException) {
                e.printStackTrace()
            }

        }

    }

    override fun onLocationChanged(location: Location) {
        Log.i(TAG, "onLocationChanged: $location")

        if (location.toString() != null) {
            handleNewLocation(location)
        } else {
            informer.locationFailed("Location service started but location was provided null")
        }
    }

    private fun handleNewLocation(location: Location) {
        Log.i(TAG, "handleNewLocation: $location")

        informer.locationUpdates(location)

    }

    fun connectGoogleApiClient() {
        Log.i(TAG, "MainAcivity connectGoogleApiClient")

        googleApiClient!!.connect()
    }

    fun disconnectGoogleApiClient() {
        Log.i(TAG, "MainAcivity disconnectGoogleApiClient")

        if (googleApiClient!!.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
            googleApiClient!!.disconnect()
        }
    }

    companion object {
        private val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000

        private val TAG = "SignIn_Screen"
    }

}
