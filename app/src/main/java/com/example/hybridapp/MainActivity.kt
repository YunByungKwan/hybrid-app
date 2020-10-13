package com.example.hybridapp

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
        registerSmsReceiver()
    }

    override fun onPause() {
        unregisterSmsReceiver()
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    /** 시작 시 기본 초기화 함수 */
    private fun init() {
        setTheme(R.style.SplashTheme)
        CoroutineScope(Dispatchers.Default).launch {
            val logUrlDao = LogUrlRoomDatabase.getDatabase(this@MainActivity).logUrlDao()
            repository = LogUrlRepository(logUrlDao)
        }
        // Android KeyStore init
        AndroidKeyStoreUtil.init(App.context())
        setFlexWebView()
        setFlexInterface()
        setFlexAction()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setTheme(R.style.AppTheme)
    }

    /** 기본, 팝업 FlexView 설정 */
    private fun setFlexWebView() {
        WebView.setWebContentsDebuggingEnabled(true)
        flex_pop_up_web_view.baseUrl = App.INSTANCE.getString(R.string.base_url)
        flex_web_view.apply {
            baseUrl = getString(R.string.base_url)
            loadUrl(getString(R.string.url))
            settings.setSupportMultipleWindows(true)
            webChromeClient = BasicWebChromeClient(this@MainActivity)
            webViewClient = BasicWebViewClient()
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
        }
    }

    private fun setFlexInterface() {
        flex_web_view.addFlexInterface(ToastCompat)
        flex_web_view.addFlexInterface(NotiCompat)
        flex_web_view.addFlexInterface(Utils)
        flex_web_view.addFlexInterface(smsCompat!!)
        flex_web_view.mapInterface(getString(R.string.type_network), NetworkCompat.getNetworkStatusAction)
    }

    private fun setFlexAction() {
        flex_web_view.setAction(getString(R.string.type_dialog), DialogCompat.showDialogAction)
        flex_web_view.setAction(getString(R.string.type_camera_device_ratio), cameraCompat!!.actionByDeviceRatio)
        flex_web_view.setAction(getString(R.string.type_camera_ratio), cameraCompat!!.actionByRatio)
        flex_web_view.setAction(getString(R.string.type_photo_device_ratio), photoCompat!!.actionByDeviceRatioSingle)
        flex_web_view.setAction(getString(R.string.type_photo_ratio), photoCompat!!.actionByRatioSingle)
        flex_web_view.setAction(getString(R.string.type_multi_photo_device_ratio), photoCompat!!.actionByDeviceRatioMulti)
        flex_web_view.setAction(getString(R.string.type_multi_photo_ratio), photoCompat!!.actionByRatioMulti)
        flex_web_view.setAction(getString(R.string.type_qr_code_scan), qrCodeCompat!!.scanQrCodeAction)
        flex_web_view.setAction(getString(R.string.type_location), locationCompat!!.getLocationAction)
        flex_web_view.setAction(getString(R.string.type_send_sms), smsCompat!!.sendSmsAction)
        flex_web_view.setAction(getString(R.string.type_auth), Authentication.authentication)
        flex_web_view.setAction(getString(R.string.type_local_repo), Utils.localRepository)
        flex_web_view.setAction(getString(R.string.type_web_pop_up), Utils.webPopUp)
        flex_web_view.setAction(getString(R.string.type_download), Utils.downloadAction)
    }

    /** sms리시버를 등록한다. */
    private fun registerSmsReceiver() {
        if(smsReceiver == null) {
            smsReceiver = SMSReceiver()
        }
        smsCompat!!.registerReceiver(smsReceiver)
    }

    /** sms리시버를 해제한다. */
    private fun unregisterSmsReceiver() {
        smsCompat!!.unregisterReceiver(smsReceiver)
    }

    /** FloatingActionButton 클릭시 이벤트 */
    fun onClickFab(v: View?) {
        contactsCompat!!.getNameAndNumberFromContacts()
    }

    /** 뒤로 가기 버튼 클릭 이벤트 */
    override fun onBackPressed() {
        if(isPopupOpen()) {
            closePopUp()
        } else {
            backPressed()
        }
    }

    /** 팝업창을 띄웠는지 여부 */
    private fun isPopupOpen(): Boolean = flex_pop_up_web_view.visibility == View.VISIBLE

    /** 팝업을 닫는다. */
    private fun closePopUp() {
        Utils.removeBackgroundViewFrom(App.activity.constraintLayout)
        Utils.removeCloseButtonFrom(App.activity.constraintLayout)
        Utils.hidePopUpView(this@MainActivity, flex_pop_up_web_view)
    }

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
            ToastCompat.showShortToast(getString(R.string.back_pressed))
            Handler(Looper.getMainLooper()).postDelayed({
                isPressedTwice = false
            }, 2000)
        }
    }
}