package com.example.hybridapp.util.module

import android.app.ProgressDialog
import android.content.Context
import android.location.Location
import android.location.LocationManager
import app.dvkyun.flexhybridand.FlexAction
import com.example.hybridapp.App
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Location {

    /** 현재 위치(위도, 경도) 가져오기 */
    fun getCurrent(action: FlexAction?) {
        Constants.LOGD("Call getCurrent() in Location object.")

        val locationPermissions = arrayOf(Constants.PERM_ACCESS_FINE_LOCATION,
            Constants.PERM_ACCESS_COARSE_LOCATION)

        if(Utils.existAllPermission(locationPermissions)) {
            Constants.LOGD("All location permissions exist.")

            val basicActivity = App.activity as BasicActivity
            basicActivity.locationAction = action

            if(isLocationEnabled(basicActivity)) {
                val mLocationRequest = getLocationRequest()
                val mFusedLocationClient = LocationServices
                    .getFusedLocationProviderClient(basicActivity)
                mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback,
                    null)
            } else {
                action?.promiseReturn(null)
            }
        } else {
            Utils.checkDangerousPermissions(locationPermissions, Constants.REQ_PERM_CODE_LOCATION)
            action?.promiseReturn(null)
        }
    }

    private fun isLocationEnabled(context: Context): Boolean {
        var locationManager = context.getSystemService(Context.LOCATION_SERVICE)
                as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER
        )
    }

    private fun getLocationRequest(): LocationRequest {
        Constants.LOGD("Create LocationRequest() in Location object.")

        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        return mLocationRequest
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            CoroutineScope(Dispatchers.Main).launch {
                Constants.LOGD("Call onLocationResult() in LocationCallback object.")

                var mLastLocation: Location = locationResult.lastLocation
                val latitude = mLastLocation.latitude.toString()
                val longitude = mLastLocation.longitude.toString()
                Constants.LOGD("Latitude: $latitude, Longitude: $longitude")

                (App.activity as BasicActivity).locationAction?.promiseReturn(
                    "Latitude: $latitude, Longitude: $longitude")
            }
        }
    }
}