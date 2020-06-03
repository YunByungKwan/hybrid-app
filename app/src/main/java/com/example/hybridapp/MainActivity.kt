package com.example.hybridapp

import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.constraintlayout.widget.ConstraintLayout
import app.dvkyun.flexhybridand.FlexWebChromeClient
import com.example.hybridapp.data.LogUrlRepository
import com.example.hybridapp.data.LogUrlRoomDatabase
import com.example.hybridapp.util.*
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.basic.BasicWebViewClient
import com.example.hybridapp.util.module.*
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLDecoder
import kotlin.collections.ArrayList

class MainActivity : BasicActivity() {

    private val scope = CoroutineScope(Dispatchers.Default)
    private lateinit var repository: LogUrlRepository
    private var smsReceiver: SMSReceiver? = null
    private var mFilePatCallback: ValueCallback<Array<Uri>>? = null

    override fun onResume() {
        super.onResume()

        if(smsReceiver == null) {
            smsReceiver = SMSReceiver()
        }

        SMS.registerReceiver(smsReceiver)
    }

    override fun onPause() {
        super.onPause()

        SMS.unregisterReceiver(smsReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** Room Database default settings */
        scope.launch {
            val logUrlDao = LogUrlRoomDatabase.getDatabase(this@MainActivity).logUrlDao()
            repository = LogUrlRepository(logUrlDao)
        }

        setFlexWebViewSettings()

        setInterface()
        setActions()

        /** File Download */
        flex_web_view.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            if(Utils.existsPermission(Constants.PERM_WRITE_EXTERNAL_STORAGE)) {
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
                Utils.requestPermissions(arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE), 1234)
            }
        }
        /** Load SharedPreferences Interface */
        flex_web_view.setAction(Constants.TYPE_LOAD_SHARED_PREFERENCES) { action, array ->

            val fileName = array!!.getString(0)
            val key = array.getString(1)

            val result = SharedPreferences.getString(fileName, key)

            action?.promiseReturn(result)
        }
    }

    private fun setFlexWebViewSettings() {
        flex_pop_up_web_view.setBaseUrl(Constants.BASE_URL)
        flex_web_view.setBaseUrl(Constants.BASE_URL)
        flex_web_view.loadUrl("file:///android_asset/demo/index.html")
        flex_web_view.addFlexInterface(FlexInterface())
        flex_web_view.settings.setSupportMultipleWindows(true)
        WebView.setWebContentsDebuggingEnabled(true)
        flex_web_view.webChromeClient = object: FlexWebChromeClient(this) {
            override fun onShowFileChooser(
                webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: WebChromeClient.FileChooserParams?): Boolean
            {
                mFilePatCallback = filePathCallback

                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"

                App.activity.startActivityForResult(intent, Constants.REQ_CODE_FILE_UPLOAD)
                return true
            }
        }
        flex_web_view.webViewClient = BasicWebViewClient
    }

    /** FlexWebView Interface 셋팅 */
    private fun setInterface() {
        flex_web_view.setInterface("WebPopup") {array ->
            CoroutineScope(Dispatchers.Main).launch {
                val isExternalUrl = true
                val url = array.getString(1)
                val width = array.getDouble(2)
                val height = array.getDouble(3)
                val displayMetrics = DisplayMetrics()
                App.activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

                val screenWidth = displayMetrics.widthPixels
                val screenHeight = displayMetrics.heightPixels
                val newWidth = (width * screenWidth).toInt()
                val newHeight = (height * screenHeight).toInt()

                flex_pop_up_web_view.loadUrl("https://www.naver.com")
                flex_pop_up_web_view.layoutParams =
                    ConstraintLayout.LayoutParams(newWidth, newHeight)
                flex_pop_up_web_view.visibility = View.VISIBLE

                val bottomUp = AnimationUtils.loadAnimation(App.INSTANCE, R.anim.open)
                flex_pop_up_web_view.startAnimation(bottomUp)
            }

            null
        }
    }

    /** FlexWebView Action 셋팅 */
    private fun setActions() {
        flex_web_view.setAction(Constants.TYPE_DIALOG, Action.dialog)
        flex_web_view.setAction(Constants.TYPE_NETWORK, Action.network)
        flex_web_view.setAction(Constants.TYPE_CAMERA_DEVICE_RATIO, Action.cameraDeviceRatio)
        flex_web_view.setAction(Constants.TYPE_CAMERA_RATIO, Action.cameraRatio)
        flex_web_view.setAction(Constants.TYPE_PHOTO_DEVICE_RATIO, Action.photoDeviceRatio)
        flex_web_view.setAction(Constants.TYPE_PHOTO_RATIO, Action.photoRatio)
        flex_web_view.setAction(Constants.TYPE_MULTI_PHOTO_DEVICE_RATIO, Action.multiPhotoDeviceRatio)
        flex_web_view.setAction(Constants.TYPE_MULTI_PHOTO_RATIO, Action.multiPhotoRatio)
        flex_web_view.setAction(Constants.TYPE_QR_CODE_SCAN, Action.qrcode)
        flex_web_view.setAction(Constants.TYPE_LOCATION, Action.location)
        flex_web_view.setAction(Constants.TYPE_BIO_AUTHENTICATION, Action.bioAuth)
        flex_web_view.setAction(Constants.TYPE_RECORD, Action.record)
        flex_web_view.setAction(Constants.TYPE_LOCAL_REPO, Action.localRepository)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val resultOk = (resultCode == Activity.RESULT_OK)

        when(requestCode) {
            Constants.REQ_CODE_CAMERA_DEVICE_RATIO -> {
                if(resultOk) {
                    val imageUri = data?.data
                    val ratio = data?.getDoubleExtra("ratio",1.0)
                    var isWidthRatio = data?.getBooleanExtra("isWidthRatio", true)

                    if(imageUri != null) {
                        val bitmapImage = Photo.convertUriToBitmap(imageUri)
                        val resizedBitmapImage = Photo.resizeBitmapByDeviceRatio(bitmapImage,
                            ratio!!, isWidthRatio!!)
                        val base64Image = Photo.convertBitmapToBase64(resizedBitmapImage)

                        cameraDeviceAction?.promiseReturn(base64Image)
                    } else {
                        cameraDeviceAction?.promiseReturn(Constants.RESULT_CANCELED)
                    }
                } else {
                    cameraDeviceAction?.promiseReturn(Constants.RESULT_CANCELED)
                }
            }
            Constants.REQ_CODE_CAMERA_RATIO -> {
                if(resultOk) {
                    val imageUri = data?.data
                    val ratio = data?.getDoubleExtra("ratio",1.0)

                    if(imageUri != null) {
                        val bitmapImage = Photo.convertUriToBitmap(imageUri)
                        val resizedBitmapImage = Photo.resizeBitmapByRatio(bitmapImage,
                            ratio!!)
                        val base64Image = Photo.convertBitmapToBase64(resizedBitmapImage)

                        cameraAction?.promiseReturn(base64Image)
                    } else {
                        cameraAction?.promiseReturn(Constants.RESULT_CANCELED)
                    }
                } else {
                    cameraAction?.promiseReturn(Constants.RESULT_CANCELED)
                }
            }
            Constants.REQ_CODE_PHOTO_DEVICE_RATIO -> {
                if(resultOk) {
                    val imageUri = data?.data
                    val ratio = data?.getDoubleExtra("ratio",1.0)
                    var isWidthRatio = data?.getBooleanExtra("isWidthRatio", true)

                    if(imageUri != null) {
                        val bitmapImage = Photo.convertUriToBitmap(imageUri)
                        val resizedBitmapImage = Photo.resizeBitmapByDeviceRatio(bitmapImage,
                            ratio!!, isWidthRatio!!)
                        val base64Image = Photo.convertBitmapToBase64(resizedBitmapImage)

                        photoDeviceAction?.promiseReturn(base64Image)
                    } else {
                        photoDeviceAction?.promiseReturn(Constants.RESULT_CANCELED)
                    }
                } else {
                    photoDeviceAction?.promiseReturn(Constants.RESULT_CANCELED)
                }
            }
            Constants.REQ_CODE_PHOTO_RATIO -> {
                if(resultOk) {
                    val imageUri = data?.data
                    val ratio = data?.getDoubleExtra("ratio",1.0)

                    if(imageUri != null) {
                        val bitmapImage = Photo.convertUriToBitmap(imageUri)
                        val resizedBitmapImage = Photo.resizeBitmapByRatio(bitmapImage, ratio!!)
                        val base64Image = Photo.convertBitmapToBase64(resizedBitmapImage)

                        photoAction?.promiseReturn(base64Image)
                    } else {
                        photoAction?.promiseReturn(Constants.RESULT_CANCELED)
                    }
                } else {
                    photoAction?.promiseReturn(Constants.RESULT_CANCELED)
                }
            }
            Constants.REQ_CODE_MULTI_PHOTO_DEVICE_RATIO -> {
                if(resultOk) {
                    val base64Photos = ArrayList<String>()
                    val clipData = data?.clipData

                    val ratio = data?.getDoubleExtra("ratio", 1.0)
                    val isWidthRatio = data?.getBooleanExtra("isWidthRatio", true)

                    if(clipData != null) {
                        if(clipData.itemCount in 1..9) {
                            for(idx in 0 until clipData.itemCount) {
                                val imageUri = clipData.getItemAt(idx).uri
                                val bitmapImage = Photo.convertUriToBitmap(imageUri)
                                val resizedBitmapImage = Photo.resizeBitmapByDeviceRatio(bitmapImage, ratio!!, isWidthRatio)
                                val base64Image = Photo.convertBitmapToBase64(resizedBitmapImage)

                                base64Photos.add(base64Image)
                            }
                            multiplePhotoDeviceAction?.promiseReturn(base64Photos.toArray())
                        } else {
                            Log.e(Constants.TAG_MAIN, "10장 이상의 사진을 첨부할 수 없습니다.")
                            multiplePhotoDeviceAction?.promiseReturn(Constants.RESULT_CANCELED)
                        }
                    } else {
                        Log.e(Constants.TAG_MAIN, "ClipData is null.")
                        multiplePhotoDeviceAction?.promiseReturn(Constants.RESULT_CANCELED)
                    }
                } else {
                    multiplePhotoDeviceAction?.promiseReturn(Constants.RESULT_CANCELED)
                }
            }
            Constants.REQ_CODE_MULTI_PHOTO_RATIO -> {
                if(resultOk) {
                    val base64Photos = ArrayList<String>()
                    val clipData = data?.clipData

                    val ratio = data?.getDoubleExtra("ratio", 1.0)

                    if(clipData != null) {
                        if(clipData.itemCount in 1..9) {
                            for(idx in 0 until clipData.itemCount) {
                                val imageUri = clipData.getItemAt(idx).uri
                                val bitmapImage = Photo.convertUriToBitmap(imageUri)
                                val resizedBitmapImage = Photo.resizeBitmapByRatio(bitmapImage, ratio!!)
                                val base64Image = Photo.convertBitmapToBase64(resizedBitmapImage)

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
                        Log.e(Constants.TAG_MAIN, result.contents)
                        qrCodeScanAction?.promiseReturn(result.contents)
                    } else {
                        Log.e(Constants.TAG_MAIN, "QR Code is null.")
                        qrCodeScanAction?.promiseReturn(Constants.RESULT_CANCELED)
                    }
                } else {
                    qrCodeScanAction?.promiseReturn(Constants.RESULT_CANCELED)
                }
            }
            Constants.REQ_PERM_CODE_SEND_SMS -> {
                if(resultOk) {

                } else {

                }
            }
            Constants.REQ_CODE_FILE_UPLOAD -> {
                if(resultOk) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mFilePatCallback?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
                    }else{
                    }
                    mFilePatCallback = null
                } else {

                }

            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val isNotEmpty = grantResults.isNotEmpty()
        val isGranted = (grantResults[0] == PackageManager.PERMISSION_GRANTED)

        when(requestCode) {
            Constants.REQ_PERM_CODE_CAMERA -> {
                if (isNotEmpty && isGranted) {
                    Log.e(Constants.TAG_MAIN, Constants.LOG_PERM_GRANTED_CAMERA)
                } else {
                    Dialog.showDenialPermissionText()
                }
            }
            Constants.REQ_PERM_CODE_WRITE -> {
                if(isNotEmpty && isGranted) {
                    Log.e(Constants.TAG_MAIN, Constants.LOG_PERM_GRANTED_WRITE)
                } else {
                    Dialog.showDenialPermissionText()
                }
            }
            Constants.REQ_PERM_CODE_READ_WRITE -> {
                if(isNotEmpty && isGranted) {
                    Log.e(Constants.TAG_MAIN, Constants.LOG_PERM_GRANTED_READ_WRITE)
                } else {
                    Dialog.showDenialPermissionText()
                }
            }
            Constants.REQ_PERM_CODE_LOCATION -> {
                if(isNotEmpty && isGranted) {
                    Log.e(Constants.TAG_MAIN, Constants.LOG_PERM_GRANTED_LOCATION)
                } else {
                    Dialog.showDenialPermissionText()
                }
            }
            Constants.REQ_PERM_CODE_RECORD_AUDIO -> {
                if(isNotEmpty && isGranted) {
                    Log.e(Constants.TAG_MAIN, Constants.LOG_PERM_GRANTED_RECORD_AUDIO)
                } else {
                    Dialog.showDenialPermissionText()
                }
            }
            Constants.REQ_PERM_CODE_SEND_SMS -> {
                if(isNotEmpty && isGranted) {
                    Log.e(Constants.TAG_MAIN, Constants.LOG_PERM_GRANTED_SEND_SMS)
                } else {
                    Dialog.showDenialPermissionText()
                }
            }
        }
    }

    /** 뒤로 가기 버튼 클릭 이벤트 */
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
