package com.example.hybridapp.util

import android.Manifest
import android.util.Log
import androidx.annotation.IntDef
import androidx.annotation.StringDef
import androidx.core.app.NotificationManagerCompat
import com.example.hybridapp.App
import com.example.hybridapp.R
import com.google.zxing.integration.android.IntentIntegrator

class Constants {

   companion object {
       /** Permission Name */
       const val PERM_CAMERA = Manifest.permission.CAMERA
       const val PERM_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
       const val PERM_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
       const val PERM_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
       const val PERM_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
       const val PERM_SEND_SMS = Manifest.permission.SEND_SMS

       /** Permission Code */
       const val PERM_QR_REQ_CODE = 1001
       const val PERM_FILE_REQ_CODE = 1002
       const val PERM_LOCATION_REQ_CODE = 1006
       const val PERM_SEND_SMS_REQ_CODE = 1007
       const val PERM_CAMERA_DEVICE_REQ_CODE = 1008
       const val PERM_CAMERA_REQ_CODE = 1009
       const val PERM_PHOTO_DEVICE_REQ_CODE = 1010
       const val PERM_PHOTO_REQ_CODE = 1011
       const val PERM_MUL_PHOTO_DEVICE_REQ_CODE = 1012
       const val PERM_MUL_PHOTO_REQ_CODE = 1013

       /** Intent Request Code */
       const val CAMERA_DEVICE_RATIO_REQ_CODE = 10001
       const val CAMERA_RATIO_REQ_CODE = 10002
       const val PHOTO_DEVICE_RATIO_REQ_CODE = 10003
       const val PHOTO_RATIO_REQ_CODE = 10004
       const val MULTI_PHOTO_DEVICE_RATIO_REQ_CODE = 10005
       const val MULTI_PHOTO_RATIO_REQ_CODE = 10006
       const val FILE_UPLOAD_REQ_CODE = 10007
       const val QR_REQ_CODE = IntentIntegrator.REQUEST_CODE // 49374
       const val SEND_SMS_REQ_CODE = 10008

       /** Notification Text */
       const val NOTIFICATION_ID = 101
       const val NOTI_DEFAULT: Int = NotificationManagerCompat.IMPORTANCE_DEFAULT
       const val NOTI_HIGH: Int = NotificationManagerCompat.IMPORTANCE_HIGH

       /** Network Status Code */
       const val NET_STAT_DISCONNECTED = 0
       const val NET_STAT_CELLULAR = 1
       const val NET_STAT_WIFI = 2

       /** SharedPreferences Code */
       const val PUT_DATA_CODE = 0
       const val GET_DATA_CODE = 1
       const val DEL_DATA_CODE = 2
   }

}