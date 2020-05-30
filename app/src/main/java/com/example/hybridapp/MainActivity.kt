package com.example.hybridapp

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.dvkyun.flexhybridand.FlexAction
import app.dvkyun.flexhybridand.FlexWebChromeClient
import app.dvkyun.flexhybridand.FlexWebViewClient
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.sms.SMSReceiver
import com.example.hybridapp.util.Utils
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLDecoder
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var utils: Utils

    /** FlexAction instances */
    private var dialogAction: FlexAction? = null
    private var networkAction: FlexAction? = null
    private var networkStatusAction: FlexAction? = null
    private var permissionAction: FlexAction? = null
    private var cameraAction: FlexAction? = null
    private var qrCodeScanAction: FlexAction? = null
    private var singlePhotoAction: FlexAction? = null
    private var multiplePhotosAction: FlexAction? = null
    private var locationAction: FlexAction? = null
    private var bioAuthenticationAction: FlexAction? = null
    private var loadSharedPreferencesAction: FlexAction? = null

    private val scope = CoroutineScope(Dispatchers.Default)

    /** Room Database */
    private lateinit var repository: LogUrlRepository

    private val iRequestCodePhoneNumber = 100

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
        funLOGE("onCreate")

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
        flex_web_view.apply {
            settings.javaScriptEnabled = true
            settings.setSupportMultipleWindows(true)
            settings.javaScriptCanOpenWindowsAutomatically = true
        }
        /** Dialog Interface */
        flex_web_view.setAction(getString(R.string.type_dialog)) { action, array ->
            funLOGE(getString(R.string.type_dialog))

            scope.launch(Dispatchers.Main) {
                dialogAction = action
                val title = array!!.getString(0)
                val contents = array!!.getString(1)
                val posButtonText = array!!.getString(2)
                val negButtonText = array!!.getString(3)
                val posListener = DialogInterface.OnClickListener { _, _ ->
                    dialogAction!!.promiseReturn(posButtonText)
                    dialogAction = null
                }
                val negListener = DialogInterface.OnClickListener { _, _ ->
                    dialogAction!!.promiseReturn(negButtonText)
                    dialogAction = null
                }

                utils.showDialog(title, contents, posButtonText, negButtonText,
                    posListener, negListener)
            }
        }
        /** Network Interface */
        flex_web_view.setAction(getString(R.string.type_network)) { action, _ ->
            funLOGE(getString(R.string.type_network))

            CoroutineScope(Dispatchers.Main).launch {
                networkAction = action

                if(utils.isNetworkConnected()) {
                    val networkConnectedText = getString(R.string.network_connected_text)
                    utils.showShortToast(networkConnectedText)
                    networkAction!!.promiseReturn(networkConnectedText)
                    networkAction = null
                } else {
                    val networkDisConnectedText
                            = getString(R.string.network_disconnected_text)
                    utils.showShortToast(networkDisConnectedText)
                    networkAction!!.promiseReturn(networkDisConnectedText)
                    networkAction = null
                }
            }
        }
        flex_web_view.setAction(getString(R.string.type_network_status)) { action, _ ->
            funLOGE(getString(R.string.type_network_status))

            CoroutineScope(Dispatchers.Main).launch {
                networkStatusAction = action

                val status = utils.getNetworkStatus()
                if(status == 1) {
                    val cellularStatus = getString(R.string.network_cellular_text)
                    utils.showShortToast(cellularStatus)

                    networkStatusAction!!.promiseReturn(cellularStatus)
                    networkStatusAction = null
                } else if(status == 2) {
                    val wifiStatus = getString(R.string.network_wifi_text)
                    utils.showShortToast(wifiStatus)

                    networkStatusAction!!.promiseReturn(wifiStatus)
                    networkStatusAction = null
                } else {
                    val noStatus = getString(R.string.network_no_text)
                    utils.showShortToast(noStatus)

                    networkStatusAction!!.promiseReturn(noStatus)
                    networkStatusAction = null
                }
            }
        }
        /** Permission Interface */
        flex_web_view.setAction(getString(R.string.type_permission)) { action, _->
            funLOGE(getString(R.string.type_permission))

            permissionAction = action

            utils.requestPermissions(
                arrayOf(Constants.PERM_CAMERA, Constants.PERM_READ_EXTERNAL_STORAGE,
                Constants.PERM_WRITE_EXTERNAL_STORAGE, Constants.PERM_ACCESS_FINE_LOCATION,
                Constants.PERM_ACCESS_COARSE_LOCATION, Constants.PERM_ACCESS_BACKGROUND_LOCATION)
                , Constants.REQUEST_PERMISSIONS_ALL
            )
        }
        /** Camera Interface */
        flex_web_view.setAction(getString(R.string.type_camera)) { action, _ ->
            funLOGE(getString(R.string.type_camera))

            CoroutineScope(Dispatchers.Main).launch {
                cameraAction = action

                if(utils.hasPermissionFor(Constants.PERM_CAMERA)) {

                    requestCameraIntent()

                } else {
                    if(utils.isRejectPermission(Constants.PERM_CAMERA)) {

                        val clickListener = DialogInterface.OnClickListener { _, _ ->
                            startActivity(utils.takeAppSettingsIntent())
                        }

                        utils.showDialog(getString(R.string.dialog_title_text),
                            getString(R.string.dialog_camera_text),
                            getString(R.string.dialog_positive_btn_text),
                            getString(R.string.dialog_negative_btn_text),
                            clickListener, null)

                        cameraAction = null
                    } else {
                        utils.requestPermissions(arrayOf(Constants.PERM_CAMERA),
                            Constants.REQUEST_PERMISSIONS_CAMERA)
                    }
                }
            }
        }
        /** QrCode Interface */
        flex_web_view.setAction(getString(R.string.type_qr_code_scan)) { action, _->
            funLOGE(getString(R.string.type_qr_code_scan))

            qrCodeScanAction = action

            utils.takeQRCodeReader()
        }
        /** Gallery Interface */
        flex_web_view.setAction(getString(R.string.type_single_photo)) { action, _ ->
            funLOGE(getString(R.string.type_single_photo))

            CoroutineScope(Dispatchers.Main).launch {
                singlePhotoAction = action

                if(utils.hasPermissionFor(Constants.PERM_WRITE_EXTERNAL_STORAGE)
                    && utils.hasPermissionFor(Constants.PERM_READ_EXTERNAL_STORAGE)) {

                    requestSinglePhotoIntent()

                } else {
                    if(utils.isRejectPermission(Constants.PERM_WRITE_EXTERNAL_STORAGE)
                        || utils.isRejectPermission(Constants.PERM_READ_EXTERNAL_STORAGE)) {

                        val clickListener = DialogInterface.OnClickListener { _, _ ->
                            startActivity(utils.takeAppSettingsIntent())
                        }

                        utils.showDialog(getString(R.string.dialog_title_text),
                            getString(R.string.dialog_galley_text),
                            getString(R.string.dialog_positive_btn_text),
                            getString(R.string.dialog_negative_btn_text),
                            clickListener, null)

                        singlePhotoAction = null
                    } else {
                        utils.requestPermissions(
                            arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                                Constants.PERM_READ_EXTERNAL_STORAGE),
                            Constants.REQUEST_SINGLE_PHOTO_INTENT)
                    }
                }
            }
        }
        flex_web_view.setAction(getString(R.string.type_multi_photo)) { action, _ ->
            funLOGE(getString(R.string.type_multi_photo))

            CoroutineScope(Dispatchers.Main).launch {
                multiplePhotosAction = action

                if(utils.hasPermissionFor(Constants.PERM_WRITE_EXTERNAL_STORAGE)
                    && utils.hasPermissionFor(Constants.PERM_READ_EXTERNAL_STORAGE)) {

                    requestMultiplePhotoIntent()

                } else {
                    if(utils.isRejectPermission(Constants.PERM_WRITE_EXTERNAL_STORAGE)
                        || utils.isRejectPermission(Constants.PERM_READ_EXTERNAL_STORAGE)) {

                        val clickListener = DialogInterface.OnClickListener { _, _ ->
                            startActivity(utils.takeAppSettingsIntent())
                        }

                        utils.showDialog(getString(R.string.dialog_title_text),
                            getString(R.string.dialog_galley_text),
                            getString(R.string.dialog_positive_btn_text),
                            getString(R.string.dialog_negative_btn_text),
                            clickListener, null)

                        multiplePhotosAction = null
                    } else {
                        utils.requestPermissions(
                            arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                                Constants.PERM_READ_EXTERNAL_STORAGE),
                            Constants.REQUEST_MULTIPLE_PHOTOS_INTENT)
                    }
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

                startActivityForResult(intent, Constants.REQUEST_FILE_UPLOAD)

                return true
            }
        }
        /** 앱 접속 이력 관리 */
        flex_web_view.webViewClient = object : FlexWebViewClient() {
            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                funLOGE("doUpdateVisitedHistory")

                scope.launch {
                    val currentDateTime = utils.getCurrentTime()

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

            if(utils.hasPermissionFor(Constants.PERM_WRITE_EXTERNAL_STORAGE)) {
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
                    Toast.makeText(applicationContext,"파일이 다운로드됩니다.", Toast.LENGTH_LONG).show()
                }
                catch (e: Exception) {
                    Log.e(Constants.TAG_MAIN, e.toString())
                }
            } else {
                utils.requestPermissions(arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE), 1234)
            }
        }
        /** Location Interface */
        flex_web_view.setAction(getString(R.string.type_location)) { action, _->
            funLOGE(getString(R.string.type_location))

            locationAction = action
            val currentLocation: MutableMap<String, Double> = mutableMapOf()

            CoroutineScope(Dispatchers.Main).launch {
                if(utils.hasPermissionFor(Manifest.permission.ACCESS_FINE_LOCATION)
                    && utils.hasPermissionFor(Manifest.permission.ACCESS_COARSE_LOCATION)) {

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
                                locationAction = null
                            }
                        }
                        fusedLocationClient!!.lastLocation!!.addOnCanceledListener {
                            locationAction?.promiseReturn(null);
                            locationAction = null
                        }
                        fusedLocationClient!!.lastLocation!!.addOnCompleteListener {
                            locationAction?.promiseReturn(null);
                            locationAction = null
                        }
                        fusedLocationClient!!.lastLocation!!.addOnFailureListener {
                            locationAction?.promiseReturn(null);
                            locationAction = null
                        }
                    } catch (e: NullPointerException) {
                        // 오류 처리
                        locationAction = null
                    }
                } else {
                    utils.checkPermissions(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION), Constants.REQUEST_PERMISSIONS_LOCATION)

                    locationAction?.promiseReturn(null)
                    locationAction = null
                }
            }
        }
        /** BioAuthentication Interface */
        flex_web_view.setAction(getString(R.string.type_bio_authentication))
        { action, _->
            funLOGE(getString(R.string.type_bio_authentication))

            CoroutineScope(Dispatchers.Main).launch {
                bioAuthenticationAction = action

                utils.showBiometricPrompt(this@MainActivity)

                bioAuthenticationAction!!.promiseReturn(null)
                bioAuthenticationAction = null
            }
        }
        /** SMS Interface */
        flex_web_view.setInterface(getString(R.string.type_send_sms)) {
            funLOGE(getString(R.string.type_send_sms))

            if(utils.hasPermissionFor(Constants.PERM_SEND_SMS)) {
                utils.sendSMS("01040501485", "집에 갑시다.")
            } else {
                if(utils.isRejectPermission(Constants.PERM_SEND_SMS)) {

                    val clickListener = DialogInterface.OnClickListener { _, _ ->
                        startActivity(utils.takeAppSettingsIntent())
                    }

                    utils.showDialog(getString(R.string.dialog_title_text),
                        getString(R.string.dialog_sms_text),
                        getString(R.string.dialog_positive_btn_text),
                        getString(R.string.dialog_negative_btn_text),
                        clickListener, null)

                    cameraAction = null
                } else {
                    utils.requestPermissions(arrayOf(Constants.PERM_SEND_SMS),
                        Constants.REQUEST_PERMISSIONS_SMS)
                }
            }
        }
        flex_web_view.setInterface(getString(R.string.type_receive_sms)) {
            funLOGE("type_receive_sms")

            utils.createSmsRetriever()
        }
        /** Load SharedPreferences Interface */
        flex_web_view.setAction(getString(R.string.type_load_shared_preferences))
        { action, array ->
            funLOGE(getString(R.string.type_load_shared_preferences))

            loadSharedPreferencesAction = action

            val fileName = array!!.getString(0)
            val key = array.getString(1)

            val result = utils.getStringFromPreferences(fileName, key)

            loadSharedPreferencesAction!!.promiseReturn(result)
            loadSharedPreferencesAction = null
        }
        /** 접속 이력 모두 보기 */
        flex_web_view.setInterface(getString(R.string.type_url_log)) {
            funLOGE(getString(R.string.type_url_log))

            // utils.loadAllUrlLog()
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

    private fun requestCameraIntent() {
        funLOGE("requestCameraIntent")

        val cameraIntent = utils.takeCameraIntent()

        if(utils.existsReceiveActivity(cameraIntent, packageManager)) {
            startActivityForResult(cameraIntent, Constants.REQUEST_CAMERA_INTENT)
        } else {
            Log.e(Constants.TAG_MAIN, getString(R.string.log_camera_intent_error))

            cameraAction = null
        }
    }

    private fun requestSinglePhotoIntent() {
        funLOGE("requestSinglePhotoIntent")

        val singlePhotoIntent = utils.takeSinglePhotoIntent()

        if(utils.existsReceiveActivity(singlePhotoIntent, packageManager)) {
            startActivityForResult(singlePhotoIntent, Constants.REQUEST_SINGLE_PHOTO_INTENT)
        } else {
            Log.e(Constants.TAG_MAIN, getString(R.string.log_gallery_intent_error))

            singlePhotoAction = null
        }
    }

    private fun requestMultiplePhotoIntent() {
        funLOGE("requestMultiplePhotoIntent")

        val multiplePhotosIntent = utils.takeMultiplePhotosIntent()

        if(utils.existsReceiveActivity(multiplePhotosIntent, packageManager)) {
            startActivityForResult(multiplePhotosIntent,
                Constants.REQUEST_MULTIPLE_PHOTOS_INTENT)
        } else {
            Log.e(Constants.TAG_MAIN, getString(R.string.log_gallery_intent_error))

            multiplePhotosAction = null
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == Constants.REQUEST_PERMISSIONS_CAMERA) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.e(Constants.TAG_MAIN, "Camera permission is granted.")

                requestCameraIntent()

            } else {
                utils.showPermissionDeniedDialog()
            }
        } else if(requestCode == Constants.REQUEST_SINGLE_PHOTO_INTENT) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.e(Constants.TAG_MAIN, "READ/WRITE Storage permission is granted.")

                requestSinglePhotoIntent()

            } else {
                utils.showPermissionDeniedDialog()
            }
        } else if(requestCode == Constants.REQUEST_MULTIPLE_PHOTOS_INTENT) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.e(Constants.TAG_MAIN, "READ/WRITE Storage permission is granted.")

                requestMultiplePhotoIntent()

            } else {
                utils.showPermissionDeniedDialog()

                when {
                    cameraAction != null -> {
                        cameraAction!!.promiseReturn("Cancel")
                    }
                    singlePhotoAction != null -> {
                        singlePhotoAction!!.promiseReturn("Cancel")
                    }
                    multiplePhotosAction != null -> {
                        multiplePhotosAction!!.promiseReturn("Cancel")
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            Log.e(Constants.TAG_MAIN,
                "resultCode is Activity.RESULT_OK in ${Constants.TAG_MAIN}")

            when (requestCode) {
                Constants.REQUEST_CAMERA_INTENT -> {
                    Log.e(Constants.TAG_MAIN, "REQUEST_CAMERA_INTENT in onActivityResult()")

                    if(data != null) {
                        val imageBitmap = data.extras!!.get("data") as Bitmap
                        val base64Image = utils.convertBitmapToBase64(imageBitmap)

                        Log.e(Constants.TAG_MAIN, "base64: $base64Image")

                        cameraAction?.promiseReturn(base64Image)
                        cameraAction = null
                    }else {
                        Log.e(Constants.TAG_MAIN, "Data is null.")

                        cameraAction = null
                    }
                }
                Constants.REQUEST_SINGLE_PHOTO_INTENT -> {
                    Log.e(Constants.TAG_MAIN, "REQUEST_GALLERY_INTENT in onActivityResult()")

                    val imageUri = data?.data
                    if(imageUri != null) {
                        val base64Image = utils.convertUriToBase64(imageUri)

                        Log.e(Constants.TAG_MAIN, "base64: $base64Image")

                        singlePhotoAction?.promiseReturn(base64Image)
                        singlePhotoAction = null
                    }else {
                        Log.e(Constants.TAG_MAIN, "imageUri is null.")
                    }
                }
                Constants.REQUEST_MULTIPLE_PHOTOS_INTENT -> {
                    Log.e(Constants.TAG_MAIN,
                        "REQUEST_MULTIPLE_PHOTOS_INTENT in onActivityResult()")

                    val base64Photos = ArrayList<String>()
                    val clipData = data?.clipData

                    if(clipData != null) {
                        if(clipData.itemCount in 1..9) {
                            for(idx in 0 until clipData.itemCount) {
                                val imageUri = clipData.getItemAt(idx).uri
                                val base64Image = utils.convertUriToBase64(imageUri)

                                base64Photos.add(base64Image)
                            }

                            multiplePhotosAction!!.promiseReturn(base64Photos.toArray())
                            multiplePhotosAction = null
                        } else {
                            Log.e(Constants.TAG_MAIN, "10장 이상의 사진을 첨부할 수 없습니다.")
                        }
                    } else {
                        Log.e(Constants.TAG_MAIN, "ClipData is null.")
                    }
                }
                iRequestCodePhoneNumber -> {
                    val credential: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)
                    val phoneNumber = credential?.id

                    Toast.makeText(this, phoneNumber, Toast.LENGTH_SHORT).show()

                    //getAppSignatures(this)

                    Log.e("d", utils.getAppSignatures(this).toString())
                }
                IntentIntegrator.REQUEST_CODE -> {
                    Log.e(Constants.TAG_MAIN,
                        "IntentIntegrator.REQUEST_CODE in onActivityResult()")

                    val result: IntentResult? =
                        IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

                    if(result != null) {
                        qrCodeScanAction!!.promiseReturn(result.contents)
                        qrCodeScanAction = null
                    } else {
                        Log.e(Constants.TAG_MAIN, "QR Code is null.")

                        qrCodeScanAction = null
                    }
                }
                Constants.REQUEST_FILE_UPLOAD -> {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mFilePatCallback?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
                    }else{
                        // mFilePatCallback?.onReceiveValue(data.getData())
                    }
                    mFilePatCallback = null
                }
            }
        }
        else if(resultCode == Activity.RESULT_CANCELED) {
            Log.e(Constants.TAG_MAIN, "Activity.RESULT_CANCELED in onActivityResult()")
            singlePhotoAction?.promiseReturn("Cancel")
        } else {
            Log.e(Constants.TAG_MAIN, "Activity.RESULT_FAIL in onActivityResult()")
        }
    }

    /** Native->WebView로 파일 업로드를 위함 */

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
