package com.example.hybridapp.util

import android.Manifest
import android.util.Log
import androidx.annotation.IntDef
import androidx.annotation.StringDef
import androidx.core.app.NotificationManagerCompat
import com.google.zxing.integration.android.IntentIntegrator

class Constants {
   companion object {
       @Retention(AnnotationRetention.SOURCE)
       @StringDef(SHARED_APPID_FILE_NAME,
           SHARED_APPID_KEY,
           BASE_URL,
           URL,
           SHARED_FILE_NAME,
           SCREEN_WIDTH,
           SCREEN_HEIGHT,
           PERM_CAMERA,
           PERM_WRITE_EXTERNAL_STORAGE,
           PERM_READ_EXTERNAL_STORAGE,
           PERM_ACCESS_FINE_LOCATION,
           PERM_ACCESS_COARSE_LOCATION,
           PERM_SEND_SMS,
           TYPE_DIALOG,
           TYPE_NETWORK,
           TYPE_CAMERA_DEVICE_RATIO,
           TYPE_CAMERA_RATIO,
           TYPE_PHOTO_DEVICE_RATIO,
           TYPE_PHOTO_RATIO,
           TYPE_MULTI_PHOTO_DEVICE_RATIO,
           TYPE_MULTI_PHOTO_RATIO,
           TYPE_QR_CODE_SCAN,
           TYPE_SEND_SMS,
           TYPE_LOCATION,
           TYPE_AUTH,
           TYPE_LOCAL_REPO,
           TAG,
           TAG_MAIN,
           MSG_DENIED_PERM,
           BIO_PROMPT_TITLE,
           BIO_PROMPT_DESCRIPTION,
           BIO_PROMPT_SUB_TITLE,
           BIO_PROMPT_NEGATIVE_BUTTON,
           BIOMETRIC_SUCCESS,
           BIOMETRIC_ERROR_NO_HARDWARE,
           BIOMETRIC_ERROR_HW_UNAVAILABLE,
           BIOMETRIC_ERROR_NONE_ENROLLED,
           BASE64_URL,
           MSG_NO_QR,
           MSG_NOT_LOAD_QR,
           MSG_NOT_LOAD_CAMERA,
           MSG_NOT_LOAD_GALLERY,
           NOTI_CHANNEL_ID,
           NOTI_CHANNEL_NAME,
           NOTI_DESC,
           MSG_DISCONNECTED,
           MSG_CELLULAR,
           MSG_WIFI,
           MSG_NOT_LOAD_LAT_LOT,
           OBJ_KEY_AUTH,
           OBJ_KEY_DATA,
           OBJ_KEY_MSG,
           LOG_MSG_NOT_CHANNEL,
           LOG_LOG_MSG_SMS_SUCCESS,
           LOG_MSG_SMS_CANCELED,
           LOG_MSG_SMS_ERROR,
           LOG_MSG_SMS_TIMEOUT,
           MSG_NO_ROOTING)
       @IntDef(CAMERA_DEVICE_RATIO_REQ_CODE,
           CAMERA_RATIO_REQ_CODE,
           PHOTO_DEVICE_RATIO_REQ_CODE,
           PHOTO_RATIO_REQ_CODE,
           MULTI_PHOTO_DEVICE_RATIO_REQ_CODE,
           MULTI_PHOTO_RATIO_REQ_CODE,
           FILE_UPLOAD_REQ_CODE,
           QR_REQ_CODE,
           PERM_CAMERA_REQ_CODE,
           PERM_FILE_REQ_CODE,
           PERM_READ_WRITE_REQ_CODE,
           PERM_LOCATION_REQ_CODE,
           PERM_SEND_SMS_REQ_CODE)
       annotation class types

       /** SharedPreferences Default */
       const val SHARED_DEFAULT_STRING = ""
       const val SHARED_DEFAULT_NUMBER = 0
       const val SHARED_DEFAULT_BOOLEAN = false

       /** AppId SharedPreferences filepath */
       const val SHARED_APPID_FILE_NAME = "pref_appid"
       const val SHARED_APPID_KEY = "appId"

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
       const val TYPE_SEND_SMS = "SendSMS"
       const val TYPE_LOCATION = "Location"
       const val TYPE_AUTH = "Authentication"
       const val TYPE_LOCAL_REPO = "LocalRepository"
       const val TYPE_WEB_POP_UP = "WebPopup"
       const val TYPE_DOWNLOAD = "FileDownload"

       const val TAG = "HybridApp"
       const val TAG_MAIN = "MainActivity class"

       const val MSG_DENIED_PERM = "해당 권한이 거부되었습니다"

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

       /** QR Code 텍스트 */
       const val MSG_NO_QR = "QR Code값이 존재하지 않습니다"
       const val MSG_NOT_LOAD_QR = "QR Code값을 불러올 수 없습니다"

       /** Camera 텍스트 */
       const val MSG_NOT_LOAD_CAMERA = "카메라 앱을 실행할 수 없습니다"

       /** Photo 텍스트 */
       const val MSG_NOT_LOAD_GALLERY = "앨범을 실행할 수 없습니다"

       /** 알림 텍스트 */
       const val NOTI_CHANNEL_ID = "com.example.hybridapp"
       const val NOTI_CHANNEL_NAME = "롯데정보통신"
       const val NOTI_DESC = "롯데정보통신 알림 채널입니다."
       const val NOTI_DEFAULT: Int = NotificationManagerCompat.IMPORTANCE_DEFAULT
       const val NOTI_HIGH: Int = NotificationManagerCompat.IMPORTANCE_HIGH


       /** 네트워크 상태 텍스트 */
       const val MSG_DISCONNECTED = "No Connection"
       const val MSG_CELLULAR = "Cellular Connection"
       const val MSG_WIFI = "Wifi Connection"

       /** 위치 텍스트 */
       const val MSG_NOT_LOAD_LAT_LOT = "현재 위도, 경도를 불러올 수 없습니다"

       /** JSONObject key */
       const val OBJ_KEY_AUTH = "auth"
       const val OBJ_KEY_DATA = "data"
       const val OBJ_KEY_MSG = "msg"

       /** 로그 텍스트 */
       const val LOG_MSG_NOT_CHANNEL = "No notification channel required."
       const val LOG_LOG_MSG_SMS_SUCCESS = "SMS Status: SUCCESS."
       const val LOG_MSG_SMS_CANCELED = "SMS Status: CANCELED."
       const val LOG_MSG_SMS_ERROR = "SMS Status: ERROR."
       const val LOG_MSG_SMS_TIMEOUT = "SMS Status: TIMEOUT."

       /** NETWORK STATUS CODE */
       const val NET_STAT_DISCONNECTED = 0
       const val NET_STAT_CELLULAR = 1
       const val NET_STAT_WIFI = 2

       /** NOTIFICATION */
       const val NOTIFICATION_ID = 101

       /** SHAREDPREFERENCES CODE */
       const val PUT_DATA_CODE = 0
       const val GET_DATA_CODE = 1
       const val DEL_DATA_CODE = 2

       /** INTENT REQUEST CODE */
       const val CAMERA_DEVICE_RATIO_REQ_CODE = 10001
       const val CAMERA_RATIO_REQ_CODE = 10002
       const val PHOTO_DEVICE_RATIO_REQ_CODE = 10003
       const val PHOTO_RATIO_REQ_CODE = 10004
       const val MULTI_PHOTO_DEVICE_RATIO_REQ_CODE = 10005
       const val MULTI_PHOTO_RATIO_REQ_CODE = 10006
       const val FILE_UPLOAD_REQ_CODE = 10007
       const val QR_REQ_CODE = IntentIntegrator.REQUEST_CODE // 49374
       const val SEND_SMS_REQ_CODE = 10008

       /** PERMISSION CODE */
       const val PERM_QR_REQ_CODE = 1001
       const val PERM_FILE_REQ_CODE = 1002
       const val PERM_READ_WRITE_REQ_CODE = 1003
       //const val PERM_CAMERA_REQ_CODE = 1005
       const val PERM_LOCATION_REQ_CODE = 1006
       const val PERM_SEND_SMS_REQ_CODE = 1007

       const val PERM_CAMERA_DEVICE_REQ_CODE = 1008
       const val PERM_CAMERA_REQ_CODE = 1009
       const val PERM_PHOTO_DEVICE_REQ_CODE = 1010
       const val PERM_PHOTO_REQ_CODE = 1011
       const val PERM_MUL_PHOTO_DEVICE_REQ_CODE = 1012
       const val PERM_MUL_PHOTO_REQ_CODE = 1013

       /** 루팅체크 */
       const val MSG_NO_ROOTING = "루팅이 확인되지 않았습니다"

       /** Function */
       fun LOGD(message: String) = Log.d(TAG, message)

       fun LOGE(message: String) = Log.e(TAG, message)
   }
}