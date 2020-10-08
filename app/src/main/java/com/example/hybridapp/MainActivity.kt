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

    override fun onResume() {
        super.onResume()

        if(smsReceiver == null) {
            smsReceiver = SMSReceiver()
        }
        smsInstance!!.registerReceiver(smsReceiver)
    }

    override fun onPause() {
        smsInstance!!.unregisterReceiver(smsReceiver)
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
            val logUrlDao = LogUrlRoomDatabase.getDatabase(this@MainActivity).logUrlDao()
            repository = LogUrlRepository(logUrlDao)
        }
        // Android KeyStore init
        AndroidKeyStoreUtil.init(App.context())
        setFlexWebView()
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
        flex_web_view.addFlexInterface(Toast)
        flex_web_view.addFlexInterface(Notification)
        flex_web_view.addFlexInterface(Utils)
        flex_web_view.addFlexInterface(smsInstance!!)
        flex_web_view.isVerticalScrollBarEnabled = false
        flex_web_view.isHorizontalScrollBarEnabled = false

        /** FlexWebView Action 설정 */
        flex_web_view.setAction(getString(R.string.type_dialog), Dialog.showAction)
        flex_web_view.mapInterface(getString(R.string.type_network), Network.getStatusAction)
        flex_web_view.setAction(getString(R.string.type_camera_device_ratio), cameraInstance!!.actionByDeviceRatio)
        flex_web_view.setAction(getString(R.string.type_camera_ratio), cameraInstance!!.actionByRatio)
        flex_web_view.setAction(getString(R.string.type_photo_device_ratio), photoInstance!!.actionByDeviceRatioSingle)
        flex_web_view.setAction(getString(R.string.type_photo_ratio), photoInstance!!.actionByRatioSingle)
        flex_web_view.setAction(getString(R.string.type_multi_photo_device_ratio), photoInstance!!.actionByDeviceRatioMulti)
        flex_web_view.setAction(getString(R.string.type_multi_photo_ratio), photoInstance!!.actionByRatioMulti)
        flex_web_view.setAction(getString(R.string.type_qr_code_scan), qrInstance!!.scanAction)
        flex_web_view.setAction(getString(R.string.type_location), locInstance!!.findAction)
        flex_web_view.setAction(getString(R.string.type_send_sms), smsInstance!!.sendAction)
        flex_web_view.setAction(getString(R.string.type_auth), Authentication.authentication)
        flex_web_view.setAction(getString(R.string.type_local_repo), Utils.localRepository)
        flex_web_view.setAction(getString(R.string.type_web_pop_up), Utils.webPopUp)
        flex_web_view.setAction(getString(R.string.type_download), Utils.downloadAction)
    }

    /** 뒤로 가기 버튼 클릭 이벤트 */
    override fun onBackPressed() {
        if(isPopupOpen()) {
            Utils.removeBackgroundViewFrom(App.activity.constraintLayout)
            Utils.removeCloseButtonFrom(App.activity.constraintLayout)
            Utils.hidePopUpView(this@MainActivity, flex_pop_up_web_view)
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
            Toast.showShortToast(getString(R.string.back_pressed))
            Handler(Looper.getMainLooper()).postDelayed({
                isPressedTwice = false
            }, 2000)
        }
    }
}