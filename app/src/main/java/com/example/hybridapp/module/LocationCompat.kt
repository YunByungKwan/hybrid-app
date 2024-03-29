package com.example.hybridapp.module

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.location.Location
import android.location.LocationManager
import androidx.activity.result.contract.ActivityResultContracts
import app.dvkyun.flexhybridand.FlexAction
import app.dvkyun.flexhybridand.FlexLambda
import com.example.hybridapp.App
import com.example.hybridapp.MainActivity
import com.example.hybridapp.R
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LocationCompat(private val basicAct: MainActivity) {

    var flexAction: FlexAction? = null

    /** 현재 위치의 위도,경도 가져오기 */
    @SuppressLint("MissingPermission")
    fun getCurrentLatitudeAndLongitude() {
        if(isLocationProviderEnabled(basicAct)) {
            // 뒷배경 뷰 생성
            //val mInflater = Utils.getLayoutInflater(App.activity)
            //basicActivity.backgroundView = mInflater.inflate(R.layout.background_popup, App.activity.constraintLayout, true)
            //App.activity.constraintLayout.addView(basicActivity.backgroundView)
            Utils.visibleProgressBar()

            val mLocationRequest = getLocationRequest()
            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(basicAct)
            mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
            return
        } else {
            val posListener = DialogInterface.OnClickListener { _, _ ->
                val returnObj = Utils.returnJson(true,
                    null, App.INSTANCE.getString(R.string.msg_not_load_lat_lot))
                flexAction?.promiseReturn(returnObj)
            }

            DialogCompat.showDialog("위치 권한", "GPS를 켜야 합니다", "확인",
                null, null, posListener,
                null, null,
                { val returnObj = Utils.returnJson(true,
                    null, App.INSTANCE.getString(R.string.msg_not_load_lat_lot))
                    flexAction?.promiseReturn(returnObj)  })
        }
    }

    /** GPS를 사용 가능 여부 판별 */
    private fun isLocationProviderEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER
        )
    }

    /** LocationRequest 생성 후 반환 */
    private fun getLocationRequest(): LocationRequest = LocationRequest().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 0
        fastestInterval = 0
        numUpdates = 1
    }

    /** Location Callback */
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            CoroutineScope(Dispatchers.Main).launch {
                var mLastLocation: Location = locationResult.lastLocation
                val latitude = mLastLocation.latitude.toString()
                val longitude = mLastLocation.longitude.toString()

                val locObj = JSONObject()
                locObj.put("lat", latitude)
                locObj.put("lot", longitude)

                val returnObj = Utils.returnJson(true, locObj, null)
                //basicActivity.constraintLayout.removeView(basicActivity.backgroundView)
                Utils.invisibleProgressBar()
                flexAction?.promiseReturn(returnObj)
            }
        }
    }

    /**=================================== Location Action =======================================*/
    val getLocationAction
            = FlexLambda.action { action, _->
        withContext(Dispatchers.Main) {
            flexAction = action
            val permissions = arrayOf(Constants.PERM_ACCESS_FINE_LOCATION,
                Constants.PERM_ACCESS_COARSE_LOCATION)
            if(Utils.existAllPermission(permissions)) {
                getCurrentLatitudeAndLongitude()
            } else {
                permissionsResult.launch(permissions)
            }
        }
    }

    /** onRequestPermissionResult */
    private val permissionsResult
            = basicAct.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if(Utils.existAllPermission(permissions)) {
            getCurrentLatitudeAndLongitude()
        } else {
            val deniedObj = Utils.returnJson(false, null, basicAct.getString(R.string.msg_denied_perm))
            flexAction!!.promiseReturn(deniedObj)
        }
    }
}