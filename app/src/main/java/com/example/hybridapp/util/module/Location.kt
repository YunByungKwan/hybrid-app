package com.example.hybridapp.util.module

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.example.hybridapp.App
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

object Location {

    /** 현재 위치의 위도,경도 가져오기 */
    @SuppressLint("MissingPermission")
    fun getCurrentLatAndLot() {
        Constants.LOGD("Call getCurrentLatAndLot() in Location object.")

        val basicActivity = App.activity as BasicActivity

        // GPS 사용이 가능한 경우
        if(isLocationEnabled(basicActivity)) {
            val mLocationRequest = getLocationRequest()
            val mFusedLocationClient = LocationServices
                .getFusedLocationProviderClient(basicActivity)
            mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, null)

            return
        }
        // GPS 사용이 불가능한 경우
        else {
            val returnObj = Utils.createJSONObject(true,
                null, Constants.MSG_NOT_LOAD_LAT_LOT)
            basicActivity.locationAction?.promiseReturn(returnObj)
        }
    }

    /** GPS를 사용 가능 여부 판별 */
    private fun isLocationEnabled(context: Context): Boolean {
        var locationManager = context.getSystemService(Context.LOCATION_SERVICE)
                as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER
        )
    }

    /** LocationRequest 생성 후 반환 */
    private fun getLocationRequest(): LocationRequest {
        Constants.LOGD("Call getLocationRequest() in Location object.")

        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        return mLocationRequest
    }

    /** Location Callback */
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            CoroutineScope(Dispatchers.Main).launch {
                Constants.LOGD("Call onLocationResult() in LocationCallback object.")

                var mLastLocation: Location = locationResult.lastLocation
                val latitude = mLastLocation.latitude.toString()
                val longitude = mLastLocation.longitude.toString()
                Constants.LOGD("Latitude: $latitude, Longitude: $longitude")

                // 위도, 경도에 대한 JSONObject 생성
                val locObj = JSONObject()
                locObj.put("lat", latitude)
                locObj.put("lot", longitude)

                // promiseReturn할 JSONObject 생성
                val returnObj = Utils.createJSONObject(true,
                    locObj, null)
                (App.activity as BasicActivity).locationAction?.promiseReturn(returnObj)
            }
        }
    }
}