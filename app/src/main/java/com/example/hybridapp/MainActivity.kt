package com.example.hybridapp

import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Button
import app.dvkyun.flexhybridand.FlexFuncInterface
import com.example.hybridapp.data.LogUrlRepository
import com.example.hybridapp.data.LogUrlRoomDatabase
import com.example.hybridapp.util.*
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.basic.BasicWebChromeClient
import com.example.hybridapp.basic.BasicWebViewClient
import com.example.hybridapp.util.module.*
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.URLDecoder
import kotlin.collections.ArrayList

class MainActivity : BasicActivity() {

    private lateinit var repository: LogUrlRepository
    private var smsReceiver: SMSReceiver? = null
    private lateinit var backgroundView: View
    private lateinit var popupCloseButton: Button

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

        init()
    }

    /** 시작 시 기본 초기화 함수 */
    private fun init() {
        /** Room Database default settings */
        CoroutineScope(Dispatchers.Default).launch {
            val logUrlDao = LogUrlRoomDatabase.getDatabase(this@MainActivity).logUrlDao()
            repository = LogUrlRepository(logUrlDao)
        }

        setFlexWebView()
        setActions()
        setWebViewDownloadListener()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

    }

    /** 기본, 팝업 FlexView 설정 */
    private fun setFlexWebView() {
        flex_pop_up_web_view.setBaseUrl(Constants.BASE_URL)
        flex_web_view.setBaseUrl(Constants.BASE_URL)
        flex_web_view.loadUrl(Constants.URL)
        flex_web_view.settings.setSupportMultipleWindows(true)
        WebView.setWebContentsDebuggingEnabled(true)
        flex_web_view.webChromeClient = BasicWebChromeClient(this)
        flex_web_view.webViewClient = BasicWebViewClient()
        flex_web_view.addFlexInterface(FlexActionInterface())
        flex_web_view.addFlexInterface(FlexPopupInterface())
    }

    /** FlexWebView Action 설정 */
    private fun setActions() {
        flex_web_view.setAction(Constants.TYPE_DIALOG, Action.dialog)
        flex_web_view.setAction(Constants.TYPE_NETWORK, Action.network)
        flex_web_view.setAction(Constants.TYPE_CAMERA_DEVICE_RATIO, Action.cameraByDeviceRatio)
        flex_web_view.setAction(Constants.TYPE_CAMERA_RATIO, Action.cameraByRatio)
        flex_web_view.setAction(Constants.TYPE_PHOTO_DEVICE_RATIO, Action.photoByDeviceRatio)
        flex_web_view.setAction(Constants.TYPE_PHOTO_RATIO, Action.photoByRatio)
        flex_web_view.setAction(Constants.TYPE_MULTI_PHOTO_DEVICE_RATIO, Action.multiPhotoByDeviceRatio)
        flex_web_view.setAction(Constants.TYPE_MULTI_PHOTO_RATIO, Action.multiPhotoByRatio)
        flex_web_view.setAction(Constants.TYPE_QR_CODE_SCAN, Action.qrCode)
        flex_web_view.setAction(Constants.TYPE_LOCATION, Action.location)
        flex_web_view.setAction(Constants.TYPE_BIO_AUTHENTICATION, Action.bioAuth)
        flex_web_view.setAction(Constants.TYPE_LOCAL_REPO, Action.localRepository)
    }

    private fun setWebViewDownloadListener() {
        /** File Download */
        flex_web_view.setDownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
            if(Utils.existsPermission(Constants.PERM_WRITE_EXTERNAL_STORAGE)) {
                try {
                    val request = DownloadManager.Request(Uri.parse(url))
                    val downloadManager
                            = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

                    val decodedContentDisposition
                            = URLDecoder.decode(contentDisposition,"UTF-8")

                    val fileName = decodedContentDisposition
                        .replace("attachment; filename=", "")

                    request.setMimeType(mimeType)
                    request.addRequestHeader("User-Agent", userAgent)
                    request.setDescription("Downloading File")
                    request.setAllowedOverMetered(true)
                    request.setAllowedOverRoaming(true)
                    request.setTitle(fileName)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        request.setRequiresCharging(false)
                    }
                    request.allowScanningByMediaScanner()
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
        val resultOk = (resultCode == Activity.RESULT_OK)
        when(requestCode) {
            Constants.QR_REQ_CODE -> {
                Constants.LOGD("QR CODE in onActivityResult()")

                val result: IntentResult? =
                    IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

                // QR Code 성공
                if(result != null) {
                    // QR Code 값이 있는 경우
                    if(result.contents != null) {
                        Constants.LOGD("IntentIntegrator Result: ${result.contents}")
                        val returnObj = Utils.createJSONObject(
                            true, result.contents)
                        qrCodeScanAction?.promiseReturn(returnObj)
                    }
                    // QR Code 값이 없는 경우
                    else {
                        Constants.LOGE("QR Code result is null")
                        val returnObj = Utils.createJSONObject(
                            true, Constants.MSG_NO_QR)
                        qrCodeScanAction?.promiseReturn(returnObj)
                    }
                }
                // QR Code 실패
                else {
                    Constants.LOGE("QR CODE RESULT_CANCELED")
                    val returnObj = Utils.createJSONObject(
                        true, Constants.MSG_NOT_LOAD_QR)
                    qrCodeScanAction?.promiseReturn(returnObj)
                }
            }
            Constants.CAMERA_DEVICE_RATIO_REQ_CODE -> {
                Constants.LOGD("CAMERA DEVICE RATIO in onActivityResult()")

                // 카메라 촬영 성공
                if(resultOk) {
                    if(data != null) {
                        val base64 = Constants.BASE64_URL +
                            Photo.convertUriToResizingBase64(data.data, ratio, isWidthRatio)

                        val returnObj = Utils.createJSONObject(true, base64)
                        cameraDeviceAction?.promiseReturn(returnObj)
                        ratio = null
                        isWidthRatio = null
                    } else {
                        Constants.LOGE("사진이 존재하지 않습니다")
                        val returnObj = Utils.createJSONObject(
                            true, "사진이 존재하지 않습니다")
                        cameraDeviceAction?.promiseReturn(returnObj)
                        ratio = null
                        isWidthRatio = null
                    }
                }
                // 카메라 촬영 실패
                else {
                    Constants.LOGE("취소되었습니다")
                    val returnObj = Utils.createJSONObject(
                        true, "취소되었습니다")
                    cameraDeviceAction?.promiseReturn(returnObj)
                    ratio = null
                    isWidthRatio = null
                }
            }
            Constants.CAMERA_RATIO_REQ_CODE -> {
                Constants.LOGD("CAMERA DEVICE RATIO in onActivityResult()")

                // 카메라 촬영 성공
                if(resultOk) {
                    if(data != null) {
                        val base64 = Constants.BASE64_URL +
                                Photo.convertUriToResizingBase64(data.data, ratio, isWidthRatio)

                        val returnObj = Utils.createJSONObject(true, base64)
                        cameraAction?.promiseReturn(returnObj)
                        ratio = null
                    } else {
                        Constants.LOGE("사진이 존재하지 않습니다")
                        val returnObj = Utils.createJSONObject(
                            true, "사진이 존재하지 않습니다")
                        cameraAction?.promiseReturn(returnObj)
                        ratio = null
                    }
                }
                // 카메라 촬영 실패
                else {
                    Constants.LOGE("취소되었습니다")
                    val returnObj = Utils.createJSONObject(
                        true, "취소되었습니다")
                    cameraAction?.promiseReturn(returnObj)
                    ratio = null
                }

                cameraAction?.resolveVoid()
            }
            /** 한 개의 이미지 선택 관련 */
            Constants.PHOTO_DEVICE_RATIO_REQ_CODE -> {
                Constants.LOGD("PHOTO DEVICE RATIO in onActivityResult()")
                if(resultOk) {
                    if(data != null) {
                        val base64 = Constants.BASE64_URL +
                                Photo.convertUriToResizingBase64(data.data, ratio, isWidthRatio)
                        photoDeviceAction?.promiseReturn(base64)
                        ratio = null
                        isWidthRatio = null
                    } else {
                        Constants.LOGD("Data is null")
                        photoDeviceAction?.resolveVoid()
                    }
                } else {
                    Constants.LOGD("PHOTO BY DEVICE RATIO RESULT CANCELED")
                    photoDeviceAction?.resolveVoid()
                }
            }
            Constants.PHOTO_RATIO_REQ_CODE -> {
                Constants.LOGD("PHOTO RATIO in onActivityResult()")
                if(resultOk) {
                    if(data != null) {
                        val base64 = Constants.BASE64_URL +
                                Photo.convertUriToResizingBase64(data.data, ratio, isWidthRatio)

                        photoAction?.promiseReturn(base64)
                        ratio = null
                    } else {
                        Constants.LOGD("Data is null")
                        photoAction?.resolveVoid()
                    }
                } else {
                    Constants.LOGD("PHOTO BY RATIO RESULT CANCELED")
                    photoAction?.resolveVoid()
                }
            }
            /** 멀티(다중) 이미지 선택 관련 */
            Constants.MULTI_PHOTO_DEVICE_RATIO_REQ_CODE -> {
                Constants.LOGD("MULTI PHOTO DEVICE RATIO in onActivityResult()")
                if(resultOk) {
                    val base64Images = ArrayList<String>()
                    if(data != null) {
                        val clipData = data.clipData
                        if(clipData?.itemCount in 1..9) {
                            for(i in 0 until clipData?.itemCount!!) {
                                val imageUri = clipData.getItemAt(i).uri
                                val base64 = Constants.BASE64_URL +
                                        Photo.convertUriToResizingBase64(imageUri, ratio, isWidthRatio)
                                base64Images.add(base64)
                            }

                            multiplePhotoDeviceAction?.promiseReturn(base64Images)
                            ratio = null
                            isWidthRatio = null
                        } else {
                            Toast.showLongText("10장 이상의 사진을 첨부할 수 없습니다.")
                            multiplePhotoDeviceAction?.resolveVoid()
                        }
                    } else {
                        Constants.LOGE("Data is null")
                        multiplePhotoDeviceAction?.resolveVoid()
                    }
                } else {
                    Constants.LOGE("MULTI PHOTO BY DEVICE RATIO RESULT CANCELED")
                    multiplePhotoDeviceAction?.resolveVoid()
                }
            }
            Constants.MULTI_PHOTO_RATIO_REQ_CODE -> {
                Constants.LOGD("MULTI PHOTO RATIO in onActivityResult()")
                if(resultOk) {
                    val base64Images = ArrayList<String>()
                    if(data != null) {
                        val clipData = data.clipData
                        if(clipData?.itemCount in 1..9) {
                            for(idx in 0 until clipData?.itemCount!!) {
                                val imageUri = clipData.getItemAt(idx).uri
                                val base64 = Constants.BASE64_URL +
                                        Photo.convertUriToResizingBase64(imageUri, ratio, isWidthRatio)
                                base64Images.add(base64)
                            }

                            multiplePhotosAction?.promiseReturn(base64Images)
                            ratio = null
                        } else {
                            Toast.showLongText("10장 이상의 사진을 첨부할 수 없습니다.")
                            multiplePhotosAction?.resolveVoid()
                        }
                    } else {
                        Constants.LOGE("Data is null")
                        multiplePhotosAction?.resolveVoid()
                    }
                } else {
                    Constants.LOGE("MULTI PHOTO BY RATIO RESULT CANCELED")
                    multiplePhotosAction?.resolveVoid()
                }
            }
            Constants.PERM_SEND_SMS_REQ_CODE -> {
                if(resultOk) {

                } else {

                }
            }
            Constants.FILE_UPLOAD_REQ_CODE -> {
                var mFilePatCallback: ValueCallback<Array<Uri>>? = null
                if(resultOk) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mFilePatCallback?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
                    }else{
                    }
                    mFilePatCallback = null
                } else {

                }

            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val isNotEmpty = grantResults.isNotEmpty()
        val isGranted = (grantResults[0] == PackageManager.PERMISSION_GRANTED)

        when(requestCode) {
            Constants.PERM_QR_REQ_CODE -> {
                // 처음에 권한을 승인한 경우
                if(isNotEmpty && isGranted) {
                    QRCode.startScan()
                }
                // 권한을 거부한 경우
                else {
                    val returnObj = Utils.createJSONObject(
                        false, Constants.MSG_DENIED_PERM)
                    qrCodeScanAction?.promiseReturn(returnObj)
                }
            }
            Constants.PERM_WRITE_REQ_CODE -> {
                if(isNotEmpty && isGranted) {
                    Constants.LOGD(Constants.LOG_PERM_GRANTED_CAMERA)
                } else {

                }
                Log.e(Constants.TAG_MAIN, Constants.LOG_PERM_GRANTED_WRITE)
            }
            Constants.PERM_READ_WRITE_REQ_CODE -> {
                if(isNotEmpty && isGranted) {
                    Constants.LOGD(Constants.LOG_PERM_GRANTED_READ_WRITE)
                } else {

                }

            }
            Constants.PERM_CAMERA_REQ_CODE -> {
                // 처음에 권한을 승인한 경우
                if(isNotEmpty && isGranted) {
                    if(isWidthRatio != null) {
                        Camera.request(isWidthRatio)
                    } else {
                        Camera.request(null)
                    }

                }
                // 권한을 거부한 경우
                else {
                    val returnObj = Utils.createJSONObject(
                        false, Constants.MSG_DENIED_PERM)
                    cameraDeviceAction?.promiseReturn(returnObj)
                }

            }
            Constants.PERM_LOCATION_REQ_CODE -> {
                // 처음에 권한을 승인한 경우
                if(isNotEmpty && isGranted) {
                    Location.getCurrentLatAndLot()
                }
                // 권한을 거부한 경우
                else {
                    val returnObj = Utils.createJSONObject(
                        false, Constants.MSG_DENIED_PERM)
                    locationAction?.promiseReturn(returnObj)
                }
            }
            Constants.PERM_SEND_SMS_REQ_CODE -> {
                if(isNotEmpty && isGranted) {
                    Constants.LOGD(Constants.LOG_PERM_GRANTED_SEND_SMS)
                } else {

                }
            }
        }
    }

    /** 뒤로 가기 버튼 클릭 이벤트 */
    override fun onBackPressed() {
        if(flex_pop_up_web_view.visibility == View.VISIBLE) {
            Utils.closePopup(
                this@MainActivity, constraintLayout,
                backgroundView, popupCloseButton, flex_pop_up_web_view
            )
        } else
            backPressedTwice()
    }

    /** 뒤로가기 두 번 클릭 이벤트  */
    private fun backPressedTwice() {
        // 두 번 뒤로가기 누를 시 종료
        if(backPressedTwice) {
            super.onBackPressed()
            return
        }

        backPressedTwice = true
        Toast.showLongText("뒤로가기 버튼을 한 번 더 누르시면 종료됩니다.")

        Handler().postDelayed({
            backPressedTwice = false
        }, 2000)
    }

    /*
    * 인터페이스
    * */
    inner class FlexPopupInterface{

        @FlexFuncInterface
        fun WebPopup(array: JSONArray) {
            CoroutineScope(Dispatchers.Main).launch {

                // 뒷배경 뷰 생성
                val mInflater = Utils.getLayoutInflater(this@MainActivity)
                backgroundView = mInflater.inflate(R.layout.background_popup, null)
                constraintLayout.addView(backgroundView)

                val url = array.getString(0)
                val ratio = array.getDouble(1)

                val screenSize = Utils.getScreenSize()
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

                popupCloseButton.setOnClickListener {
                    Utils.closePopup(this@MainActivity, constraintLayout, backgroundView, popupCloseButton, flex_pop_up_web_view)
                }
            }
        }
    }
}