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
       const val PERM_CAMERA = Manifest.permission.CAMERA
       const val PERM_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
       const val PERM_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

       const val TAG_UTILS = "Utils"
       const val TAG_MAIN = "MainActivity"
       const val TAG_INTERFACE = "FlexInterface"

       const val TAG_TOAST = "TOAST"
       const val TAG_SNACKBAR = "SNACKBAR"
       const val TAG_DIALOG = "DIALOG"
       const val TAG_PERMISSION = "PERMISSION"
       const val TAG_NETWORK = "NETWORK"

       const val REQUEST_CAMERA_INTENT = 10001
       const val REQUEST_SINGLE_PHOTO_INTENT = 10002
       const val REQUEST_MULTIPLE_PHOTOS_INTENT = 10003

       const val REQUEST_PERMISSIONS_CAMERA = 1001
       const val REQUEST_PERMISSIONS_SINGLE_PHOTO = 1002
       const val REQUEST_PERMISSIONS_MULTIPLE_PHOTOS = 1003
   }
}