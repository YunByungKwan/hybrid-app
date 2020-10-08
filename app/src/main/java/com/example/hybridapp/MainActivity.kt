package com.example.hybridapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import com.example.hybridapp.data.LogUrlRepository
import com.example.hybridapp.data.LogUrlRoomDatabase
import com.example.hybridapp.util.*
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.basic.BasicWebChromeClient
import com.example.hybridapp.basic.BasicWebViewClient
import com.example.hybridapp.module.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BasicActivity() {

    private lateinit var repository: LogUrlRepository
    private var smsReceiver: SMSReceiver? = null

    override fun onResume() {
        super.onResume()

        if(smsReceiver == null) {
            smsReceiver = SMSReceiver()
        }
        sms!!.registerReceiver(smsReceiver)
    }

    override fun onPause() {
        sms!!.unregisterReceiver(smsReceiver)
        super.onPause()
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
        flex_pop_up_web_view.baseUrl = App.INSTANCE.getString(R.string.base_url)
        flex_web_view.baseUrl = App.INSTANCE.getString(R.string.base_url)
        flex_web_view.loadUrl(App.INSTANCE.getString(R.string.url))
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
        flex_web_view.setAction(getString(R.string.type_dialog), Dialog.action)
        flex_web_view.mapInterface(getString(R.string.type_network), Action.network)
        flex_web_view.setAction(getString(R.string.type_camera_device_ratio), Action.cameraByDeviceRatio)
        flex_web_view.setAction(getString(R.string.type_camera_ratio), Action.cameraByRatio)
        flex_web_view.setAction(getString(R.string.type_photo_device_ratio), Action.photoByDeviceRatio)
        flex_web_view.setAction(getString(R.string.type_photo_ratio), Action.photoByRatio)
        flex_web_view.setAction(getString(R.string.type_multi_photo_device_ratio), Action.multiPhotoByDeviceRatio)
        flex_web_view.setAction(getString(R.string.type_multi_photo_ratio), Action.multiPhotoByRatio)
        flex_web_view.setAction(getString(R.string.type_qr_code_scan), Action.qrCode)
        flex_web_view.setAction(getString(R.string.type_location), Action.location)
        flex_web_view.setAction(getString(R.string.type_send_sms), Action.sendSms)
        flex_web_view.setAction(getString(R.string.type_auth), Action.authentication)
        flex_web_view.setAction(getString(R.string.type_local_repo), Action.localRepository)
        flex_web_view.setAction(getString(R.string.type_web_pop_up), Action.webPopUp)
        flex_web_view.setAction(getString(R.string.type_download), Action.fileDownload)
        //flex_web_view.mapInterface("network2", Action.network2)
    }

    /** 뒤로 가기 버튼 클릭 이벤트 */
    override fun onBackPressed() {
        if(isPopupOpen()) {
            Utils.closePopup(this@MainActivity, constraintLayout,
                backgroundView, popUpCloseButton, flex_pop_up_web_view)
        } else {
            backPressed()
        }
    }

    /** 팝업창을 띄웠는지 여부 */
    private fun isPopupOpen(): Boolean = flex_pop_up_web_view.visibility == View.VISIBLE

    /** 팝업창이 없을 때 뒤로 가기 버튼 클릭 이벤트  */
    private fun backPressed() {
        if(flex_web_view.canGoBack()) {
            flex_web_view.goBack()
        } else {
            backPressedTwice()
        }
    }

    /** 웹뷰가 뒤로 갈 곳이 없을 때 처리 */
    private fun backPressedTwice() {
        if(isPressedTwice) {
            super.onBackPressed()
        } else {
            isPressedTwice = true
            toast!!.showShortText(getString(R.string.back_pressed))

            Handler(Looper.getMainLooper()).postDelayed({
                isPressedTwice = false
            }, 2000)
        }
    }
}