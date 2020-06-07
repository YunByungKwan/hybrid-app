package com.example.hybridapp.util

import android.Manifest
import android.util.Log
import androidx.annotation.IntDef
import androidx.annotation.StringDef
import androidx.core.app.NotificationManagerCompat
import com.google.zxing.integration.android.IntentIntegrator

class Constants {
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(REQ_PERM_CODE_CAMERA)
    @StringDef(TAG_UTILS, TAG_TOAST, TAG_SNACKBAR, TAG_DIALOG, TAG_PERMISSION,
        PERM_CAMERA, TAG_NETWORK, PERM_WRITE_EXTERNAL_STORAGE, PERM_READ_EXTERNAL_STORAGE
    )
    annotation class types

   companion object {

       /** WebView Url */
       const val BASE_URL = "file:///android_asset"
       const val URL = "file:///android_asset/demo/index.html"
       const val SHARED_FILE_NAME = "prefs"

       /** 화면 정보 */
       const val SCREEN_WIDTH = "width"
       const val SCREEN_HEIGHT = "height"

       /** 권한 이름 */
       const val PERM_CAMERA = Manifest.permission.CAMERA
       const val PERM_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
       const val PERM_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
       const val PERM_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
       const val PERM_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
       const val PERM_SEND_SMS = Manifest.permission.SEND_SMS

       /** 인터페이스, 액션 이름 */
       const val TYPE_DIALOG = "Dialog"
       const val TYPE_NETWORK = "Network"
       const val TYPE_CAMERA_DEVICE_RATIO = "CameraByDeviceRatio"
       const val TYPE_CAMERA_RATIO = "CameraByRatio"
       const val TYPE_PHOTO_DEVICE_RATIO = "PhotoByDeviceRatio"
       const val TYPE_PHOTO_RATIO = "PhotoByRatio"
       const val TYPE_MULTI_PHOTO_DEVICE_RATIO = "MultiPhotoByDeviceRatio"
       const val TYPE_MULTI_PHOTO_RATIO = "MultiPhotoByRatio"
       const val TYPE_QR_CODE_SCAN = "QRCodeScan"
       const val TYPE_LOCATION = "Location"
       const val TYPE_BIO_AUTHENTICATION = "BioAuthentication"
       const val TYPE_LOCAL_REPO = "LocalRepository"
       const val TYPE_POP_UP = "WebPopup"

       /** Class or Activity LOG TAG */
       const val TAG = "Hybrid App"
       const val TAG_UTILS = "Utils class"
       const val TAG_MAIN = "MainActivity class"
       const val TAG_TOAST = "Toast class"
       const val TAG_SNACKBAR = "Snackbar class"
       const val TAG_DIALOG = "Dialog class"
       const val TAG_NETWORK = "Network class"
       const val TAG_PHOTO = "Photo class"
       const val TAG_BIO_AUTH = "BioAuth class"
       const val TAG_SHARED = "SharedPreferences class"
       const val TAG_FCM_SERVICE = "FCM"

       /** Function LOG TAG */
       const val TAG_PERMISSION = "PERMISSION"

       /** 다이얼로그 텍스트 */
       const val DIAL_TITLE = "알림"
       const val DIAL_POS = "설정"
       const val DIAL_NEG = "닫기"

       /** 권한 다이얼로그 텍스트 */
       const val DIAL_MSG_CAMERA = "앱을 사용하기 위해 카메라 권한이 필요합니다."
       const val DIAL_MSG_WRITE = "앱을 사용하기 위해 저장소 쓰기 권한이 필요합니다."
       const val DIAL_MSG_READ_WRITE = "앱을 사용하기 위해 저장소 읽기/쓰기 권한이 필요합니다."
       const val DIAL_MSG_LOCATION = "앱을 사용하기 위해 위치 권한이 필요합니다."
       const val DIAL_MSG_SEND_SMS = "앱을 사용하기 위해 SMS 보내기 권한이 필요합니다."

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

       const val BASE64_URL = "data:image/jpeg;base64,"

       /** 알림 텍스트 */
       const val NOTI_CHANNEL_ID = "com.example.hybridapp"
       const val NOTI_CHANNEL_NAME = "롯데정보통신"
       const val NOTI_DESC = "롯데정보통신 알림 채널입니다."
       const val NOTI_DEFAULT: Int = NotificationManagerCompat.IMPORTANCE_DEFAULT
       const val NOTI_HIGH: Int = NotificationManagerCompat.IMPORTANCE_HIGH
       const val NOTIFICATION_ID = 101

       /** SharedPreferences */
       const val SET_DATA_SHARED = 0
       const val GET_DATA_SHARED = 1
       const val DELETE_DATA_SHARED = 2

       /** 로그 텍스트 */
       const val LOG_MSG_CAMERA = "Camera app can't be launched."
       const val LOG_MSG_GALLERY = "Gallery app can't be launched."
       const val LOG_MSG_NOT_CHANNEL = "No notification channel required."
       const val RESULT_CANCELED = "Cancel"

       /** 네트워크 상태 텍스트 */
       const val MSG_DISCONNECTED = "No Connection!"
       const val MSG_CELLULAR = "Cellular Connection!"
       const val MSG_WIFI = "WIFI Connection!"
       const val STATUS_DISCONNECTED = 0
       const val STATUS_CELLULAR = 1
       const val STATUS_WIFI = 2

       /** SMS 텍스트 */
       const val MSG_SMS_SUCCESS = "문자 메시지를 전송하였습니다."
       const val LOG_MSG_SMS_SUCCESS = "SMS Status: SUCCESS."
       const val LOG_MSG_SMS_CANCELED = "SMS Status: CANCELED."
       const val LOG_MSG_SMS_ERROR = "SMS Status: ERROR."
       const val LOG_MSG_SMS_TIMEOUT = "SMS Status: TIMEOUT."

       /** INTENT REQUEST CODE */
       const val REQ_CODE_CAMERA_DEVICE_RATIO = 10001
       const val REQ_CODE_CAMERA_RATIO = 10002
       const val REQ_CODE_PHOTO_DEVICE_RATIO = 10003
       const val REQ_CODE_PHOTO_RATIO = 10004
       const val REQ_CODE_MULTI_PHOTO_DEVICE_RATIO = 10005
       const val REQ_CODE_MULTI_PHOTO_RATIO = 10006
       const val REQ_CODE_FILE_UPLOAD = 10007
       const val REQ_CODE_QR = IntentIntegrator.REQUEST_CODE

       /** PERMISSION CODE */
       const val REQ_PERM_CODE_CAMERA = 1001
       const val REQ_PERM_CODE_WRITE = 1002
       const val REQ_PERM_CODE_READ_WRITE = 1003
       const val REQ_PERM_CODE_LOCATION = 1004
       const val REQ_PERM_CODE_SEND_SMS = 1005

       const val LOG_PERM_GRANTED_CAMERA = "CAMERA permission is granted."
       const val LOG_PERM_GRANTED_WRITE = "WRITE EXTERNAL STORAGE permission is granted."
       const val LOG_PERM_GRANTED_READ_WRITE
               = "READ/WRITE EXTERNAL STORAGE permission is granted."
       const val LOG_PERM_GRANTED_LOCATION
               = "ACCESS FINE/COARSE LOCATION permission is granted."
       const val LOG_PERM_GRANTED_SEND_SMS = "SEND SMS permission is granted."

       /** Function */

       fun LOGD(message: String) = Log.d(TAG, message)

       fun LOGE(functionName: String, className: String) {
           Log.e(className, "call $functionName() in $className")
       }
   }
}