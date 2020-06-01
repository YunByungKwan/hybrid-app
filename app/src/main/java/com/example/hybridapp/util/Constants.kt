package com.example.hybridapp.util

import android.Manifest
import android.util.Log
import androidx.annotation.IntDef
import androidx.annotation.StringDef
import com.google.zxing.integration.android.IntentIntegrator

class Constants {
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(REQ_CODE_CAMERA, REQ_PERM_CODE_CAMERA)
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
       const val PERM_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
       const val PERM_SEND_SMS = Manifest.permission.SEND_SMS

       /** Class or Activity LOG TAG */
       const val TAG_UTILS = "Utils class"
       const val TAG_MAIN = "MainActivity class"
       const val TAG_TOAST = "Toast class"
       const val TAG_SNACKBAR = "Snackbar class"
       const val TAG_DIALOG = "Dialog class"
       const val TAG_NETWORK = "Network class"
       const val TAG_CAMERA = "Camera class"
       const val TAG_PHOTO = "Photo class"
       const val TAG_SMS = "SMS class"
       const val TAG_BIO_AUTH = "BioAuth class"
       const val TAG_NOTIFICATION = "Notification class"
       const val TAG_SHARED = "SharedPreferences class"

       const val TAG_INTERFACE = "FlexInterface"
       const val TAG_SMS_RECEIVER = "SMSReceiver"
       const val TAG_FCM_SERVICE = "FCM"

       /** Function LOG TAG */
       const val TAG_PERMISSION = "PERMISSION"
       const val TAG_IDENTIFIER = "IDENTIFIER"
       const val TAG_NFC = "NFC"

       /** 토스트 텍스트 */
       const val TOAST_MSG_NETWORK_CONNECTED = "네트워크가 연결되어 있습니다."
       const val TOAST_MSG_NETWORK_DISCONNECTED = "네트워크가 연결되어 있지 않습니다."

       /** 다이얼로그 텍스트 */
       const val DIAL_TITLE = "알림"
       const val DIAL_POS = "설정"
       const val DIAL_NEG = "닫기"

       const val DIAL_MSG_CAMERA = "앱을 사용하기 위해 카메라 권한이 필요합니다."
       const val DIAL_MSG_GALLERY = "앱을 사용하기 위해 저장소 읽기/쓰기 권한이 필요합니다."
       const val DIAL_MSG_SMS = "앱을 사용하기 위해 SMS 보내기 권한이 필요합니다."

       const val DIAL_TITLE_DENIED = "알림"
       const val DIAL_MSG_DENIED = "해당 권한이 거부되었습니다."
       const val DIAL_POS_DENIED = "확인"

       /** 로그 텍스트 */
       const val LOG_MSG_CAMERA = "Camera app can't be launched."
       const val LOG_MSG_GALLERY = "Gallery app can't be launched."

       const val LOG_MSG_CAMERA_PERM_SUCCESS = "Camera permission is granted."
       const val LOG_MSG_GALLERY_PERM_SUCCESS = "Read/write storage permission is granted."
       const val LOG_MSG_LOCATION_PERM_SUCCESS = "Location permission is granted."

       const val LOG_RESULT_OK = "RESULT_OK - onActivityResult"

       const val RESULT_CANCELED = "Cancel"

       /** INTENT REQUEST CODE */
       const val REQ_CODE_CAMERA = 10001
       const val REQ_CODE_SINGLE_PHOTO = 10002
       const val REQ_CODE_MULTIPLE_PHOTO = 10003
       const val REQ_CODE_FILE_UPLOAD = 10004
       const val REQ_CODE_QR =  IntentIntegrator.REQUEST_CODE
       const val REQ_CODE_RECORD = 10005

       /** PERMISSION CODE */
       const val REQ_PERM_CODE_ALL = 1001
       const val REQ_PERM_CODE_CAMERA = 1002
       const val REQ_PERM_CODE_SINGLE_PHOTO = 1003
       const val REQ_PERM_CODE_MULTIPLE_PHOTO = 1004
       const val REQ_PERM_CODE_LOCATION = 1005
       const val REQ_PERM_CODE_RECORD = 1006
       const val REQ_PERM_CODE_SMS = 1007

       const val NOTIFICATION_ID = 101

       fun LOGE(functionName: String, className: String) {
           Log.e(className, "call $functionName() in $className")
       }
   }
}