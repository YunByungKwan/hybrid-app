package com.example.hybridapp.module

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.location.Location
import android.location.LocationManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import com.example.hybridapp.App
import com.example.hybridapp.R
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Utils
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class Location {

    private val basicActivity = App.activity as BasicActivity
    private val deniedObj = Utils.createJSONObject(false, null,
        basicActivity.getString(R.string.msg_denied_perm))

    val requestPermissionResult = basicActivity.registerForActivityResult(
        ActivityResultContracts.RequestPermission(), Manifest.permission.READ_EXTERNAL_STORAGE
    ) { isGranted ->
        if(isGranted) {
            getCurrentLatAndLot()
        } else {
            basicActivity.locationAction!!.promiseReturn(deniedObj)
        }
    }


    /** 현재 위치의 위도,경도 가져오기 */
    @SuppressLint("MissingPermission")
    fun getCurrentLatAndLot() {
        // GPS 사용이 가능한 경우
        if(isLocationEnabled(basicActivity)) {
            // 뒷배경 뷰 생성
            val mInflater = Utils.getLayoutInflater(App.activity)
            basicActivity.backgroundView = mInflater.inflate(R.layout.background_popup, null)
            App.activity.constraintLayout.addView(basicActivity.backgroundView)
            Utils.visibleProgressBar()

            val mLocationRequest = getLocationRequest()
            val mFusedLocationClient = LocationServices
                .getFusedLocationProviderClient(basicActivity)
            mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, null)

            return
        }
        // GPS 사용이 불가능한 경우
        else {
            val posListener = DialogInterface.OnClickListener { _, _ ->
                val returnObj = Utils.createJSONObject(true,
                    null, App.INSTANCE.getString(R.string.msg_not_load_lat_lot))
                basicActivity.locationAction?.promiseReturn(returnObj)
            }

            Dialog.show("위치 권한", "GPS를 켜야 합니다", "확인",
                null, null, posListener,
                null, null,
                { val returnObj = Utils.createJSONObject(true,
                    null, App.INSTANCE.getString(R.string.msg_not_load_lat_lot))
                    basicActivity.locationAction?.promiseReturn(returnObj)  })
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
        Utils.LOGD("Call getLocationRequest() in Location object.")

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
                Utils.LOGD("Call onLocationResult() in LocationCallback object.")

                var mLastLocation: Location = locationResult.lastLocation
                val latitude = mLastLocation.latitude.toString()
                val longitude = mLastLocation.longitude.toString()
                Utils.LOGD("Latitude: $latitude, Longitude: $longitude")

                // 위도, 경도에 대한 JSONObject 생성
                val locObj = JSONObject()
                locObj.put("lat", latitude)
                locObj.put("lot", longitude)

                // promiseReturn할 JSONObject 생성
                val returnObj = Utils.createJSONObject(true,
                    locObj, null)

                val basicActivity = App.activity as BasicActivity
                basicActivity.constraintLayout.removeView(basicActivity.backgroundView)
                Utils.invisibleProgressBar()

                (App.activity as BasicActivity).locationAction?.promiseReturn(returnObj)
            }
        }
    }
}