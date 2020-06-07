package com.example.hybridapp

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import com.example.hybridapp.data.LogUrlRepository
import com.example.hybridapp.data.LogUrlRoomDatabase
import com.example.hybridapp.util.*
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.basic.BasicWebChromeClient
import com.example.hybridapp.basic.BasicWebViewClient
import com.example.hybridapp.util.module.*
import android.location.Location
import com.google.android.gms.common.stats.ConnectionTracker
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
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
    var backgroundView: View? = null
    var popupCloseButton: Button? = null

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
        setWebViewDownloadListener()
    }

    private fun setFlexWebViewSettings() {
        flex_pop_up_web_view.setBaseUrl(Constants.BASE_URL)
        flex_web_view.setBaseUrl(Constants.BASE_URL)
        flex_web_view.loadUrl(Constants.URL)
        flex_web_view.addFlexInterface(FlexInterface())
        flex_web_view.settings.setSupportMultipleWindows(true)
        WebView.setWebContentsDebuggingEnabled(true)
        flex_web_view.webChromeClient = BasicWebChromeClient(this)
        flex_web_view.webViewClient = BasicWebViewClient()
    }

    /** FlexWebView Interface 셋팅 */
    private fun setInterface() {
        /** 팝업 */
        flex_web_view.setInterface(Constants.TYPE_POP_UP) {array ->
            CoroutineScope(Dispatchers.Main).launch {

                // 뒷배경 뷰 생성
                val mInflater = Utils.getLayoutInflater(this@MainActivity)
                backgroundView = mInflater.inflate(R.layout.background_popup, null)
                constraintLayout.addView(backgroundView)

                val url = array.getString(0)
                val ratio = array.getDouble(1)

                val screenSize = Utils.getScreenSize(this@MainActivity)
                val popupWidth = (ratio * screenSize.getValue(Constants.SCREEN_WIDTH)).toInt()
                val popupHeight = (ratio * screenSize.getValue(Constants.SCREEN_HEIGHT)).toInt()

                flex_pop_up_web_view.loadUrl(url)
                flex_pop_up_web_view.visibility = View.VISIBLE
                flex_pop_up_web_view.layoutParams = Utils.getParamsAlignCenterInConstraintLayout(
                    popupWidth, popupHeight, R.id.constraintLayout)

                val bottomUp = AnimationUtils.loadAnimation(this@MainActivity, R.anim.open)
                flex_pop_up_web_view.startAnimation(bottomUp)
                flex_pop_up_web_view.bringToFront()

                // 닫기 버튼 생성
                popupCloseButton = Utils.createCloseButton(this@MainActivity, R.id.constraintLayout)
                constraintLayout.addView(popupCloseButton)

                popupCloseButton!!.setOnClickListener {
                    val closeAnimation = AnimationUtils.loadAnimation(
                        this@MainActivity, R.anim.close)
                    flex_pop_up_web_view.startAnimation(closeAnimation)
                    flex_pop_up_web_view.visibility = View.GONE
                    constraintLayout.removeView(backgroundView)
                    constraintLayout.removeView(popupCloseButton)
                }
            }

            null
        }
    }

    /** FlexWebView Action 셋팅 */
    private fun setActions() {
        flex_web_view.setAction(Constants.TYPE_DIALOG, Action.dialog)
        flex_web_view.setAction(Constants.TYPE_NETWORK, Action.network)
        flex_web_view.setAction(Constants.TYPE_CAMERA_DEVICE_RATIO, Action.cameraByDeviceRatio)
        flex_web_view.setAction(Constants.TYPE_CAMERA_RATIO, Action.cameraByRatio)
        flex_web_view.setAction(Constants.TYPE_PHOTO_DEVICE_RATIO, Action.photoByDeviceRatio)
        flex_web_view.setAction(Constants.TYPE_PHOTO_RATIO, Action.photoByRatio)
        flex_web_view.setAction(Constants.TYPE_MULTI_PHOTO_DEVICE_RATIO, Action.multiPhotoByDeviceRatio)
        flex_web_view.setAction(Constants.TYPE_MULTI_PHOTO_RATIO, Action.multiPhotoByRatio)
        flex_web_view.setAction(Constants.TYPE_QR_CODE_SCAN, Action.qrcode)
        flex_web_view.setAction(Constants.TYPE_LOCATION, Action.location)
        flex_web_view.setAction(Constants.TYPE_BIO_AUTHENTICATION, Action.bioAuth)
        flex_web_view.setAction(Constants.TYPE_RECORD, Action.record)
        flex_web_view.setAction(Constants.TYPE_LOCAL_REPO, Action.localRepository)
    }

    private fun setWebViewDownloadListener() {
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val resultOk = (resultCode == Activity.RESULT_OK)

        when(requestCode) {
            Constants.REQ_CODE_CAMERA_DEVICE_RATIO -> {
                if(resultOk) {
                    val imageUri = data?.data

                    if(imageUri != null) {
                        val base64 = Constants.BASE64_URL +
                                Photo.convertUriToResizingBase64(imageUri, ratio, isWidthRatio)

                        cameraDeviceAction?.promiseReturn(base64)
                        ratio = null
                        isWidthRatio = null
                    } else {
                        cameraDeviceAction?.promiseReturn(null)
                    }
                } else {
                    cameraDeviceAction?.promiseReturn(null)
                }
            }
            Constants.REQ_CODE_CAMERA_RATIO -> {
                if(resultOk) {
                    val imageUri = data?.data

                    if(imageUri != null) {
                        val base64 = Constants.BASE64_URL +
                                Photo.convertUriToResizingBase64(imageUri, ratio, isWidthRatio)
                        cameraAction?.promiseReturn(base64)
                        ratio = null
                    } else {
                        cameraAction?.promiseReturn(null)
                    }
                } else {
                    cameraAction?.promiseReturn(null)
                }
            }
            Constants.REQ_CODE_PHOTO_DEVICE_RATIO -> {
                if(resultOk) {
                    val imageUri = data?.data

                    if(imageUri != null) {
                        val base64 = Constants.BASE64_URL +
                                Photo.convertUriToResizingBase64(imageUri, ratio, isWidthRatio)

                        photoDeviceAction?.promiseReturn(base64)
                        ratio = null
                        isWidthRatio = null
                    } else {
                        photoDeviceAction?.promiseReturn(null)
                    }
                } else {
                    photoDeviceAction?.promiseReturn(null)
                }
            }
            Constants.REQ_CODE_PHOTO_RATIO -> {
                if(resultOk) {
                    val imageUri = data?.data

                    if(imageUri != null) {
                        val base64 = Constants.BASE64_URL +
                                Photo.convertUriToResizingBase64(imageUri, ratio, isWidthRatio)

                        photoAction?.promiseReturn(base64)
                        ratio = null
                    } else {
                        photoAction?.promiseReturn(null)
                    }
                } else {
                    photoAction?.promiseReturn(null)
                }
            }
            Constants.REQ_CODE_MULTI_PHOTO_DEVICE_RATIO -> {
                if(resultOk) {
                    val base64Images = ArrayList<String>()
                    val clipData = data?.clipData

                    if(clipData != null) {
                        if(clipData.itemCount in 1..9) {
                            for(idx in 0 until clipData.itemCount) {
                                val imageUri = clipData.getItemAt(idx).uri
                                val base64 = Constants.BASE64_URL +
                                        Photo.convertUriToResizingBase64(imageUri, ratio, isWidthRatio)
                                base64Images.add(base64)
                            }
                            multiplePhotoDeviceAction?.promiseReturn(base64Images.toTypedArray())
                            ratio = null
                            isWidthRatio = null
                        } else {
                            Log.e(Constants.TAG_MAIN, "10장 이상의 사진을 첨부할 수 없습니다.")
                            multiplePhotoDeviceAction?.promiseReturn(null)
                        }
                    } else {
                        Log.e(Constants.TAG_MAIN, "ClipData is null.")
                        multiplePhotoDeviceAction?.promiseReturn(null)
                    }
                } else {
                    multiplePhotoDeviceAction?.promiseReturn(null)
                }
            }
            Constants.REQ_CODE_MULTI_PHOTO_RATIO -> {
                if(resultOk) {
                    val base64Images = ArrayList<String>()
                    val clipData = data?.clipData

                    if(clipData != null) {
                        if(clipData.itemCount in 1..9) {
                            for(idx in 0 until clipData.itemCount) {
                                val imageUri = clipData.getItemAt(idx).uri
                                val base64 = Constants.BASE64_URL +
                                        Photo.convertUriToResizingBase64(imageUri, ratio, isWidthRatio)
                                base64Images.add(base64)
                            }
                            multiplePhotosAction?.promiseReturn(base64Images.toTypedArray())
                            ratio = null
                        } else {
                            Log.e(Constants.TAG_MAIN, "10장 이상의 사진을 첨부할 수 없습니다.")
                            multiplePhotosAction?.promiseReturn(null)
                        }
                    } else {
                        Log.e(Constants.TAG_MAIN, "ClipData is null.")
                        multiplePhotosAction?.promiseReturn(null)
                    }
                } else {
                    multiplePhotosAction?.promiseReturn(null)
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
                        qrCodeScanAction?.promiseReturn(null)
                    }
                } else {
                    qrCodeScanAction?.promiseReturn(null)
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

        if(isNotEmpty && isGranted) {
            when(requestCode) {
                Constants.REQ_PERM_CODE_CAMERA -> {
                    Log.e(Constants.TAG_MAIN, Constants.LOG_PERM_GRANTED_CAMERA)
                }
                Constants.REQ_PERM_CODE_WRITE -> {
                    Log.e(Constants.TAG_MAIN, Constants.LOG_PERM_GRANTED_WRITE)
                }
                Constants.REQ_PERM_CODE_READ_WRITE -> {
                    Log.e(Constants.TAG_MAIN, Constants.LOG_PERM_GRANTED_READ_WRITE)
                }
                Constants.REQ_PERM_CODE_LOCATION -> {
                    Log.e(Constants.TAG_MAIN, Constants.LOG_PERM_GRANTED_LOCATION)
                }
                Constants.REQ_PERM_CODE_RECORD_AUDIO -> {
                    Log.e(Constants.TAG_MAIN, Constants.LOG_PERM_GRANTED_RECORD_AUDIO)
                }
                Constants.REQ_PERM_CODE_SEND_SMS -> {
                    Log.e(Constants.TAG_MAIN, Constants.LOG_PERM_GRANTED_SEND_SMS)
                }
            }
        } else {
            Dialog.showDenialPermissionText()
        }
    }

    /** 뒤로 가기 버튼 클릭 이벤트 */
    override fun onBackPressed() {
        if(flex_pop_up_web_view.visibility == View.VISIBLE) {
            val closeAnimation = AnimationUtils.loadAnimation(
                this@MainActivity, R.anim.close)
            flex_pop_up_web_view.startAnimation(closeAnimation)
            flex_pop_up_web_view.visibility = View.GONE

            constraintLayout.removeView(backgroundView)
            constraintLayout.removeView(popupCloseButton)
        } else {
            super.onBackPressed()
        }
    }
}
