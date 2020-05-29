package com.example.hybridapp.util

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.SmsManager
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.hybridapp.App
import com.example.hybridapp.LogUrlRepository
import com.example.hybridapp.LogUrlRoomDatabase
import com.example.hybridapp.R
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.BuildConfig
import com.google.firebase.iid.FirebaseInstanceId
import com.google.zxing.integration.android.IntentIntegrator
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.Executor
import kotlin.collections.ArrayList

class Utils {

    /** ###################################### Dialog ########################################### */

    /** 짧은 Toast 메시지 출력 */
    fun showShortToast(message: String) {
        funLOGE("showShortToast")

        val context = App.INSTANCE
        val duration = Toast.LENGTH_SHORT
        Toast.makeText(context, message, duration).show()
    }

    /** 긴 Toast 메시지 출력 */
    fun showLongToast(message: String) {
        funLOGE("showLongToast")

        val context = App.INSTANCE
        val duration = Toast.LENGTH_LONG
        Toast.makeText(context, message, duration).show()
    }

    /** 짧은 Snackbar 메시지 출력 */
    fun showShortSnackbar(view: View, message: String) {
        funLOGE("showShortSnackbar")

        val duration = Snackbar.LENGTH_SHORT
        Snackbar.make(view, message, duration).show()
    }

    /** 긴 Snackbar 메시지 출력 */
    fun showLongSnackbar(view: View, message: String) {
        funLOGE("showLongSnackbar")

        val duration = Snackbar.LENGTH_LONG
        Snackbar.make(view, message, duration).show()
    }

    /** 다이얼로그 출력 */
    fun showDialog(title: String, message: String) {
        funLOGE("showDialog")
        
        AlertDialog.Builder(App.activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인", null)
            .setNegativeButton("취소", null)
            .create()
            .show()
    }

    /** 다이얼로그 출력 */
    fun showDialog(title: String, message: String, positiveButtonText: String) {
        funLOGE("showDialog")

        AlertDialog.Builder(App.activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText, null)
            .create()
            .show()
    }

    /** 다이얼로그 출력 */
    fun showDialog(title: String, message: String,
                   positiveButtonText: String,
                   negativeButtonText: String) {
        funLOGE("showDialog")

        AlertDialog.Builder(App.activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText, null)
            .setNegativeButton(negativeButtonText, null)
            .create()
            .show()
    }

    /** 다이얼로그 출력 */
    fun showDialog(title: String, message: String,
                           positiveButtonText: String,
                           negativeButtonText: String,
                           pListener: DialogInterface.OnClickListener?,
                           nListener: DialogInterface.OnClickListener?) {
        funLOGE("showDialog")

        AlertDialog.Builder(App.activity
        )
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText, pListener)
            .setNegativeButton(negativeButtonText, nListener)
            .create()
            .show()
    }

    /** #################################### Permission ######################################### */

    /** 해당 권한이 있는지 확인 */
    internal fun hasPermissionFor(permissionName: String): Boolean
            = (ContextCompat.checkSelfPermission(App.INSTANCE, permissionName)
                    == PackageManager.PERMISSION_GRANTED)

    /** 해당 권한이 거절된 적이 있는지 확인 */
    internal fun isRejectPermission(permissionName: String): Boolean =
        ActivityCompat.shouldShowRequestPermissionRationale(App.activity, permissionName)

    /** 다수 권한 요청 */
    fun requestPermissions(permissions: Array<out String>, requestCode: Int) {
        funLOGE("requestPermissions")

        ActivityCompat.requestPermissions(App.activity, permissions, requestCode)
    }

    fun checkPermissions(permissions: Array<String>, requestCode: Int) {
        funLOGE("checkPermissions")

        val deniedPermissions = ArrayList<String>()

        for(permission in permissions) {
            if(!hasPermissionFor(permission)) {
                deniedPermissions.add(permission)
            }
        }

        if(deniedPermissions.isNotEmpty()) {
            requestPermissions(deniedPermissions.toTypedArray(), requestCode)
        }
    }


    /** 권한 거부 시 다이얼로그 */
    fun showPermissionDeniedDialog() {
        val title: String = App.INSTANCE.getString(R.string.permission_denied_dialog_title)
        val message = App.INSTANCE.getString(R.string.permission_denied_dialog_message)
        val positiveButtonText
                = App.INSTANCE.getString(R.string.permission_denied_dialog_positiveButtonText)

        showDialog(title, message, positiveButtonText)
    }

    /** 앱 설정 인텐트 가져오기 */
    fun takeAppSettingsIntent(): Intent {
        val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        settingsIntent.data = Uri.parse("package:"+ BuildConfig.APPLICATION_ID)
        settingsIntent.addCategory(Intent.CATEGORY_DEFAULT)
        settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        return settingsIntent
    }

    /** ######################################### Network ####################################### */

    /**
     * activeNetworkInfo is deprecated in API level 29(Q)
     *
     * activeNetwork is added in API level 23(M)
     * getNetworkCapabilities() is added in API level 21(L)
     * hasTransport() is added in API level 21(L)
     */
    /** 네트워크 연결 체크 */
    fun isNetworkConnected(): Boolean {
        funLOGE("isNetworkConnected")

        val connectivityManger
                = App.activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(Constants.TAG_NETWORK, "Build version is greater than Marshmallow.")

            val activeNetwork = connectivityManger.activeNetwork ?: return false
            val networkCapabilities
                    = connectivityManger.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Log.e(Constants.TAG_NETWORK, "")

                    true
                }
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                else -> false
            }
        } else {
            Log.e(Constants.TAG_NETWORK, "Build version is smaller than Marshmallow.")

            connectivityManger.activeNetworkInfo ?: return false
        }

        return true
    }

    /**
     * 네트워크 상태를 확인
     *
     * return :
     * 0 : 연결되지 않음
     * 1 : 데이터 연결됨
     * 2 : 와이파이 연결됨
     */
    fun getNetworkStatus(): Int {
        funLOGE("getNetworkStatus")

        val connectivityManger
                = App.activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(Constants.TAG_NETWORK, "Build version is greater than Marshmallow.")

            val activeNetwork = connectivityManger.activeNetwork ?: return 0
            val networkCapabilities
                    = connectivityManger.getNetworkCapabilities(activeNetwork) ?: return 0
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> 1
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> 2
                else -> 0
            }
        } else {
            Log.e(Constants.TAG_NETWORK, "Build version is smaller than Marshmallow.")

            connectivityManger.activeNetworkInfo ?: return 0
        }

        return 0
    }

    /** ######################################## Photo ########################################## */

    /** 카메라 인텐트 가져오기 */
    fun takeCameraIntent(): Intent {
        funLOGE("takeCameraIntent")

        return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    }

    /** QR Code 스캔 */
    fun takeQRCodeReader() {
        val intentIntegrator = IntentIntegrator(App.activity)
        intentIntegrator.setBeepEnabled(false)
        intentIntegrator.initiateScan()
    }

    /** 갤러리 인텐트 가져오기 */
    fun takeSinglePhotoIntent(): Intent {
        funLOGE("takeSinglePhotoIntent")

        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"

        return galleryIntent
    }

    /** 갤러리 인텐트 가져오기(여러 장) */
    fun takeMultiplePhotosIntent(): Intent {
        funLOGE("takeMultiplePhotosIntent")

        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = MediaStore.Images.Media.CONTENT_TYPE
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        return galleryIntent
    }

    /** 암시적 인텐트를 받을 수 있는 앱이 있는지 확인 */
    fun existsReceiveActivity(intent: Intent, packageManager: PackageManager): Boolean {
        funLOGE("existsReceiveActivity")

        return intent.resolveActivity(packageManager) != null
    }

    /** Uri->Base64 로 변환 */
    internal fun convertUriToBase64(uri: Uri): String {
        funLOGE("convertUriToBase64")

        val bitmap = convertUriToBitmap(uri)

        return convertBitmapToBase64(bitmap)
    }

    /** Uri->Bitmap 으로 변환 */
    private fun convertUriToBitmap(uri: Uri): Bitmap {
        funLOGE("convertUriToBitmap")

        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val source
                    = ImageDecoder.createSource(App.INSTANCE.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(App.INSTANCE.contentResolver, uri)
        }
    }

    /** Bitmap->Base64 로 변환 */
    internal fun convertBitmapToBase64(bitmap: Bitmap): String {
        funLOGE("convertBitmapToBase64")

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(
            Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    /** ######################################## Audio ########################################## */

    /** ###################################### Location ######################################### */
    // MainActivity에 있음

    /** ################################# BiometricPrompt ####################################### */

    fun showBiometricPrompt(fragmentActivity: FragmentActivity){
        funLOGE("showBiometricPrompt")

        var biometricPromptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(App.INSTANCE.getString(R.string.biometricPrompt_title))
            .setDescription(App.INSTANCE.getString(R.string.biometricPrompt_description))
            .setSubtitle(App.INSTANCE.getString(R.string.biometricPrompt_sub_title))
            .setNegativeButtonText(App.INSTANCE.getString(R.string.biometricPrompt_negative_button))
            .build()

        val authenticationCallback = getAuthenticationCallback()
        val biometricPrompt = BiometricPrompt(fragmentActivity, Executor {  }, authenticationCallback)

        biometricPrompt.authenticate(biometricPromptInfo)
    }

    private fun getAuthenticationCallback() = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            funLOGE("onAuthenticationError")

            //super.onAuthenticationError(errorCode, errString)


        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            funLOGE("onAuthenticationSucceeded")

            // super.onAuthenticationSucceeded(result)
        }

        override fun onAuthenticationFailed() {
            funLOGE("onAuthenticationFailed")

            //super.onAuthenticationFailed()
        }
    }

    /** ######################################### SMS ########################################### */

    /**
     * 문자 형식
     * <#> 으로 시작해야 한다.
     * 11자 해시 문자열로 끝나야 한다.
     * 140 byte 이하여야 한다.
     * Example>
     *
     * <#>
     * 테스트입니다.
     * amvosl3kf/u
     */

    /** SMS 보내기 */
    fun sendSMS(phoneNumber: String, message: String) {
        val smsManager = SmsManager.getDefault()
        smsManager
            .sendTextMessage(phoneNumber, null, message, null, null)
    }

    /** SMS Retriever instance 생성 */
    fun createSmsRetriever() {
        funLOGE("createSmsRetriever")

        val client = SmsRetriever.getClient(App.activity)

        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            Log.e(Constants.TAG_UTILS, "createSmsRetriever() success")
        }

        task.addOnFailureListener {
            Log.e(Constants.TAG_UTILS, "createSmsRetriever() fail")
        }
    }

    fun getAppSignatures(context: Context): ArrayList<String> {
        val appCodes: ArrayList<String> = ArrayList()

        try {
            val packageName: String = context.packageName
            val packageManager: PackageManager = context.packageManager
            val signatures: Array<Signature> =
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures

            for (signature in signatures) {
                val hash = getHash(packageName, signature.toCharsString())
                if (hash != null) {
                    appCodes.add(String.format("%s", hash))
                }
                Log.e(Constants.TAG_UTILS,
                    String.format("이 값을 SMS 뒤에 써서 보내주면 됩니다 : %s", hash)
                )
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(Constants.TAG_UTILS, "Unable to find package to obtain hash. : $e")
        }

        return appCodes
    }

    /** 해쉬값 얻기 */
    private fun getHash(packageName: String, signature: String): String? {
        funLOGE("getHash")

        val appInfo = "$packageName $signature"

        try {
            val messageDigest: MessageDigest = MessageDigest.getInstance("SHA-256")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
            }
            var hashSignature: ByteArray = messageDigest.digest()

            hashSignature = hashSignature.copyOfRange(0, 9)

            var base64Hash = encode11DigitsBase64String(hashSignature)

            Log.e(Constants.TAG_UTILS,
                String.format("\nPackage : %s\nHash : %s", packageName, base64Hash))

            return base64Hash
        } catch (e: NoSuchAlgorithmException) {
            Log.e(Constants.TAG_UTILS, "hash:NoSuchAlgorithm : $e")
        }

        return null
    }

    /** 11자리 Base64로 인코딩 */
    private fun encode11DigitsBase64String(hashSignature: ByteArray): String {
        return Base64.encodeToString(hashSignature,
            Base64.NO_PADDING or Base64.NO_WRAP).substring(0, 11)
    }

    /** ##################################### Notification ###################################### */

    /**
     * This function is available on API level 26 and higher
     * Badge: >= API level 26
     * API level 26부터 모든 알림은 채널에 할당되어야 함. 그렇지 않으면 알림이 나타나지 않음
     * API level 25 이하는 앱 단위로만 알림을 관리할 수 있음. 각 앱이 채널을 하나만 가짐
     * API level 26 이상에서 알림의 중요도는 알림이 게시된 채널의 importance에 따라 결정됨
     * API level 25 이하에서는 각 알림의 중요도는 알림의 priority에 따라 결정됨
     */
    fun createNotificationChannel(importance: Int, showBadge: Boolean,
                                  name: String, descriptionText: String) {
        funLOGE("createNotificationChannel")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e(Constants.TAG_NOTIFICATION,
                "Version is greater(or equals) than ${Build.VERSION_CODES.O}")

            val channelId = "${App.activity.packageName}-$name"
            val mChannel = NotificationChannel(channelId, name, importance)
            mChannel.description = descriptionText
            mChannel.setShowBadge(showBadge)

            val notificationManager
                    = App.activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        } else {
            Log.e(Constants.TAG_NOTIFICATION,
                "SDK VERSION ${Build.VERSION.SDK_INT} doesn't need channel.")
        }
    }

    fun createNotification(channelId: String, icon: Int, title: String,
                           content: String, pendingIntent: PendingIntent) {
        funLOGE("createNotification")

        val builder = NotificationCompat.Builder(App.activity, channelId)
        builder.setSmallIcon(icon)
        builder.setContentTitle(title)
        builder.setContentText(content)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        builder.setAutoCancel(true)
        builder.setContentIntent(pendingIntent)

        val notificationManager
                = NotificationManagerCompat.from(App.activity)
        notificationManager.notify(Constants.NOTIFICATION_ID, builder.build())
    }

    /** ######################################### FCM ########################################### */
    /**
     * FCM: https://console.firebase.google.com/project/hybridfcm-161a7/notification
     * Reference: https://blog.naver.com/ndb796/221553341369
     */

    /** ######################################### NFC ########################################### */



    /** ##################################### Identifier ######################################## */

    /** Instance ID 획득 */
    fun getInstanceId(): String {
        funLOGE("getInstanceId")

        var instanceId = ""
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener {task ->
                if(task.isSuccessful) {
                    val token = task.result?.token

                    if(token != null) {
                        instanceId = token
                    } else {
                        Log.e(Constants.TAG_IDENTIFIER, "token is null.")
                    }
                } else {
                    Log.e(Constants.TAG_IDENTIFIER, "getInstanceId failed", task.exception)
                }
            }

        return instanceId
    }

    /** GUID 획득 */
    fun getGUID(): String {
        funLOGE("getGUID")

        return UUID.randomUUID().toString()
    }

    /** ################################### SharedPreferences ################################### */

    /** SharedPreferences에 데이터 저장 */
    fun putDataToPreferences(fileName: String, key: String, value: Any?) {
        funLOGE("putDataToPreferences")

        val prefs = App.activity.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        when (value) {
            is Boolean -> {
                Log.e(Constants.TAG_UTILS, "Value is Boolean.")

                editor.putBoolean(key, value).apply()
            }
            is Float -> {
                Log.e(Constants.TAG_UTILS, "Value is Float.")

                editor.putFloat(key, value).apply()
            }
            is Int -> {
                Log.e(Constants.TAG_UTILS, "Value is Int.")

                editor.putInt(key, value).apply()
            }
            is Long -> {
                Log.e(Constants.TAG_UTILS, "Value is Long.")

                editor.putLong(key, value).apply()
            }
            is String -> {
                Log.e(Constants.TAG_UTILS, "Value is String.")

                editor.putString(key, value).apply()
            }
            else -> {
                Log.e(Constants.TAG_UTILS, "Value is Set<String>.")

                editor.putStringSet(key, value as Set<String>).apply()
            }
        }
    }

    /** SharedPreferences에서 데이터 불러오기 */

    fun getBooleanFromPreferences(fileName: String, key: String): Boolean {
        funLOGE("getBooleanFromPreferences")

        val prefs = App.activity.getSharedPreferences(fileName, Context.MODE_PRIVATE)

        return prefs?.getBoolean(key, false) ?: false
    }

    fun getFloatFromPreferences(fileName: String, key: String): Float {
        funLOGE("getFloatFromPreferences")

        val prefs = App.activity.getSharedPreferences(fileName, Context.MODE_PRIVATE)

        return prefs?.getFloat(key, 0F) ?: 0F

    }

    fun getIntFromPreferences(fileName: String, key: String): Int {
        funLOGE("getIntFromPreferences")

        val prefs = App.activity.getSharedPreferences(fileName, Context.MODE_PRIVATE)

        return prefs?.getInt(key, 0) ?: 0
    }

    fun getLongFromPreferences(fileName: String, key: String): Long {
        funLOGE("getLongFromPreferences")

        val prefs = App.activity.getSharedPreferences(fileName, Context.MODE_PRIVATE)

        return prefs?.getLong(key, 0) ?: 0
    }

    fun getStringFromPreferences(fileName: String, key: String): String {
        funLOGE("getStringFromPreferences")

        val prefs = App.activity.getSharedPreferences(fileName, Context.MODE_PRIVATE)

        return if(prefs != null) prefs.getString(key, "")!! else ""
    }

    fun getStringSetFromPreferences(fileName: String, key: String) {
        funLOGE("getStringSetFromPreferences")
        TODO("구현 필요")
    }

    /** SharedPreferences에서 해당 키값 데이터 제거 */
    fun removeDataFromPreferences(fileName: String, key: String) {
        funLOGE("removeDataFromPreferences")

        val prefs = App.activity.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.remove(key).apply()
    }

    /** 앱 접속 이력 관리 */
    fun loadAllUrlLog() {
        val prefs = App.activity.getSharedPreferences(
            App.INSTANCE.getString(R.string.url_log), Context.MODE_PRIVATE)

        val urlLogMap = prefs.all
        val sortedUrlLogMap = urlLogMap.toSortedMap(reverseOrder())

        Log.e(Constants.TAG_UTILS, "============================= Visit Url Log =============================")
        for(i in sortedUrlLogMap) {
            Log.e(Constants.TAG_UTILS, "접속시간: ${i.key} 접속 URL: ${i.value}")
        }
    }

    fun createDatabase() {
        val logUrlDao = LogUrlRoomDatabase.getDatabase(App.INSTANCE).logUrlDao()
        val repository = LogUrlRepository(logUrlDao)
        val allLogUrls = repository.allLogUrls
    }

    fun insert() {

    }

    /** ########################################## ETC ########################################## */

    /** log.e wrapper function */
    private fun funLOGE(functionName: String) {
        Log.e(Constants.TAG_UTILS, "call $functionName() in ${Constants.TAG_UTILS}")
    }
}