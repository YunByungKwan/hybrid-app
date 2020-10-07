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
       const val PERM_FILE_REQ_CODE = 1002

       /** Intent Request Code */
       const val FILE_UPLOAD_REQ_CODE = 10007

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