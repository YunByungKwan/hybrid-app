package com.example.hybridapp.util

import android.Manifest
import androidx.annotation.IntDef
import androidx.annotation.StringDef

class Constants {
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(REQUEST_CAMERA_INTENT, REQUEST_PERMISSIONS_CAMERA)
    @StringDef(TAG_UTILS, TAG_TOAST, TAG_SNACKBAR, TAG_DIALOG, TAG_PERMISSION,
        PERM_CAMERA, TAG_NETWORK, PERM_WRITE_EXTERNAL_STORAGE, PERM_READ_EXTERNAL_STORAGE
    )
    annotation class types

   companion object {

       /** PERMISSION NAME */
       const val PERM_CAMERA = Manifest.permission.CAMERA
       const val PERM_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
       const val PERM_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
       const val PERM_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
       const val PERM_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
       const val PERM_ACCESS_BACKGROUND_LOCATION = Manifest.permission.ACCESS_BACKGROUND_LOCATION
       const val PERM_SEND_SMS = Manifest.permission.SEND_SMS

       /** Class or Activity LOG TAG */
       const val TAG_UTILS = "Utils"
       const val TAG_MAIN = "MainActivity"
       const val TAG_INTERFACE = "FlexInterface"
       const val TAG_SMS_RECEIVER = "SMSReceiver"
       const val TAG_FCM_SERVICE = "FCMService"

       /** Function LOG TAG */
       const val TAG_TOAST = "TOAST"
       const val TAG_SNACKBAR = "SNACKBAR"
       const val TAG_DIALOG = "DIALOG"
       const val TAG_PERMISSION = "PERMISSION"
       const val TAG_NETWORK = "NETWORK"
       const val TAG_IDENTIFIER = "IDENTIFIER"
       const val TAG_NFC = "NFC"
       const val TAG_NOTIFICATION = "NOTIFICATION"

       /** INTENT REQUEST CODE */
       const val REQUEST_CAMERA_INTENT = 10001
       const val REQUEST_SINGLE_PHOTO_INTENT = 10002
       const val REQUEST_MULTIPLE_PHOTOS_INTENT = 10003
       const val REQUEST_FILE_UPLOAD = 10004

       /** PERMISSION REQUEST CODE */
       const val REQUEST_PERMISSIONS_ALL = 1001
       const val REQUEST_PERMISSIONS_CAMERA = 1002
       const val REQUEST_PERMISSIONS_SINGLE_PHOTO = 1003
       const val REQUEST_PERMISSIONS_MULTIPLE_PHOTOS = 1004
       const val REQUEST_PERMISSIONS_LOCATION = 1005
       const val REQUEST_PERMISSIONS_SMS = 1006

       const val NOTIFICATION_ID = 101
   }
}