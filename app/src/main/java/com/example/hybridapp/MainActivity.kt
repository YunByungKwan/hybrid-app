package com.example.hybridapp

import android.app.Activity
import android.app.DownloadManager
import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import app.dvkyun.flexhybridand.FlexAction
import app.dvkyun.flexhybridand.FlexWebChromeClient
import app.dvkyun.flexhybridand.FlexWebViewClient
import com.example.hybridapp.util.*
import com.example.hybridapp.util.base.BaseActivity
import com.example.hybridapp.util.module.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.URLDecoder
import kotlin.collections.ArrayList

class MainActivity : BaseActivity() {

    private lateinit var utils: Utils

    /** FlexAction instances */
    private var dialogAction: FlexAction? = null
    private var recordAction: FlexAction? = null

    private val scope = CoroutineScope(Dispatchers.Default)

    /** Room Database */
    private lateinit var repository: LogUrlRepository

    private var smsReceiver: SMSReceiver? = null

    private var mFilePatCallback: ValueCallback<Array<Uri>>? = null

    var fusedLocationClient: FusedLocationProviderClient? = null



    override fun onResume() {
        funLOGE("onResume")
        super.onResume()

        if(smsReceiver == null) {
            Log.e(Constants.TAG_MAIN, "smsReceiver instance is created.")
            smsReceiver = SMSReceiver()
        } else {
            Log.e(Constants.TAG_MAIN, "smsReceiver instance is already created.")
        }

        val filter = IntentFilter()
        filter.addAction(smsReceiver!!.SMSRetrievedAction)
        registerReceiver(smsReceiver, filter)

        Log.e(Constants.TAG_MAIN, "Register SMSReceiver()")
    }

    override fun onPause() {
        funLOGE("onPause")

        if(smsReceiver != null) {
            unregisterReceiver(smsReceiver)
            Log.e(Constants.TAG_MAIN, "Unregister SMSReceiver()")
        } else {
            Log.e(Constants.TAG_MAIN, "smsReceiver is null in onPause().")
        }

        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        utils = Utils()

        /** Room Database default settings */
        scope.launch {
            val logUrlDao = LogUrlRoomDatabase.getDatabase(this@MainActivity).logUrlDao()
            repository = LogUrlRepository(logUrlDao)
        }

        /** FlexWebview default settings */
        flex_pop_up_web_view.setBaseUrl("file:///android_asset")
        flex_web_view.setBaseUrl("file:///android_asset")
        flex_web_view.loadUrl("file:///android_asset/html/test.html")
        flex_web_view.addFlexInterface(FlexInterface())
        flex_web_view.settings.setSupportMultipleWindows(true)

        /** Dialog interface */
        flex_web_view.setAction(getString(R.string.type_dialog)) { action, array ->
            scope.launch(Dispatchers.Main) {
                funLOGE(getString(R.string.type_dialog))

                dialogAction = action
                val title = array!!.getString(0)
                val contents = array.getString(1)

                val posButtonText = array.getString(2)
                val negButtonText = array.getString(3)
                val posListener = DialogInterface.OnClickListener { _, _ ->
                    dialogAction!!.promiseReturn(posButtonText)
                }
                val negListener = DialogInterface.OnClickListener { _, _ ->
                    dialogAction!!.promiseReturn(negButtonText)
                }
                val cancelListener = {
                    dialogAction!!.promiseReturn("cancel")
                }

                Dialog.show(title, contents, posButtonText, negButtonText,
                    posListener, negListener, cancelListener)
            }
        }
        /** Network Interface */
        flex_web_view.setAction(getString(R.string.type_network), Action.network)

        flex_web_view.setAction(getString(R.string.type_network_status)) { action, _ ->
            funLOGE(getString(R.string.type_network_status))

            CoroutineScope(Dispatchers.Main).launch {

                val status = Network.getStatus()
                if(status == 1) {
                    val cellularStatus = getString(R.string.network_cellular_text)
                    Toast.showShortText(cellularStatus)

                    action?.promiseReturn(1)
                } else if(status == 2) {
                    val wifiStatus = getString(R.string.network_wifi_text)
                    Toast.showShortText(wifiStatus)

                    action?.promiseReturn(2)
                } else {
                    val noStatus = getString(R.string.network_no_text)
                    Toast.showShortText(noStatus)

                    action?.promiseReturn(0)
                }
            }
        }
        /** Camera interface */
//        flex_web_view.setAction(getString(R.string.type_camera)) { action, _ ->
//            CoroutineScope(Dispatchers.Main).launch {
//                funLOGE(getString(R.string.type_camera))
//
//                cameraAction = action
//
//                if(utils.existAllPermission(arrayOf(Constants.PERM_CAMERA))) {
//                    Camera.request()
//                } else {
//                    utils.requestDangerousPermissions(arrayOf(Constants.PERM_CAMERA),
//                        Constants.REQ_PERM_CODE_CAMERA)
//                }
//            }
//        }
        flex_web_view.setAction(getString(R.string.type_camera), Action.camera)

        /** QrCode interface */
        flex_web_view.setAction(getString(R.string.type_qr_code_scan)) { action, _->
            funLOGE(getString(R.string.type_qr_code_scan))

            qrCodeScanAction = action

            QRCode.startScan()
        }
        /** Single photo interface */
        flex_web_view.setAction(getString(R.string.type_single_photo)) { action, _ ->
            CoroutineScope(Dispatchers.Main).launch {
                funLOGE(getString(R.string.type_single_photo))

                singlePhotoAction = action

                val galleyPermissions = arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                    Constants.PERM_READ_EXTERNAL_STORAGE)

                if(utils.existAllPermission(galleyPermissions)) {
                    Photo.requestSingle()
                } else {
                    utils.requestDangerousPermissions(galleyPermissions,
                        Constants.REQ_PERM_CODE_SINGLE_PHOTO)
                }
            }
        }
        /** Multiple photo interface */
        flex_web_view.setAction(getString(R.string.type_multi_photo)) { action, _ ->
            CoroutineScope(Dispatchers.Main).launch {
                funLOGE(getString(R.string.type_multi_photo))

                multiplePhotosAction = action

                val galleyPermissions = arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                    Constants.PERM_READ_EXTERNAL_STORAGE)

                if(utils.existAllPermission(galleyPermissions)) {
                    Photo.requestMultiple()
                } else {
                    utils.requestDangerousPermissions(galleyPermissions,
                        Constants.REQ_PERM_CODE_MULTIPLE_PHOTO)
                }
            }
        }
        /** File Upload */
        flex_web_view.webChromeClient = object : FlexWebChromeClient(this) {

            override fun onShowFileChooser(
                webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: WebChromeClient.FileChooserParams?): Boolean
            {
                funLOGE("onShowFileChooser")

                mFilePatCallback = filePathCallback

                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"

                startActivityForResult(intent, Constants.REQ_CODE_FILE_UPLOAD)

                return true
            }
        }
        /** 앱 접속 이력 관리 */
        flex_web_view.webViewClient = object : FlexWebViewClient() {
            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                funLOGE("doUpdateVisitedHistory")

                scope.launch {
                    val currentDateTime = utils.getCurrentDateAndTime()

                    if(url != null) {
                        Log.e(Constants.TAG_MAIN, "($currentDateTime)접속 URL: $url")
                        val logUrl = LogUrl(0, currentDateTime, url)
                        repository.insert(logUrl)
                    }
                }

                super.doUpdateVisitedHistory(view, url, isReload)
            }
        }
        /** File Download */
        WebView.setWebContentsDebuggingEnabled(true)
        flex_web_view.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            funLOGE("setDownloadListener")

            if(utils.existsPermission(Constants.PERM_WRITE_EXTERNAL_STORAGE)) {
                try {
                    val request = DownloadManager.Request(Uri.parse(url))
                    val downloadManager
                            = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

                    val decodedContentDisposition
                            = URLDecoder.decode(contentDisposition,"UTF-8")

                    val fileName = decodedContentDisposition
                        .replace("attachment; filename=", "")
                    request.setMimeType(mimetype)
                    request.addRequestHeader("User-Agent", userAgent)
                    request.setDescription("Downloading File")
                    request.setAllowedOverMetered(true)
                    request.setAllowedOverRoaming(true)
                    request.setTitle(fileName)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        request.setRequiresCharging(false)
                    }

                    request.allowScanningByMediaScanner()
                    request.setAllowedOverMetered(true)
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                    downloadManager.enqueue(request)

                    Toast.showLongText("파일이 다운로드됩니다.")
                }
                catch (e: Exception) {
                    Log.e(Constants.TAG_MAIN, e.toString())
                }
            } else {
                utils.requestPermissions(arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE), 1234)
            }
        }
        /** Location interface */
        flex_web_view.setAction(getString(R.string.type_location)) { locationAction, _->
            CoroutineScope(Dispatchers.Main).launch {
                funLOGE(getString(R.string.type_location))

                val currentLocation: MutableMap<String, Double> = mutableMapOf()

                val locationPermissions = arrayOf(Constants.PERM_ACCESS_FINE_LOCATION,
                    Constants.PERM_ACCESS_COARSE_LOCATION)

                if(utils.existAllPermission(locationPermissions)) {
                    fusedLocationClient= LocationServices
                        .getFusedLocationProviderClient(this@MainActivity)

                    try {
                        fusedLocationClient!!.lastLocation!!.addOnSuccessListener(this@MainActivity)
                        { location : Location? ->
                            if(location == null) {
                                Log.e("LOG", "location is null")
                            } else location.apply {
                                Log.e("LOG", "${this.latitude}, ${this.longitude}")

                                currentLocation["latitude"] = this.latitude
                                currentLocation["longitude"] = this.longitude

                                locationAction?.promiseReturn(currentLocation)
                            }
                        }
                        fusedLocationClient!!.lastLocation!!.addOnCanceledListener {
                            locationAction?.promiseReturn(null)
                        }
                        fusedLocationClient!!.lastLocation!!.addOnCompleteListener {
                            locationAction?.promiseReturn(null)
                        }
                        fusedLocationClient!!.lastLocation!!.addOnFailureListener {
                            locationAction?.promiseReturn(null)
                        }
                    } catch (e: NullPointerException) {
                        Log.e(Constants.TAG_MAIN, e.stackTrace.toString())
                    }
                } else {
                    utils.requestDangerousPermissions(locationPermissions,
                        Constants.REQ_PERM_CODE_LOCATION)
                }
            }
        }
        /** BioAuthentication Interface */
        flex_web_view.setAction(getString(R.string.type_bio_authentication)) { action, _->
            CoroutineScope(Dispatchers.Main).launch {
                funLOGE(getString(R.string.type_bio_authentication))

                bioAuthenticationAction = action

                BioAuth.showPrompt(this@MainActivity)

                bioAuthenticationAction!!.promiseReturn(null)
                bioAuthenticationAction = null
            }
        }
        /** Record interface */
        flex_web_view.setAction(getString(R.string.type_record)) { action, _->
            funLOGE(getString(R.string.type_record))

            recordAction = action

            val recordPermission = arrayOf(Constants.PERM_RECORD_AUDIO)

            if(utils.existAllPermission(recordPermission)) {
                Record.getIntent()
            } else {
                utils.requestDangerousPermissions(recordPermission, Constants.REQ_PERM_CODE_RECORD)
            }
        }
        /** Send sms interface */
        flex_web_view.setInterface(getString(R.string.type_send_sms)) {
            funLOGE(getString(R.string.type_send_sms))

            if(utils.existAllPermission(arrayOf(Constants.PERM_SEND_SMS))) {
                SMS.sendMessage("01040501485", "집에 갑시다.")
            } else {
                utils.requestDangerousPermissions(arrayOf(Constants.PERM_SEND_SMS),
                    Constants.REQ_PERM_CODE_RECORD)
            }
        }
        /** Receive sms interface */
        flex_web_view.setInterface(getString(R.string.type_receive_sms)) {
            funLOGE("type_receive_sms")

            SMS.receiveMessage()
        }
        /** Notification interface */
        flex_web_view.setInterface(getString(R.string.type_notification)) {
            funLOGE(getString(R.string.type_notification))

            val importance = NotificationManagerCompat.IMPORTANCE_DEFAULT
            val showBadge = false
            val channelName = "알림"
            val description = "App notification channel"
            Notification.createChannel(importance, showBadge, channelName, description)

            val channelId = "01040501485"
            val title = "알림 제목"
            val content = "알림 본문입니다."
            val intent = Intent(App.INSTANCE, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent
                    = PendingIntent.getActivity(App.INSTANCE, 0, intent, 0)

            Notification.create(channelId,
                R.drawable.ic_launcher_background, title, content, pendingIntent)
        }
        /** Load SharedPreferences Interface */
        flex_web_view.setAction(getString(R.string.type_load_shared_preferences)) { action, array ->
            funLOGE(getString(R.string.type_load_shared_preferences))

            val fileName = array!!.getString(0)
            val key = array.getString(1)

            val result = SharedPreferences.getString(fileName, key)

            action?.promiseReturn(result)
        }
        /** 접속 이력 모두 보기 */
        flex_web_view.setInterface(getString(R.string.type_url_log)) {
            funLOGE(getString(R.string.type_url_log))

            scope.launch {
                val logUrls = repository.allLogUrls
                for(i in logUrls) {
                    Log.e(Constants.TAG_MAIN, "${i.id}, ${i.visitingTime}, ${i.visitingUrl}")
                }
            }

            null
        }
        /** PopUp window Interface */
        flex_web_view.setInterface(getString(R.string.type_pop_up_window)) {
            funLOGE(getString(R.string.type_pop_up_window))

            CoroutineScope(Dispatchers.Main).launch {
                flex_pop_up_web_view.loadUrl("file:///android_asset/html/" + it!!.getString(0))
                flex_pop_up_web_view.visibility = View.VISIBLE

                val bottomUp = AnimationUtils.loadAnimation(this@MainActivity, R.anim.open)
                flex_pop_up_web_view.startAnimation(bottomUp)
            }

            null
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val granted
                = (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)

        when(requestCode) {
            Constants.REQ_PERM_CODE_CAMERA -> {
                if (granted) {
                    Log.e(Constants.TAG_MAIN, Constants.LOG_MSG_CAMERA_PERM_SUCCESS)

//                Camera.request()
                } else {
                    Dialog.showDenialPermissionText()
                }
            }
            Constants.REQ_PERM_CODE_SINGLE_PHOTO -> {
                if (granted) {
                    Log.e(Constants.TAG_MAIN, Constants.LOG_MSG_GALLERY_PERM_SUCCESS)

                    Photo.requestSingle()

                } else {
                    Dialog.showDenialPermissionText()
                }
            }
            Constants.REQ_PERM_CODE_MULTIPLE_PHOTO -> {
                if (granted) {
                    Log.e(Constants.TAG_MAIN, Constants.LOG_MSG_GALLERY_PERM_SUCCESS)

                    Photo.requestMultiple()

                } else {
                    Dialog.showDenialPermissionText()
                }
            }
            Constants.REQ_PERM_CODE_RECORD -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.e(Constants.TAG_MAIN, "Record permission is granted.")

                    Record.getIntent()

                } else {
                    Dialog.showDenialPermissionText()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val resultOk = (resultCode == Activity.RESULT_OK)

        when(requestCode) {
            Constants.REQ_CODE_CAMERA -> {
                if(resultOk) {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    val base64Image = Photo.convertBitmapToBase64(imageBitmap)

                    Log.e(Constants.TAG_MAIN, "base64: $base64Image")

                    cameraAction?.promiseReturn(base64Image)
                } else {
                    cameraAction?.promiseReturn(Constants.RESULT_CANCELED)
                }
            }
            Constants.REQ_CODE_SINGLE_PHOTO -> {
                if(resultOk) {
                    val imageUri = data?.data
                    if(imageUri != null) {
                        val base64Image = Photo.convertUriToBase64(imageUri)

                        Log.e(Constants.TAG_MAIN, "base64: $base64Image")

                        singlePhotoAction?.promiseReturn(base64Image)
                    } else {
                        singlePhotoAction?.promiseReturn(Constants.RESULT_CANCELED)
                    }
                } else {
                    singlePhotoAction?.promiseReturn(Constants.RESULT_CANCELED)
                }
            }
            Constants.REQ_CODE_MULTIPLE_PHOTO -> {
                if(resultOk) {
                    val base64Photos = ArrayList<String>()
                    val clipData = data?.clipData

                    if(clipData != null) {
                        if(clipData.itemCount in 1..9) {
                            for(idx in 0 until clipData.itemCount) {
                                val imageUri = clipData.getItemAt(idx).uri
                                val base64Image = Photo.convertUriToBase64(imageUri)

                                base64Photos.add(base64Image)
                            }
                            multiplePhotosAction?.promiseReturn(base64Photos.toArray())
                        } else {
                            Log.e(Constants.TAG_MAIN, "10장 이상의 사진을 첨부할 수 없습니다.")
                            multiplePhotosAction?.promiseReturn(Constants.RESULT_CANCELED)
                        }
                    } else {
                        Log.e(Constants.TAG_MAIN, "ClipData is null.")
                        multiplePhotosAction?.promiseReturn(Constants.RESULT_CANCELED)
                    }
                } else {
                    multiplePhotosAction?.promiseReturn(Constants.RESULT_CANCELED)
                }

            }
            Constants.REQ_CODE_QR -> {
                if(resultOk) {
                    val result: IntentResult? =
                        IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

                    if(result != null) {
                        qrCodeScanAction?.promiseReturn(result.contents)
                    } else {
                        Log.e(Constants.TAG_MAIN, "QR Code is null.")
                        qrCodeScanAction?.promiseReturn(Constants.RESULT_CANCELED)
                    }
                } else {
                    qrCodeScanAction?.promiseReturn(Constants.RESULT_CANCELED)
                }
            }
            Constants.REQ_CODE_FILE_UPLOAD -> {
                if(resultOk) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mFilePatCallback?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
                    }else{
                        // mFilePatCallback?.onReceiveValue(data.getData())
                    }
                    mFilePatCallback = null
                } else {

                }

            }
            Constants.REQ_CODE_RECORD -> {
                if(resultOk) {

                } else {

                }
            }
        }


//        if (resultCode == Activity.RESULT_OK) {
//            Log.e(Constants.TAG_MAIN, Constants.LOG_RESULT_OK)
//
//            when (requestCode) {
//                Constants.REQ_CODE_CAMERA -> {
//                    val imageBitmap = data?.extras?.get("data") as Bitmap
//                    val base64Image = Photo.convertBitmapToBase64(imageBitmap)
//
//                    Log.e(Constants.TAG_MAIN, "base64: $base64Image")
//
//                    cameraAction?.promiseReturn(base64Image)
//                }
//                Constants.REQ_CODE_SINGLE_PHOTO -> {
//                    val imageUri = data?.data
//                    if(imageUri != null) {
//                        val base64Image = Photo.convertUriToBase64(imageUri)
//
//                        Log.e(Constants.TAG_MAIN, "base64: $base64Image")
//
//                        singlePhotoAction?.promiseReturn(base64Image)
//                    }
//                }
//                Constants.REQ_CODE_MULTIPLE_PHOTO -> {
//                    val base64Photos = ArrayList<String>()
//                    val clipData = data?.clipData
//
//                    if(clipData != null) {
//                        if(clipData.itemCount in 1..9) {
//                            for(idx in 0 until clipData.itemCount) {
//                                val imageUri = clipData.getItemAt(idx).uri
//                                val base64Image = Photo.convertUriToBase64(imageUri)
//
//                                base64Photos.add(base64Image)
//                            }
//
//                            multiplePhotosAction!!.promiseReturn(base64Photos.toArray())
//                        } else {
//                            Log.e(Constants.TAG_MAIN, "10장 이상의 사진을 첨부할 수 없습니다.")
//                        }
//                    } else {
//                        Log.e(Constants.TAG_MAIN, "ClipData is null.")
//                    }
//                }
//                Constants.REQ_CODE_QR -> {
//                    val result: IntentResult? =
//                        IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
//
//                    if(result != null) {
//                        qrCodeScanAction!!.promiseReturn(result.contents)
//                    } else {
//                        Log.e(Constants.TAG_MAIN, "QR Code is null.")
//                    }
//                }
//                Constants.REQ_CODE_FILE_UPLOAD -> {
//                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        mFilePatCallback?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
//                    }else{
//                        // mFilePatCallback?.onReceiveValue(data.getData())
//                    }
//                    mFilePatCallback = null
//                }
//                Constants.REQ_CODE_RECORD -> {
//
//                }
//            }
//        }
    }

    /** log.e wrapper function */
    private fun funLOGE(functionName: String) {
        Log.e(Constants.TAG_MAIN, "call $functionName() in ${Constants.TAG_MAIN}")
    }

    override fun onBackPressed() {
        if(flex_pop_up_web_view.visibility == View.VISIBLE) {
            val upBottom = AnimationUtils.loadAnimation(this@MainActivity, R.anim.close)
            flex_pop_up_web_view.startAnimation(upBottom)

            flex_pop_up_web_view.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }
}
