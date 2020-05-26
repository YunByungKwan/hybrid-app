package com.example.hybridapp

import android.Manifest
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
import android.nfc.NfcAdapter
import android.nfc.tech.NfcF
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.dvkyun.flexhybridand.FlexAction
import app.dvkyun.flexhybridand.FlexWebChromeClient
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.SMSReceiver
import com.example.hybridapp.util.Utils
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLDecoder

class MainActivity : AppCompatActivity() {

    private lateinit var utils: Utils

    private var cameraAction: FlexAction? = null
    private var singlePhotoAction: FlexAction? = null
    private var multiplePhotosAction: FlexAction? = null

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

        //
//        val hintRequest = HintRequest.Builder()
//            .setPhoneNumberIdentifierSupported(true)
//            .build()
//
//        val intent = Credentials.getClient(this).getHintPickerIntent(hintRequest)
//
//        startIntentSenderForResult(intent.intentSender, iRequestCodePhoneNumber, null, 0, 0, 0)


        smsRetrieverCall()
        //

        flex_web_view.setBaseUrl("file:///android_asset")
        flex_web_view.loadUrl("file:///android_asset/html/test.html")
        flex_web_view.addFlexInterface(FlexInterface())
        flex_web_view.webChromeClient = MyWebChromeClient(this)
        WebView.setWebContentsDebuggingEnabled(true)
        flex_web_view.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            Log.e(Constants.TAG_MAIN, url)
            try {
                val request = DownloadManager.Request(Uri.parse(url))
                val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

                val newContentDisposition = URLDecoder.decode(contentDisposition,"UTF-8"); //디코딩
                val FileName = newContentDisposition.replace("attachment; filename=", ""); //attachment; filename*=UTF-8''뒤에 파일명이있는데 파일명만 추출하기위해 앞에 attachment; filename*=UTF-8''제거

                val fileName = FileName //위에서 디코딩하고 앞에 내용을 자른 최종 파일명
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
                dm.enqueue(request)
                Toast.makeText(getApplicationContext(),"파일이 다운로드됩니다.", Toast.LENGTH_LONG).show()
            }
            catch (e: Exception) {

            }
        }
        /** Camera Interface */
        flex_web_view.setAction("Camera") { action, _ ->
            CoroutineScope(Dispatchers.Main).launch {
                cameraAction = action

                if(utils.hasPermissionFor(Constants.PERM_CAMERA)) {

                    requestCameraIntent()

                } else {
                    if(utils.isRejectPermission(Constants.PERM_CAMERA)) {
                        val title = "알림"
                        val message = "앱을 사용하기 위해 카메라 권한이 필요합니다."
                        val positiveButtonText = "설정"
                        val negativeButtonText = "취소"
                        val clickListener = DialogInterface.OnClickListener { _, _ ->
                            startActivity(utils.takeAppSettingsIntent())
                        }

                        utils.showDialog(title, message, positiveButtonText, negativeButtonText,
                            clickListener, null)

                        cameraAction = null
                    } else {
                        utils.requestPermissions(arrayOf(Constants.PERM_CAMERA),
                            Constants.REQUEST_PERMISSIONS_CAMERA)

                    }
                }
            }
        }
        /** Gallery Interface */
        flex_web_view.setAction("SinglePhoto") { action, _ ->
            CoroutineScope(Dispatchers.Main).launch {
                singlePhotoAction = action

                if(utils.hasPermissionFor(Constants.PERM_WRITE_EXTERNAL_STORAGE)
                    && utils.hasPermissionFor(Constants.PERM_READ_EXTERNAL_STORAGE)) {

                    requestSinglePhotoIntent()

                } else {
                    if(utils.isRejectPermission(Constants.PERM_WRITE_EXTERNAL_STORAGE)
                        || utils.isRejectPermission(Constants.PERM_READ_EXTERNAL_STORAGE)) {
                        val title = "알림"
                        val message = "앱을 사용하기 위해 저장소 읽기/쓰기 권한이 필요합니다."
                        val positiveButtonText = "설정"
                        val negativeButtonText = "취소"
                        val clickListener = DialogInterface.OnClickListener { _, _ ->
                            startActivity(utils.takeAppSettingsIntent())
                        }

                        utils.showDialog(title, message, positiveButtonText, negativeButtonText,
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
        flex_web_view.setAction("MultiplePhotos") { action, _ ->
            CoroutineScope(Dispatchers.Main).launch {
                multiplePhotosAction = action

                if(utils.hasPermissionFor(Constants.PERM_WRITE_EXTERNAL_STORAGE)
                    && utils.hasPermissionFor(Constants.PERM_READ_EXTERNAL_STORAGE)) {

                    requestMultiplePhotoIntent()

                } else {
                    if(utils.isRejectPermission(Constants.PERM_WRITE_EXTERNAL_STORAGE)
                        || utils.isRejectPermission(Constants.PERM_READ_EXTERNAL_STORAGE)) {
                        val title = "알림"
                        val message = "앱을 사용하기 위해 저장소 읽기/쓰기 권한이 필요합니다."
                        val positiveButtonText = "설정"
                        val negativeButtonText = "취소"
                        val clickListener = DialogInterface.OnClickListener { _, _ ->
                            startActivity(utils.takeAppSettingsIntent())
                        }

                        utils.showDialog(title, message, positiveButtonText, negativeButtonText,
                            clickListener, null)

                        utils.showDialog(title, message, positiveButtonText, negativeButtonText,
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
        /** Location Interface */
        flex_web_view.setAction("Location") { _action, _->

            val map: MutableMap<String, Double> = mutableMapOf()
            var action = _action

            CoroutineScope(Dispatchers.Main).launch {
                if(utils.hasPermissionFor(Manifest.permission.ACCESS_FINE_LOCATION)
                    && utils.hasPermissionFor(Manifest.permission.ACCESS_COARSE_LOCATION)) {

                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)

                    try {
                        fusedLocationClient!!.lastLocation!!.addOnSuccessListener(this@MainActivity) { location : Location? ->
                            if(location == null) {
                                Log.e("LOG", "location is null")
                            } else location.apply {
                                Log.e("LOG", "${this.latitude}, ${this.longitude}")

                                map["latitude"] = this.latitude
                                map["longitude"] = this.longitude

                                action?.promiseReturn(map)
                                action = null
                            }
                        }
                        fusedLocationClient!!.lastLocation!!.addOnCanceledListener { action?.promiseReturn(null); action = null }
                        fusedLocationClient!!.lastLocation!!.addOnCompleteListener { action?.promiseReturn(null); action = null }
                        fusedLocationClient!!.lastLocation!!.addOnFailureListener { action?.promiseReturn(null); action = null }
                    } catch (e: NullPointerException) {
                        // 오류 처리
                        action = null
                    }
                } else {
                    utils.checkPermissions(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION), Constants.REQUEST_PERMISSIONS_LOCATION)

                    action?.promiseReturn(null)
                    action = null
                }
            }
        }
    }

    private fun requestCameraIntent() {
        val cameraIntent = utils.takeCameraIntent()

        if(utils.existsReceiveActivity(cameraIntent, packageManager)) {
            startActivityForResult(cameraIntent, Constants.REQUEST_CAMERA_INTENT)
        } else {
            Log.e(Constants.TAG_MAIN, "Camera app can't be launched.")

            cameraAction = null
        }
    }

    private fun requestSinglePhotoIntent() {
        val singlePhotoIntent = utils.takeSinglePhotoIntent()

        if(utils.existsReceiveActivity(singlePhotoIntent, packageManager)) {
            startActivityForResult(singlePhotoIntent, Constants.REQUEST_SINGLE_PHOTO_INTENT)
        } else {
            Log.e(Constants.TAG_MAIN, "Gallery app can't be launched.")

            singlePhotoAction = null
        }
    }

    private fun requestMultiplePhotoIntent() {
        val multiplePhotosIntent = utils.takeMultiplePhotosIntent()

        if(utils.existsReceiveActivity(multiplePhotosIntent, packageManager)) {
            startActivityForResult(multiplePhotosIntent,
                Constants.REQUEST_MULTIPLE_PHOTOS_INTENT)
        } else {
            Log.e(Constants.TAG_MAIN, "Gallery app can't be launched.")

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

    ///
    private fun smsRetrieverCall() {
        Log.e(Constants.TAG_MAIN, "call smsRetrieverCall() in ${Constants.TAG_MAIN}")

        val client = SmsRetriever.getClient(this)

        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            Log.e(Constants.TAG_MAIN, "smsRetrieverCall Success")
        }

        task.addOnFailureListener {
            Log.e(Constants.TAG_MAIN, "smsRetrieverCall Fail")
        }
    }

    /** NFC
     * references : https://m.blog.naver.com/PostView.nhn?blogId=wndrlf2003&logNo=70186022536&proxyReferer=https:%2F%2Fwww.google.com%2F
     */
    private fun nfc() {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent
                = PendingIntent.getActivity(this, 0, intent, 0)
        val ndefIntent = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)

        try{
            ndefIntent.addDataType("*/*")
        } catch (e: java.lang.Exception) {
            Log.e(Constants.TAG_NFC, e.toString())
        }

        val intentFilters = arrayOf(ndefIntent)

        val mNFCTechLists =
            arrayOf(arrayOf<String>(NfcF::class.java.name))
    }

    /** Native->WebView로 파일 업로드를 위함 */
    inner class MyWebChromeClient(activity: Activity): FlexWebChromeClient(activity) {

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

    /** log.e wrapper function */
    private fun funLOGE(functionName: String) {
        Log.e(Constants.TAG_MAIN, "call $functionName() in ${Constants.TAG_MAIN}")
    }
}
