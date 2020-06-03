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

       /** 권한 이름 */
       const val PERM_CAMERA = Manifest.permission.CAMERA
       const val PERM_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
       const val PERM_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
       const val PERM_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
       const val PERM_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
       const val PERM_ACCESS_BACKGROUND_LOCATION = Manifest.permission.ACCESS_BACKGROUND_LOCATION
       const val PERM_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
       const val PERM_SEND_SMS = Manifest.permission.SEND_SMS

       /** 인터페이스, 액션 이름 */
       const val TYPE_SHORT_SNACKBAR = "ShortSnackbar"
       const val TYPE_LONG_SNACKBAR = "LongSnackbar"
       const val TYPE_DIALOG = "Dialog"
       const val TYPE_NETWORK = "Network"
       const val TYPE_NETWORK_STATUS = "NetworkStatus"
       const val TYPE_CAMERA = "Camera"
       const val TYPE_QR_CODE_SCAN = "QRCodeScan"
       const val TYPE_SINGLE_PHOTO = "SinglePhoto"
       const val TYPE_MULTI_PHOTO = "MultiplePhotos"
       const val TYPE_LOCATION = "Location"
       const val TYPE_BIO_AUTHENTICATION = "BioAuthentication"
       const val TYPE_RECORD = "Record"
       const val TYPE_SEND_SMS = "SendSMS"
       const val TYPE_RECEIVE_SMS = "ReceiveSMS"
       const val TYPE_NOTIFICATION = "Notification"
       const val TYPE_SAVE_SHARED_PREFERENCES = "SaveSharedPreferences"
       const val TYPE_LOAD_SHARED_PREFERENCES = "LoadSharedPreferences"
       const val TYPE_URL_LOG = "LogUrl"
       const val TYPE_POP_UP_WINDOW = "PopUp"
       const val TYPE_LOCAL_REPO = "LocalRepository"

       /** WebView Url */
       const val BASE_URL = "file:///android_asset"

       const val SHARED_FILE_NAME = "prefs"

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
       const val TOAST_MSG_CELLULAR = "데이터에 연결되어 있습니다."
       const val TOAST_MSG_WIFI = "와이파이에 연결되어 있습니다."
       const val TOAST_MSG_NO = "네트워크가 연결되어 있지 않습니다."

       /** 다이얼로그 텍스트 */
       const val DIAL_TITLE = "알림"
       const val DIAL_POS = "설정"
       const val DIAL_NEG = "닫기"

       const val DIAL_MSG_CAMERA = "앱을 사용하기 위해 카메라 권한이 필요합니다."
       const val DIAL_MSG_GALLERY = "앱을 사용하기 위해 저장소 읽기/쓰기 권한이 필요합니다."
       const val DIAL_MSG_SMS = "앱을 사용하기 위해 SMS 보내기 권한이 필요합니다."

       const val DENIED_DIAL_TITLE = "알림"
       const val DENIED_DIAL_MSG = "해당 권한이 거부되었습니다."
       const val DENIED_DIAL_POS = "확인"

       const val BIO_PROMPT_TITLE = "생체 인증"
       const val BIO_PROMPT_DESCRIPTION = "인증을 진행해 주세요."
       const val BIO_PROMPT_SUB_TITLE = "롯데정보통신"
       const val BIO_PROMPT_NEGATIVE_BUTTON = "취소"

       const val BIOMETRIC_SUCCESS = "App can authenticate using biometrics."
       const val BIOMETRIC_ERROR_NO_HARDWARE = "No biometric features available on this device."
       const val BIOMETRIC_ERROR_HW_UNAVAILABLE = "Biometric features are currently unavailable."
       const val BIOMETRIC_ERROR_NONE_ENROLLED
               = "The user hasn't associated any biometric credentials with their account."
       
       /** 로그 텍스트 */
       const val LOG_MSG_CAMERA = "Camera app can't be launched."
       const val LOG_MSG_GALLERY = "Gallery app can't be launched."

       const val LOG_RESULT_OK = "RESULT_OK - onActivityResult"

       const val RESULT_CANCELED = "Cancel"

       const val STATUS_NO = 0
       const val STATUS_CELLULAR = 1
       const val STATUS_WIFI = 2

       /** INTENT REQUEST CODE */
       const val REQ_CODE_CAMERA = 10001
       const val REQ_CODE_SINGLE_PHOTO = 10002
       const val REQ_CODE_MULTIPLE_PHOTO = 10003
       const val REQ_CODE_FILE_UPLOAD = 10004
       const val REQ_CODE_QR =  IntentIntegrator.REQUEST_CODE
       const val REQ_CODE_RECORD = 10005

       /** PERMISSION CODE */
       const val REQ_PERM_CODE_CAMERA = 1003
       const val REQ_PERM_CODE_SINGLE_PHOTO = 1004
       const val REQ_PERM_CODE_MULTIPLE_PHOTO = 1005
       const val REQ_PERM_CODE_WRITE = 1011
       const val REQ_PERM_CODE_READ_WRITE = 1010
       const val REQ_PERM_CODE_LOCATION = 1006
       const val REQ_PERM_CODE_RECORD_AUDIO = 1007
       const val REQ_PERM_CODE_SMS = 1008
       const val REQ_PERM_CODE_SEND_SMS = 1009

       const val NOTIFICATION_ID = 101

       const val LOG_PERM_GRANTED_CAMERA = "CAMERA permission is granted."
       const val LOG_PERM_GRANTED_WRITE = "WRITE EXTERNAL STORAGE permission is granted."
       const val LOG_PERM_GRANTED_READ_WRITE
               = "READ/WRITE EXTERNAL STORAGE permission is granted."
       const val LOG_PERM_GRANTED_LOCATION
               = "ACCESS FINE/COARSE LOCATION permission is granted."
       const val LOG_PERM_GRANTED_RECORD_AUDIO = "RECORD AUDIO permission is granted."
       const val LOG_PERM_GRANTED_SEND_SMS = "SEND SMS permission is granted."

       fun LOGE(functionName: String, className: String) {
           Log.e(className, "call $functionName() in $className")
       }
   }
}