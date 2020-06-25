package com.example.hybridapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
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
import org.json.JSONObject
import kotlin.collections.ArrayList

class MainActivity : BasicActivity() {

    private lateinit var repository: LogUrlRepository
    private var smsReceiver: SMSReceiver? = null

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
        App.INSTANCE.setTheme(R.style.SplashTheme)

        init()

        App.INSTANCE.setTheme(R.style.AppTheme)
    }

    /** 시작 시 기본 초기화 함수 */
    private fun init() {
        /** Room Database default settings */
        CoroutineScope(Dispatchers.Default).launch {
            val logUrlDao
                    = LogUrlRoomDatabase.getDatabase(this@MainActivity).logUrlDao()
            repository = LogUrlRepository(logUrlDao)
        }

        // Android KeyStore init
        AndroidKeyStoreUtil.init(App.context())

        setFlexWebView()
        setActions()
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
        flex_web_view.isVerticalScrollBarEnabled = false
        flex_web_view.isHorizontalScrollBarEnabled = false
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
        flex_web_view.setAction(Constants.TYPE_SEND_SMS, Action.sendSms)
        flex_web_view.setAction(Constants.TYPE_AUTH, Action.authentication)
        flex_web_view.setAction(Constants.TYPE_LOCAL_REPO, Action.localRepository)
        flex_web_view.setAction(Constants.TYPE_WEB_POP_UP, Action.webPopUp)
        flex_web_view.setAction(Constants.TYPE_DOWNLOAD, Action.fileDownload)
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
                            true, result.contents, null)
                        qrCodeScanAction?.promiseReturn(returnObj)
                    }
                    // QR Code 값이 없는 경우
                    else {
                        Constants.LOGE("QR Code result is null")
                        val returnObj = Utils.createJSONObject(
                            true, null, Constants.MSG_NO_QR)
                        qrCodeScanAction?.promiseReturn(returnObj)
                    }
                }
                // QR Code 실패
                else {
                    Constants.LOGE("QR CODE RESULT_CANCELED")
                    val returnObj = Utils.createJSONObject(
                        true, null, Constants.MSG_NOT_LOAD_QR)
                    qrCodeScanAction?.promiseReturn(returnObj)
                }
            }
            Constants.CAMERA_DEVICE_RATIO_REQ_CODE -> {
                Constants.LOGD("CAMERA DEVICE RATIO in onActivityResult()")
                Utils.visibleProgressBar()
                // 뒷배경 뷰 생성
                val mInflater = Utils.getLayoutInflater(App.activity)
                backgroundView = mInflater.inflate(R.layout.background_popup, null)
                App.activity.constraintLayout.addView(backgroundView)

                // 카메라 촬영 성공
                if(resultOk) {
                    var bitmap: Bitmap? = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.decodeBitmap(ImageDecoder.createSource(Utils.getOutputMediaFile()!!))
                    }
                    else {
                        MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(Utils.getOutputMediaFile()))
                    }
                    val degree = Photo.getDegreesFromPath(Utils.getOutputMediaFile().toString())
                    Constants.LOGD("Degree: $degree")

                    val rotatedBitmap = Photo.rotateBitmap(bitmap, degree)
                    val resizedBitmap = Photo.resizeBitmapByDeviceRatio(rotatedBitmap!!,
                        ratio!!, isWidthRatio)
                    if(bitmap != null) {
                        val base64 = Constants.BASE64_URL +
                                Photo.getBase64FromBitmap(resizedBitmap)
                        val returnObj = Utils.createJSONObject(true,
                            base64, null)
                        cameraDeviceAction?.promiseReturn(returnObj)
                        ratio = null
                        isWidthRatio = null
                    } else {
                        Constants.LOGE("사진이 존재하지 않습니다")
                        val returnObj = Utils.createJSONObject(true,
                            null, "사진이 존재하지 않습니다")
                        cameraDeviceAction?.promiseReturn(returnObj)
                        ratio = null
                        isWidthRatio = null
                    }
                }
                // 카메라 촬영 실패
                else {
                    Constants.LOGE("취소되었습니다")
                    val returnObj = Utils.createJSONObject(true,
                        null, "취소되었습니다")
                    cameraDeviceAction?.promiseReturn(returnObj)
                    ratio = null
                    isWidthRatio = null
                }

                constraintLayout.removeView(backgroundView)
                Utils.invisibleProgressBar()
            }
            Constants.CAMERA_RATIO_REQ_CODE -> {
                Constants.LOGD("CAMERA DEVICE RATIO in onActivityResult()")

                // 카메라 촬영 성공
                if(resultOk) {
                    Utils.visibleProgressBar()

                    var bitmap: Bitmap? = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.decodeBitmap(ImageDecoder.createSource(Utils.getOutputMediaFile()!!))
                    }
                    else {
                        MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(Utils.getOutputMediaFile()))
                    }

                    Utils.invisibleProgressBar()

                    if(bitmap != null) {
//                        val base64 = Constants.BASE64_URL +
//                                Photo.convertUriToResizingBase64(data.getParcelableExtra(MediaStore.EXTRA_OUTPUT), ratio, isWidthRatio)

                        val base64 = Constants.BASE64_URL +
                                Photo.getBase64FromBitmap(bitmap)
                        val returnObj = Utils.createJSONObject(true,
                            base64, null)
                        cameraAction?.promiseReturn(returnObj)
                        ratio = null
                    } else {
                        Constants.LOGE("사진이 존재하지 않습니다")
                        val returnObj = Utils.createJSONObject(true,
                            null, "사진이 존재하지 않습니다")
                        cameraAction?.promiseReturn(returnObj)
                        ratio = null
                    }
                }
                // 카메라 촬영 실패
                else {
                    Constants.LOGE("취소되었습니다")
                    val returnObj = Utils.createJSONObject(true,
                        null, "취소되었습니다")
                    cameraAction?.promiseReturn(returnObj)
                    ratio = null
                }

                cameraAction?.resolveVoid()
            }
            Constants.PHOTO_DEVICE_RATIO_REQ_CODE -> {
                Constants.LOGD("PHOTO DEVICE RATIO in onActivityResult()")

                // 사진 불러오기 성공
                if(resultOk) {
                    if(data != null) {
                        val base64 = Constants.BASE64_URL +
                                Photo.convertUriToResizingBase64(data.data, ratio, isWidthRatio)

                        val returnObj = Utils.createJSONObject(true,
                            base64, null)
                        photoDeviceAction?.promiseReturn(returnObj)
                        ratio = null
                        isWidthRatio = null
                    } else {
                        Constants.LOGE("사진이 존재하지 않습니다")
                        val returnObj = Utils.createJSONObject(true,
                            null, "사진이 존재하지 않습니다")
                        photoDeviceAction?.promiseReturn(returnObj)
                        ratio = null
                        isWidthRatio = null
                    }
                }
                // 사진 불러오기 실패
                else {
                    Constants.LOGE("취소되었습니다")
                    val returnObj = Utils.createJSONObject(true,
                        null, "취소되었습니다")
                    photoDeviceAction?.promiseReturn(returnObj)
                    ratio = null
                    isWidthRatio = null
                }
            }
            Constants.PHOTO_RATIO_REQ_CODE -> {
                Constants.LOGD("PHOTO RATIO in onActivityResult()")

                // 사진 불러오기 성공
                if(resultOk) {
                    if(data != null) {
                        val base64 = Constants.BASE64_URL +
                                Photo.convertUriToResizingBase64(data.data, ratio, isWidthRatio)

                        val returnObj = Utils.createJSONObject(true,
                            base64, null)
                        photoAction?.promiseReturn(returnObj)
                        ratio = null
                    } else {
                        Constants.LOGE("사진이 존재하지 않습니다")
                        val returnObj = Utils.createJSONObject(true,
                            null, "사진이 존재하지 않습니다")
                        photoAction?.promiseReturn(returnObj)
                        ratio = null
                    }
                }
                // 사진 불러오기 실패
                else {
                    Constants.LOGE("취소되었습니다")
                    val returnObj = Utils.createJSONObject(true,
                        null, "취소되었습니다")
                    photoAction?.promiseReturn(returnObj)
                    ratio = null
                }
            }
            Constants.MULTI_PHOTO_DEVICE_RATIO_REQ_CODE -> {
                Constants.LOGD("MULTI PHOTO DEVICE RATIO in onActivityResult()")

                // 사진 불러오기 성공
                if(resultOk) {
                    val base64Images = ArrayList<String>()
                    if(data != null) {
                        val clipData = data.clipData
                        if(clipData?.itemCount in 0..9) {
                            for(i in 0 until clipData?.itemCount!!) {
                                val imageUri = clipData.getItemAt(i).uri
//                                Log.d("dlgodnjs", " aa $imageUri")

                                val base64 = Constants.BASE64_URL +
                                        Photo.convertUriToResizingBase64(imageUri, ratio, isWidthRatio)
                                base64Images.add(base64)

//                                val mInflater = Utils.getLayoutInflater(this@MainActivity)
//                                var tempView: View = mInflater.inflate(R.layout.test, null)
//                                var imgView : ImageView = tempView.findViewById(R.id.test)
//                                imgView.setImageBitmap(Photo.getBitmapFromUri(imageUri))
//                                constraintLayout.addView(tempView)

                                Constants.LOGD("${i + 1}번째 : $base64")
                            }

//                            val returnObj = Utils.createJSONObject(true,
//                                base64Images, null)
                            val returnObj = JSONObject()
                            returnObj.put(Constants.OBJ_KEY_AUTH, true)
                            returnObj.put(Constants.OBJ_KEY_DATA, base64Images.toTypedArray())
                            returnObj.put(Constants.OBJ_KEY_MSG, null)
                            multiplePhotoDeviceAction?.promiseReturn(returnObj)
                            ratio = null
                            isWidthRatio = null
                        } else {
                            Toast.showLongText("10장 이상의 사진을 첨부할 수 없습니다")
                            val returnObj = Utils.createJSONObject(true,
                                null, "10장 이상의 사진을 첨부할 수 없습니다")
                            multiplePhotoDeviceAction?.promiseReturn(returnObj)
                            ratio = null
                            isWidthRatio = null
                        }
                    } else {
                        Constants.LOGE("Data is null")
                        val returnObj = Utils.createJSONObject(true,
                            null, "사진을 첨부할 수 없습니다")
                        multiplePhotoDeviceAction?.promiseReturn(returnObj)
                        ratio = null
                        isWidthRatio = null
                    }
                }
                // 사진 불러오기 실패
                else {
                    Constants.LOGE("MULTI PHOTO BY DEVICE RATIO RESULT CANCELED")
                    val returnObj = Utils.createJSONObject(true,
                        null, "취소되었습니다")
                    multiplePhotoDeviceAction?.promiseReturn(returnObj)
                    ratio = null
                    isWidthRatio = null
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
                            multiplePhotoAction?.promiseReturn(base64Images)
                            ratio = null
                        } else {
                            Toast.showLongText("10장 이상의 사진을 첨부할 수 없습니다.")
                            val returnObj = Utils.createJSONObject(true,
                                null, "10장 이상의 사진을 첨부할 수 없습니다")
                            multiplePhotoAction?.promiseReturn(returnObj)
                            ratio = null
                        }
                    } else {
                        Constants.LOGE("Data is null")
                        val returnObj = Utils.createJSONObject(true,
                            null, "사진을 첨부할 수 없습니다")
                        multiplePhotoAction?.promiseReturn(returnObj)
                        ratio = null
                    }
                } else {
                    Constants.LOGE("MULTI PHOTO BY RATIO RESULT CANCELED")
                    val returnObj = Utils.createJSONObject(true,
                        null, "취소되었습니다")
                    multiplePhotoAction?.promiseReturn(returnObj)
                    ratio = null
                }
            }
            Constants.SEND_SMS_REQ_CODE -> {
                Constants.LOGD("SEND SMS in onActivityResult()")

                // Intent.ACTION_SEND는 return값이 없음
                val returnObj = JSONObject()
                returnObj.put(Constants.OBJ_KEY_DATA, true)
                sendSmsAction?.promiseReturn(returnObj)
                phoneNumber = null
                smsMessage = null
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
        val deniedObj = Utils.createJSONObject(false,
            null, Constants.MSG_DENIED_PERM)

        when(requestCode) {
            Constants.PERM_QR_REQ_CODE -> {
                // 처음에 권한을 승인한 경우
                if(isNotEmpty && isGranted) {
                    QRCode.startScan()
                }
                // 권한을 거부한 경우
                else {
                    Constants.LOGD("QR코드 권한 거부")
                    qrCodeScanAction?.promiseReturn(deniedObj)
                }
            }
            Constants.PERM_PHOTO_DEVICE_REQ_CODE -> {
                // 처음에 권한을 승인한 경우
                if(isNotEmpty && isGranted) {
                    Photo.requestImage()
                }
                // 권한을 거부한 경우
                else {
                    Constants.LOGD("싱글 이미지 권한 거부")
                    photoDeviceAction?.promiseReturn(deniedObj)
                    ratio = null
                    isWidthRatio = null
                }
            }
            Constants.PERM_PHOTO_REQ_CODE -> {
                // 처음에 권한을 승인한 경우
                if(isNotEmpty && isGranted) {
                    Photo.requestImage()
                }
                // 권한을 거부한 경우
                else {
                    photoAction?.promiseReturn(deniedObj)
                    ratio = null
                }
            }
            Constants.PERM_MUL_PHOTO_DEVICE_REQ_CODE -> {
                // 처음에 권한을 승인한 경우
                if(isNotEmpty && isGranted) {
                    Photo.requestMultipleImages()
                }
                // 권한을 거부한 경우
                else {
                    Constants.LOGD("멀티 이미지 권한 거부")
                    multiplePhotoDeviceAction?.promiseReturn(deniedObj)
                    ratio = null
                    isWidthRatio = null
                }
            }
            Constants.PERM_MUL_PHOTO_REQ_CODE -> {
                // 처음에 권한을 승인한 경우
                if(isNotEmpty && isGranted) {
                    Photo.requestMultipleImages()
                }
                // 권한을 거부한 경우
                else {
                    multiplePhotoAction?.promiseReturn(deniedObj)
                    ratio = null
                }
            }
            Constants.PERM_FILE_REQ_CODE -> {
                // 처음에 권한을 승인한 경우
                if(isNotEmpty && isGranted) {
                    Utils.downloadFileFromUrl()
                }
                // 권한을 거부한 경우
                else {
                    // fileAction?.promiseReturn(deniedObj)
                    Constants.LOGE("Permission denied.")
                }
            }
            Constants.PERM_CAMERA_DEVICE_REQ_CODE -> {
                // 처음에 권한을 승인한 경우
                if(isNotEmpty && isGranted) {
                    Camera.request()
                }
                // 권한을 거부한 경우
                else {
                    Constants.LOGD("카메라 권한 거부")
                    cameraDeviceAction?.promiseReturn(deniedObj)
                    ratio = null
                    isWidthRatio = null
                }
            }
            Constants.PERM_CAMERA_REQ_CODE -> {
                // 처음에 권한을 승인한 경우
                if(isNotEmpty && isGranted) {
                    Camera.request()
                }
                // 권한을 거부한 경우
                else {
                    cameraAction?.promiseReturn(deniedObj)
                    ratio = null
                }
            }
            Constants.PERM_LOCATION_REQ_CODE -> {
                // 처음에 권한을 승인한 경우
                if(isNotEmpty && isGranted) {
                    Location.getCurrentLatAndLot()
                }
                // 권한을 거부한 경우
                else {
                    locationAction?.promiseReturn(deniedObj)
                }
            }
            Constants.PERM_SEND_SMS_REQ_CODE -> {
                // 처음에 권한을 승인한 경우
                if(isNotEmpty && isGranted) {
                    SMS.sendMessage()
                }
                // 권한을 거부한 경우
                else {
                    sendSmsAction?.promiseReturn(deniedObj)
                    phoneNumber = null
                    smsMessage = null
                }
            }
        }
    }

    /** 뒤로 가기 버튼 클릭 이벤트 */
    override fun onBackPressed() {
        if(flex_pop_up_web_view.visibility == View.VISIBLE) {
            Utils.closePopup(
                this@MainActivity, constraintLayout,
                backgroundView, popUpCloseButton, flex_pop_up_web_view
            )
        } else
            backPressedTwice()
    }

    /** 뒤로가기 두 번 클릭 이벤트  */
    private fun backPressedTwice() {
        // 뒤로 갈 곳이 있으면 뒤로 간다.
        if(flex_web_view.canGoBack()) {
            flex_web_view.goBack()
            return
        }

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
}