package com.example.hybridapp

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import app.dvkyun.flexhybridand.FlexWebView
import app.dvkyun.flexhybridand.FlexWebViewClient
import com.example.hybridapp.data.LogUrlRepository
import com.example.hybridapp.data.LogUrlRoomDatabase
import com.example.hybridapp.util.*
import com.example.hybridapp.basic.BasicWebChromeClient
import com.example.hybridapp.basic.BasicWebViewClient
import com.example.hybridapp.databinding.ActivityMainBinding
import com.example.hybridapp.module.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    /** Repository */
    var repository: LogUrlRepository? = null

    /** 문자를 받는 Broadcast Receiver */
    var smsReceiver: SMSReceiver? = null

    /** 팝업 관련 */
    var backgroundView: View? = null
    var closeBtn: Button? = null

    /** 뒤로가기 두 번 방지 Boolean */
    var isPressedTwice: Boolean = false

    /** Module Instance */
    var qrCodeCompat: QRCodeCompat? = null
    var cameraCompat: CameraCompat? = null
    var photoCompat: PhotoCompat? = null
    var locationCompat: LocationCompat? = null
    var smsCompat: SmsCompat? = null
    var contactsCompat: ContactsCompat? = null

    private lateinit var binding: ActivityMainBinding

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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    /** 시작 시 기본 초기화 함수 */
    private fun init() {
        qrCodeCompat = QRCodeCompat(this)
        cameraCompat = CameraCompat(this)
        photoCompat = PhotoCompat(this)
        locationCompat = LocationCompat(this)
        smsCompat = SmsCompat(this)
        contactsCompat = ContactsCompat(this)
        CoroutineScope(Dispatchers.Default).launch {
            val logUrlDao = LogUrlRoomDatabase.getDatabase(this@MainActivity).logUrlDao()
            repository = LogUrlRepository(logUrlDao)
        }
        // Android KeyStore init
        AndroidKeyStoreUtil.init(applicationContext)
        setFlexWebView()
        setFlexInterface()
        setFlexAction()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setTheme(R.style.AppTheme)
    }

    /** 기본, 팝업 FlexView 설정 */
    private fun setFlexWebView() {
        WebView.setWebContentsDebuggingEnabled(true)
        binding.flexPopUpWebView.baseUrl = App.INSTANCE.getString(R.string.base_url)
        binding.flexWebView.apply {
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
        binding.flexWebView.addFlexInterface(ToastCompat)
        binding.flexWebView.addFlexInterface(NotiCompat)
        binding.flexWebView.addFlexInterface(Utils)
        binding.flexWebView.addFlexInterface(smsCompat!!)
        binding.flexWebView.mapInterface(getString(R.string.type_network), NetworkCompat.getNetworkStatusAction)
    }

    private fun setFlexAction() {
        binding.flexWebView.setAction(getString(R.string.type_dialog), DialogCompat.showDialogAction)
        binding.flexWebView.setAction(getString(R.string.type_camera_device_ratio), cameraCompat!!.actionByDeviceRatio)
        binding.flexWebView.setAction(getString(R.string.type_camera_ratio), cameraCompat!!.actionByRatio)
        binding.flexWebView.setAction(getString(R.string.type_photo_device_ratio), photoCompat!!.actionByDeviceRatioSingle)
        binding.flexWebView.setAction(getString(R.string.type_photo_ratio), photoCompat!!.actionByRatioSingle)
        binding.flexWebView.setAction(getString(R.string.type_multi_photo_device_ratio), photoCompat!!.actionByDeviceRatioMulti)
        binding.flexWebView.setAction(getString(R.string.type_multi_photo_ratio), photoCompat!!.actionByRatioMulti)
        binding.flexWebView.setAction(getString(R.string.type_qr_code_scan), qrCodeCompat!!.scanQrCodeAction)
        binding.flexWebView.setAction(getString(R.string.type_location), locationCompat!!.getLocationAction)
        binding.flexWebView.setAction(getString(R.string.type_send_sms), smsCompat!!.sendSmsAction)
        binding.flexWebView.setAction(getString(R.string.type_auth), Authentication.authentication)
        binding.flexWebView.setAction(getString(R.string.type_local_repo), Utils.localRepository)
        binding.flexWebView.setAction(getString(R.string.type_web_pop_up), Utils.webPopUp)
        binding.flexWebView.setAction(getString(R.string.type_download), Utils.downloadAction)
    }

    /** sms리시버를 등록한다. */
    private fun registerSmsReceiver() {
        if(smsReceiver == null) {
            smsReceiver = SMSReceiver()
        }
        smsCompat?.registerReceiver(smsReceiver)
    }

    /** sms리시버를 해제한다. */
    private fun unregisterSmsReceiver() {
        smsCompat?.unregisterReceiver(smsReceiver)
    }

    /** FloatingActionButton 클릭시 이벤트 */
    fun onClickFab(v: View?) {
        contactsCompat?.getNameAndNumberFromContacts()
    }

    /** 뒤로 가기 버튼 클릭 이벤트 */
    override fun onBackPressed() {
        if(isPopupOpen()) {
            hidePopUpView()
        } else {
            backPressed()
        }
    }

    /** 팝업창을 띄웠는지 여부 */
    private fun isPopupOpen(): Boolean = binding.flexPopUpWebView.visibility == View.VISIBLE

    /** 팝업창이 없을 때 뒤로 가기 버튼 클릭 이벤트  */
    private fun backPressed() {
        if(binding.flexWebView.canGoBack()) {
            binding.flexWebView.goBack()
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

    /** 백그라운드 뷰 생성 */
    private fun addBackgroundViewTo() {
        backgroundView = layoutInflater.inflate(R.layout.background_popup, binding.root)
    }

    /** 백그라운드 뷰 제거 */
    private fun removeBackgroundViewFrom() {
        binding.root.removeView(backgroundView)
        backgroundView = null
    }

    /** 닫기 버튼 생성 */
    private fun addCloseButton() {
        closeBtn = Button(this)
        val params = ConstraintLayout.LayoutParams(90, 90).apply {
            topToTop = binding.root.id
            endToEnd = binding.root.id
            startToStart = binding.root.id
            topMargin = 10
        }

        closeBtn?.text = "X"
        closeBtn?.textSize = 12F
        closeBtn?.background = ContextCompat.getDrawable(this, R.drawable.circle)
        closeBtn?.textAlignment = View.TEXT_ALIGNMENT_CENTER
        closeBtn?.layoutParams = params

        binding.root.addView(closeBtn)

        closeBtn?.setOnClickListener {
            hidePopUpView()
        }
    }

    /** 닫기 버튼 제거 */
    private fun removeCloseButtonFrom() {
        binding.root.removeView(closeBtn)
        closeBtn = null
    }

    /** 팝업창을 닫는다 */
    fun hidePopUpView() {
        removeBackgroundViewFrom()
        removeCloseButtonFrom()
        binding.flexPopUpWebView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.close))
        binding.flexPopUpWebView.visibility = View.GONE
    }

    /** 팝업창을 띄운다 */
    fun showPopUpView(url: String, width: Int, height: Int) {
        addBackgroundViewTo()
        addCloseButton()
        binding.flexPopUpWebView.loadUrl(url)
        binding.flexPopUpWebView.visibility = View.VISIBLE
        binding.flexPopUpWebView.layoutParams =
            Utils.getParamsAlignCenterInConstraintLayout(width, height, R.id.constraintLayout)
        binding.flexPopUpWebView.startAnimation(AnimationUtils.loadAnimation(App.activity, R.anim.open))
        binding.flexPopUpWebView.bringToFront()

        binding.flexPopUpWebView.webViewClient = object: FlexWebViewClient() {
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?,
                                         error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                val returnObj = JSONObject()
                returnObj.put(App.INSTANCE.getString(R.string.obj_key_data), false)
                returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), "해당 URL을 불러올 수 없습니다")
                Utils.popUpAction?.promiseReturn(returnObj)
                Utils.popUpAction = null
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val returnObj = JSONObject()
                returnObj.put(App.INSTANCE.getString(R.string.obj_key_data), true)
                Utils.popUpAction?.promiseReturn(returnObj)
            }
        }
    }
}