package com.example.hybridapp.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.webkit.MimeTypeMap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import app.dvkyun.flexhybridand.*
import com.example.hybridapp.App
import com.example.hybridapp.R
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.data.LogUrlRepository
import com.example.hybridapp.data.LogUrlRoomDatabase
import com.example.hybridapp.module.Network
import com.example.hybridapp.module.SharedPreferences
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.lang.Exception
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object Utils {

    var fileAction: FlexAction? = null
    var popUpAction: FlexAction? = null
    var fileUrl: String? = null
    var closeBtn: Button? = null

    /**================================ FlexFuncInterface ========================================*/

    @FlexFuncInterface
    fun RootingCheck(array: Array<FlexData>): String {
        return App.INSTANCE.getString(R.string.msg_no_rooting)
    }

    @FlexFuncInterface
    fun UniqueAppID(array: Array<FlexData>): String {
        return getAppId()
    }

    @FlexFuncInterface
    fun UniqueDeviceID(array: Array<FlexData>): String {
        return Utils.getDeviceId(App.INSTANCE)
    }

    @FlexFuncInterface
    fun LogUrl(array: Array<FlexData>) {
        CoroutineScope(Dispatchers.Default).launch {
            val logUrlDao = LogUrlRoomDatabase.getDatabase(App.INSTANCE).logUrlDao()
            val repository = LogUrlRepository(logUrlDao)
            val logUrls = repository.allLogUrls
            for(i in logUrls) {
                Log.e(App.INSTANCE.getString(R.string.tag_main), "${i.id}, ${i.visitingTime}, ${i.visitingUrl}")
            }
        }
    }

    /**======================================  Action ============================================*/

    val downloadAction = FlexLambda.action { action, array ->
        fileAction = action
        fileUrl = array[0].reified()

        if(existAllPermission(arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE))) {
            downloadFileFromUrl(fileUrl)
        }  else {
            requestPermissionResultForFileDownload.launch()
        }
    }

    /** onRequestPermissionResult For File Download */
    private val requestPermissionResultForFileDownload
            = (App.activity as BasicActivity).registerForActivityResult(
        ActivityResultContracts.RequestPermission(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
    { isGranted ->
        if(isGranted) {
            downloadFileFromUrl(fileUrl)
        }
    }

    val localRepository
            = FlexLambda.action { localRepoAction, array ->
        when (array[0].asInt()) {
            Constants.PUT_DATA_CODE -> {
                val key = array[1].asString()!!
                val value = array[2].asString()

                SharedPreferences.putData(App.INSTANCE.getString(R.string.shared_file_name), key, value)
                localRepoAction.promiseReturn(true)
            }
            Constants.GET_DATA_CODE -> {
                val key = array[1].asString()!!
                var value = SharedPreferences.get(App.INSTANCE.getString(R.string.shared_file_name),
                    key, App.INSTANCE.getString(R.string.shared_default_string))

                localRepoAction.promiseReturn(value)
            }
            Constants.DEL_DATA_CODE -> {
                val key = array[1].asString()!!
                SharedPreferences.removeData(App.INSTANCE.getString(R.string.shared_file_name), key)
                localRepoAction.promiseReturn(true)
            }
        }
    }

    val webPopUp
            = FlexLambda.action { action, array ->
        val url = array[0].asString()!!
        val ratio = array[1].asDouble()
        popUpAction = action

        val screenSize = getScreenSize()
        val w = (ratio?.times(screenSize.getValue("width")))?.toInt()
        val h = (ratio?.times(screenSize.getValue("height")))?.toInt()

        if(Network.getStatus(App.activity) == Constants.NET_STAT_DISCONNECTED) {
            val returnObj = JSONObject()
            returnObj.put(App.INSTANCE.getString(R.string.obj_key_data), false)
            returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), "연결된 네트워크가 없습니다")
            popUpAction?.promiseReturn(returnObj)
        }

        CoroutineScope(Dispatchers.Main).launch {
            addBackgroundViewTo(App.activity.constraintLayout)
            addCloseButton(App.activity, App.activity.constraintLayout)
            showPopUpView(App.activity.flex_pop_up_web_view, url, w!!, h!!)
        }
    }

    /**======================================== Function =========================================*/

    /** 필요 권한들이 모두 있는지 확인
     *  하나라도 없으면 false
     */
    fun existAllPermission(permissions: Array<out String>): Boolean {
        for(permissionName in permissions) {
            if(!existsPermission(permissionName)) {
                return false
            }
        }
        return true
    }

    /** 권한 승인 여부 판별 */
    fun existsPermission(permissionName: String): Boolean
            = (ContextCompat.checkSelfPermission(App.INSTANCE, permissionName)
            == PackageManager.PERMISSION_GRANTED)

    /** 위험 권한이 없을 경우 */
    fun checkAbsentPerms(permissions: Array<out String>, requestCode: Int, action: FlexAction?) {
        // 거절한 권한이 하나라도 존재할 경우
        if (existsDenialPermission(permissions)) {
            LOGD("거절한 권한이 하나라도 존재함")
            val returnObj = createJSONObject(false,
                null, "설정 > 앱에서 필수 권한들을 승인해 주세요")
            action?.promiseReturn(returnObj)
        }
        // 승인이 필요한 권한들을 요청
        else {
            LOGD("거절한 권한 없음 권한을 요청함")
            val perms = getPermissionsToRequest(permissions)
            requestPermissions(perms, requestCode)
        }
    }

    /** 거절된 권한 존재 여부 판별
     * 거절된 권한이 하나라도 있을 경우 false
     */
    private fun existsDenialPermission(permissions: Array<out String>): Boolean {
        for(permissionName in permissions) {
            if(isDenialPermission(permissionName)) {
                return true

            }
        }
        return false
    }

    /** 필요 권한이 거절되었는지 확인 */
    private fun isDenialPermission(permissionName: String): Boolean =
        ActivityCompat.shouldShowRequestPermissionRationale(App.activity, permissionName)

    /** 요청이 필요한 권한들을 반환 */
    private fun getPermissionsToRequest(permissions: Array<out String>): Array<out String> {
        val perms = ArrayList<String>()

        for(permissionName in permissions) {
            if(!existsPermission(permissionName)) {
                perms.add(permissionName)
            }
        }

        return perms.toTypedArray()
    }

    /** 다수 권한 요청 */
    fun requestPermissions(permissions: Array<out String>, requestCode: Int) {
        LOGD("Call requestPermissions()")

        ActivityCompat.requestPermissions(App.activity, permissions, requestCode)
    }

    /** 암시적 인텐트를 받을 수 있는 앱이 있는지 확인 */
    fun existsReceiveActivity(intent: Intent, packageManager: PackageManager): Boolean
            = (intent.resolveActivity(packageManager) != null)

    /** url로부터 파일을 다운받음 */
    fun downloadFileFromUrl(url: String?) {
        val basicActivity = App.activity as BasicActivity
        val mimeTypeMap = MimeTypeMap.getSingleton()
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        LOGD("extension: $extension")
        val mimeType = mimeTypeMap.getMimeTypeFromExtension(extension)
        LOGD("MimeType: $mimeType")

        try {
            val downloadManager = (App.activity).getSystemService(
                AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(url))
            request.setMimeType(mimeType)
            request.setDescription("Downloading File...")
            request.setAllowedOverMetered(true)
            request.setAllowedOverRoaming(true)
            request.setTitle("file")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                request.setRequiresCharging(false)
            }
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                "file.$extension")

            downloadManager.enqueue(request)
            fileAction?.promiseReturn(true)
        } catch(e: Exception) {
            fileAction?.promiseReturn(false)
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
                LOGD(String.format("이 값을 SMS 뒤에 써서 보내주면 됩니다 : %s", hash))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            LOGE("Unable to find package to obtain hash. : $e")
        }

        return appCodes
    }

    /** Hash값 반환 */
    private fun getHash(packageName: String, signature: String): String? {
        LOGE("getHash")
        val appInfo = "$packageName $signature"

        try {
            val messageDigest: MessageDigest = MessageDigest.getInstance("SHA-256")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
            }
            var hashSignature: ByteArray = messageDigest.digest()

            hashSignature = hashSignature.copyOfRange(0, 9)

            var base64Hash = encode11DigitsBase64String(hashSignature)

            LOGD(String.format("\nPackage : %s\nHash : %s", packageName, base64Hash))

            return base64Hash
        } catch (e: NoSuchAlgorithmException) {
            LOGE("hash:NoSuchAlgorithm : $e")
        }

        return null
    }

    /** 11자리 base64로 인코딩 */
    private fun encode11DigitsBase64String(hashSignature: ByteArray): String {
        return Base64.encodeToString(hashSignature,
            Base64.NO_PADDING or Base64.NO_WRAP).substring(0, 11)
    }

    /** App id 가져오기 */
    fun getAppId(): String {
        val appId : String = SharedPreferences.get(App.INSTANCE.getString(R.string.shared_appid_file_name),
            App.INSTANCE.getString(R.string.shared_appid_key),
            App.INSTANCE.getString(R.string.shared_default_string))

        return if(appId.isNullOrEmpty()) {
            val uuid: String = UUID.randomUUID().toString()
            SharedPreferences.putData(App.INSTANCE.getString(R.string.shared_appid_file_name),
                App.INSTANCE.getString(R.string.shared_appid_key), uuid)

            uuid
        }
        else {
            appId
        }
    }

    /** 백그라운드 뷰 생성 */
    private fun addBackgroundViewTo(targetLayout: ConstraintLayout) {
        val basicActivity = App.activity as BasicActivity
        val mInflater = getLayoutInflater(App.activity)
        basicActivity.backgroundView = mInflater.inflate(R.layout.background_popup, null)
        targetLayout.addView(basicActivity.backgroundView)
    }

    /** 백그라운드 뷰 제거 */
    fun removeBackgroundViewFrom(targetLayout: ConstraintLayout) {
        val basicActivity = App.activity as BasicActivity
        targetLayout.removeView(basicActivity.backgroundView)
    }

    /** 닫기 버튼 생성 */
    private fun addCloseButton(context: Context, targetLayout: ConstraintLayout) {
        closeBtn = Button(context)
        val params = ConstraintLayout.LayoutParams(90, 90).apply {
            topToTop = targetLayout.id
            endToEnd = targetLayout.id
            startToStart = targetLayout.id
            topMargin = 10
        }

        closeBtn!!.text = "X"
        closeBtn!!.textSize = 12F
        closeBtn!!.background = ContextCompat.getDrawable(context, R.drawable.circle)
        closeBtn!!.textAlignment = View.TEXT_ALIGNMENT_CENTER
        closeBtn!!.layoutParams = params

        targetLayout.addView(closeBtn)

        closeBtn!!.setOnClickListener {
            removeBackgroundViewFrom(App.activity.constraintLayout)
            removeCloseButtonFrom(App.activity.constraintLayout)
            hidePopUpView(App.activity, App.activity.flex_pop_up_web_view)
        }
    }

    /** 닫기 버튼 제거 */
    fun removeCloseButtonFrom(targetLayout: ConstraintLayout) {
        targetLayout.removeView(closeBtn)
    }

    /** 화면 크기 정보 가져오기 */
    fun getScreenSize(): Map<String, Int> {
        val displayMetrics = DisplayMetrics()
        App.activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        return mapOf("width" to width, "height" to height)
    }

    /** 팝업창을 닫는다 */
    fun hidePopUpView(context: Context, popupView: View) {
        popupView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.close))
        popupView.visibility = View.GONE
    }

    /** 팝업창을 띄운다 */
    private fun showPopUpView(popUpView: FlexWebView, url: String, width: Int, height: Int) {
        App.activity.flex_pop_up_web_view.loadUrl(url)
        popUpView.visibility = View.VISIBLE
        popUpView.layoutParams = getParamsAlignCenterInConstraintLayout(width, height, R.id.constraintLayout)
        popUpView.startAnimation(AnimationUtils.loadAnimation(App.activity, R.anim.open))
        popUpView.bringToFront()

        App.activity.flex_pop_up_web_view.webViewClient = object: FlexWebViewClient() {
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?,
                                         error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                val returnObj = JSONObject()
                returnObj.put(App.INSTANCE.getString(R.string.obj_key_data), false)
                returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), "해당 URL을 불러올 수 없습니다")
                popUpAction?.promiseReturn(returnObj)
                popUpAction = null
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val returnObj = JSONObject()
                returnObj.put(App.INSTANCE.getString(R.string.obj_key_data), true)
                popUpAction?.promiseReturn(returnObj)
            }
        }
    }

    /** 디바이스 id 가져오기 */
    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    /** 현재 날짜와 시간 반환 */
    fun getCurrentDateAndTime(): String {
        val currentDateTime = Calendar.getInstance().time
        return SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA).format(currentDateTime)
    }

    /** Inflater 가져오기 */
    fun getLayoutInflater(context: Context): LayoutInflater {
        return context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    /** 카메라 촬영 시 앱 내부에 임시 파일 생성 */
    fun getOutputMediaFile(): File? {
        val mediaStorageDir = File(
            App.context().filesDir,
            "test"
        )

        // 디렉토리 생성
        mediaStorageDir.apply {
            if (!exists()) {
                if (!mkdirs()) {
                    return null
                }
            }
        }

//        val timeStamp = Utils.getCurrentDateAndTime()
        // 경로 : content://com.example.hybridapp.fileprovider/cameraTest/IMG_TEST.jpg
        return File("${mediaStorageDir.path}${File.separator}IMG_TEST.jpg")
    }

    /** ProgressBar를 보이게 함 */
    fun visibleProgressBar() {
        App.activity.progressBar.visibility = View.VISIBLE
    }

    /** ProgressBar를 숨김 */
    fun invisibleProgressBar() {
        App.activity.progressBar.visibility = View.GONE
    }

    /** jsonObject 내 key 파악해서 value 가져오기 */
    fun getJsonObjectValue(key: String, jsonObject: JSONObject) : String? {
        return if(jsonObject.has(key)) jsonObject.get(key).toString() else null
    }

    fun getParamsAlignCenterInConstraintLayout(
        width: Int, height: Int, root: Int): ConstraintLayout.LayoutParams {
        val params = ConstraintLayout.LayoutParams(width, height)
        params.topToTop = root
        params.bottomToBottom = root
        params.endToEnd = root
        params.startToStart = root

        return params
    }

    /**================================== JSONObject 관련 ========================================*/

    /** JSONObject 생성
     * Parameter:
     * Boolean, String
     */
    fun createJSONObject(authValue: Boolean?, dataValue: Any?, msgValue: String?): JSONObject {
        val obj = JSONObject()
        obj.put(App.INSTANCE.getString(R.string.obj_key_auth), authValue)
        obj.put(App.INSTANCE.getString(R.string.obj_key_data), createJsonOfDataValue(dataValue))
        obj.put(App.INSTANCE.getString(R.string.obj_key_msg), msgValue)

        return obj
    }

    /** 웹에서 실제로 사용하는 변수들 구분하는 함수 */
    private fun createJsonOfDataValue(dataValue: Any?) : Any? {
        return when(dataValue) {
            is String, Boolean -> dataValue
            is JSONObject -> dataValue
            is Array<*> -> dataValue
            is ArrayList<*> -> dataValue
            else -> null
        }
    }

    /** Function */
    fun LOGD(message: String) = Log.d(App.INSTANCE.getString(R.string.tag), message)

    fun LOGE(message: String) = Log.e(App.INSTANCE.getString(R.string.tag), message)
}