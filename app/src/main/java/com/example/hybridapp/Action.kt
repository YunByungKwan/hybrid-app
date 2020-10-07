package com.example.hybridapp

import android.Manifest
import android.content.DialogInterface
import android.os.Build
import android.view.View
import android.view.animation.AnimationUtils
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.result.registerForActivityResult
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import app.dvkyun.flexhybridand.FlexAction
import app.dvkyun.flexhybridand.FlexData
import app.dvkyun.flexhybridand.FlexLambda
import app.dvkyun.flexhybridand.FlexWebViewClient
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.example.hybridapp.module.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONObject

object Action {

    private val basicActivity = App.activity as BasicActivity

    /**================================= Dialog Action ===========================================*/
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    val dialog = FlexLambda.action { dialogAction, array ->
        launch(Main) {
            val title = array[0].asString()
            val contents = array[1].asString()
            val mapData = array[2].asMap()
            val isDialog = array[3].asBoolean()

            mapData?.let {
                val dialogKeys = arrayOf("basic", "destructive", "cancel")
                val basic: String? = it[dialogKeys[0]]?.asString()
                val destructive: String? = it[dialogKeys[1]]?.asString()
                val cancel: String? = it[dialogKeys[2]]?.asString()
                val returnObj = JSONObject()


                if (isDialog!!) {
                    val posListener = DialogInterface.OnClickListener { _, _ ->
                        basic?.let {
                            returnObj.put(
                                App.INSTANCE.getString(R.string.obj_key_msg),
                                dialogKeys[0]
                            )
                            Utils.LOGD(
                                "returnObj['msg'] = " +
                                        returnObj.getString(App.INSTANCE.getString(R.string.obj_key_msg))
                            )
                            dialogAction.promiseReturn(returnObj)
                        }
                    }
                    val neutralListener = DialogInterface.OnClickListener { _, _ ->
                        destructive?.let {
                            returnObj.put(
                                App.INSTANCE.getString(R.string.obj_key_msg),
                                dialogKeys[1]
                            )
                            Utils.LOGD(
                                "returnObj['msg'] = " +
                                        returnObj.getString(App.INSTANCE.getString(R.string.obj_key_msg))
                            )
                            dialogAction.promiseReturn(returnObj)
                        }
                    }
                    val negListener = DialogInterface.OnClickListener { _, _ ->
                        cancel?.let {
                            returnObj.put(
                                App.INSTANCE.getString(R.string.obj_key_msg),
                                dialogKeys[2]
                            )
                            Utils.LOGD(
                                "returnObj['msg'] = " +
                                        returnObj.getString(App.INSTANCE.getString(R.string.obj_key_msg))
                            )
                            dialogAction.promiseReturn(returnObj)
                        }
                    }
                    val exitListener = {
                        returnObj.put(
                            App.INSTANCE.getString(R.string.obj_key_msg),
                            dialogKeys[2]
                        )
                        Utils.LOGD(
                            "returnObj['msg'] = " +
                                    returnObj.getString(App.INSTANCE.getString(R.string.obj_key_msg))
                        )
                        dialogAction.promiseReturn(returnObj)
                    }

                    Dialog.show(
                        title, contents, basic, destructive, cancel,
                        posListener, neutralListener, negListener, exitListener
                    )
                } else {  // Bottom Dialog (Bottom Sheet Dialog)
                    val dialog = BottomSheetDialog(App.activity)

                    var posBtn = Dialog.getBtnView(basic)
                    posBtn?.let { btn ->
                        btn.setOnClickListener {
                            returnObj.put(
                                App.INSTANCE.getString(R.string.obj_key_msg),
                                dialogKeys[0]
                            )
                            Utils.LOGD(
                                "returnObj['msg'] = " +
                                        "${returnObj.getString(App.INSTANCE.getString(R.string.obj_key_msg))}"
                            )
                            dialogAction?.promiseReturn(returnObj)
                            dialog.dismiss()
                        }
                    }

                    var neutralBtn = Dialog.getBtnView(destructive)
                    neutralBtn?.let { btn ->
                        btn.setOnClickListener {
                            returnObj.put(
                                App.INSTANCE.getString(R.string.obj_key_msg),
                                dialogKeys[1]
                            )
                            Utils.LOGD(
                                "returnObj['msg'] = " +
                                        "${returnObj.getString(App.INSTANCE.getString(R.string.obj_key_msg))}"
                            )
                            dialogAction?.promiseReturn(returnObj)
                            dialog.dismiss()
                        }
                    }

                    var negBtn = Dialog.getBtnView(cancel)
                    negBtn?.let { btn ->
                        btn.setOnClickListener {
                            returnObj.put(
                                App.INSTANCE.getString(R.string.obj_key_msg),
                                dialogKeys[2]
                            )
                            Utils.LOGD(
                                "returnObj['msg'] = " +
                                        "${returnObj.getString(App.INSTANCE.getString(R.string.obj_key_msg))}"
                            )
                            dialogAction?.promiseReturn(returnObj)
                            dialog.dismiss()
                        }
                    }

                    val exitListener = {
                        returnObj.put(
                            App.INSTANCE.getString(R.string.obj_key_msg),
                            dialogKeys[2]
                        )
                        Utils.LOGD(
                            "returnObj['msg'] = " +
                                    "${returnObj.getString(App.INSTANCE.getString(R.string.obj_key_msg))}"
                        )
                        dialogAction?.promiseReturn(returnObj)
                    }

                    val btnList = arrayListOf(posBtn, neutralBtn, negBtn)
                    val dialogLayout = Dialog.getBottomSheetDialogView(title, contents, btnList)
                    Dialog.bottomSheetshow(dialog, dialogLayout, exitListener)
                }
            }
        }
    }

    /**================================== Network Action =========================================*/
    val network = FlexLambda.map {
        withContext(Main) {
            val returnMap = HashMap<String, Any>()
            when(Network.getStatus(App.activity)) {
                Constants.NET_STAT_CELLULAR -> {
                    returnMap[App.INSTANCE.getString(R.string.obj_key_data)] = Constants.NET_STAT_CELLULAR
                    returnMap[App.INSTANCE.getString(R.string.obj_key_msg)] = App.INSTANCE.getString(R.string.msg_cellular)
                }
                Constants.NET_STAT_WIFI -> {
                    returnMap[App.INSTANCE.getString(R.string.obj_key_data)] = Constants.NET_STAT_WIFI
                    returnMap[App.INSTANCE.getString(R.string.obj_key_msg)] = App.INSTANCE.getString(R.string.msg_wifi)
                }
                else -> {
                    returnMap[App.INSTANCE.getString(R.string.obj_key_data)] = Constants.NET_STAT_DISCONNECTED
                    returnMap[App.INSTANCE.getString(R.string.obj_key_msg)] = App.INSTANCE.getString(R.string.msg_disconnected)
                }
            }
            returnMap
        }
    }

//    val d: Deferred<Map<String, *>> = CoroutineScope(Dispatchers.IO).async {
//        HashMap<String, Any>()
//    }

//    val network2: suspend CoroutineScope.(Array<FlexData>) -> Map<String, *> = {
//        withContext(Dispatchers.Main) {
//            val returnObj = HashMap<String, Any>()
//            val b = async {
//                val c = async {
//                    returnObj
//                }
//                c.await()
//            }
//            d.await()
////            when(Network.getStatus(App.activity)) {
////                Constants.NET_STAT_CELLULAR -> {
////                    returnObj[App.INSTANCE.getString(R.string.obj_key_data)] = Constants.NET_STAT_CELLULAR
////                    returnObj[App.INSTANCE.getString(R.string.obj_key_msg)] = App.INSTANCE.getString(R.string.msg_cellular)
////                    returnObj
////                }
////                Constants.NET_STAT_WIFI -> {
////                    returnObj[App.INSTANCE.getString(R.string.obj_key_data)] = Constants.NET_STAT_WIFI
////                    returnObj[App.INSTANCE.getString(R.string.obj_key_msg)] = App.INSTANCE.getString(R.string.msg_wifi)
////                    returnObj
////                }
////                else -> {
////                    returnObj[App.INSTANCE.getString(R.string.obj_key_data)] = Constants.NET_STAT_DISCONNECTED
////                    returnObj[App.INSTANCE.getString(R.string.obj_key_msg)] = App.INSTANCE.getString(R.string.msg_disconnected)
////                    returnObj
////                }
////            }
//        }
//    }

    /**================================= QR Code Action ==========================================*/
    val qrCode = FlexLambda.action { qrCodeAction, _->
        withContext(Main) {
            basicActivity.qrCodeScanAction = qrCodeAction

            if(Utils.existAllPermission(arrayOf(Constants.PERM_CAMERA))) {
                basicActivity.qrCode!!.startScan()
            } else {
                basicActivity.qrCode!!.requestPermissionResult.launch(Manifest.permission.CAMERA)
            }
        }
    }

    /**================================ Photo Device Action ======================================*/
    val photoByDeviceRatio = FlexLambda.action { photoDeviceAction, array ->
        withContext(Main) {
            basicActivity.photoDeviceAction = photoDeviceAction
            basicActivity.ratio = array[0].reified()
            basicActivity.isWidthRatio = array[1].reified()

            if(Utils.existAllPermission(arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                    Constants.PERM_READ_EXTERNAL_STORAGE))) {
                basicActivity.photo!!.takeSingleImage()
            } else {
                basicActivity.photo!!.requestPermissionResultByDeviceSingle.launch()
            }
        }
    }

    /**==================================== Photo Action =========================================*/
    val photoByRatio = FlexLambda.action { photoAction, array ->
        withContext(Main) {
            basicActivity.photoAction = photoAction
            basicActivity.ratio = array[0].reified()

            if(Utils.existAllPermission(arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                    Constants.PERM_READ_EXTERNAL_STORAGE))) {
                basicActivity.photo!!.takeSingleImage()
            } else {
                basicActivity.photo!!.requestPermissionResultSingle.launch()
            }
        }
    }

    /**=============================== Multi Photo Device Action =================================*/
    val multiPhotoByDeviceRatio = FlexLambda.action { multiplePhotoDeviceAction, array ->
        withContext(Main) {
            basicActivity.multiplePhotoDeviceAction = multiplePhotoDeviceAction
            basicActivity.ratio = array[0].reified()
            basicActivity.isWidthRatio = array[1].reified()

            if(Utils.existAllPermission(arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                    Constants.PERM_READ_EXTERNAL_STORAGE))) {
                basicActivity.photo!!.takeMultipleImages()
            } else {
                basicActivity.photo!!.requestPermissionResultByDeviceMulti.launch()
            }
        }
    }

    /**================================== Multi Photo Action =====================================*/
    val multiPhotoByRatio = FlexLambda.action { multiplePhotoAction, array ->
        withContext(Main) {
            basicActivity.multiplePhotoAction = multiplePhotoAction
            basicActivity.ratio = array[0].reified()

            if(Utils.existAllPermission(arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                    Constants.PERM_READ_EXTERNAL_STORAGE))) {
                basicActivity.photo!!.takeMultipleImages()
            } else {
                basicActivity.photo!!.requestPermissionResultMulti.launch()
            }
        }
    }

    /**=================================== Camera Action =========================================*/
    val cameraByDeviceRatio = FlexLambda.action { cameraDeviceAction, array ->
        withContext(Main) {
            basicActivity.cameraDeviceAction = cameraDeviceAction
            basicActivity.ratio = array[0].reified()
            basicActivity.isWidthRatio = array[1].reified()

            if(Utils.existAllPermission(arrayOf(Constants.PERM_CAMERA))) {
                basicActivity.camera!!.request()
            } else {
                basicActivity.camera!!.requestPermissionResultByDevice.launch()
            }
        }
    }

    val cameraByRatio = FlexLambda.action{ cameraAction, array ->
        withContext(Main) {
            basicActivity.cameraAction = cameraAction
            basicActivity.ratio = array[0].reified()

            if(Utils.existAllPermission(arrayOf(Constants.PERM_CAMERA))) {
                basicActivity.camera!!.request()
            } else {
                basicActivity.camera!!.requestPermission.launch()
            }
        }
    }

    /**=================================== Location Action =======================================*/
    val location = FlexLambda.action { locationAction, _->
        withContext(Main) {
            basicActivity.locationAction = locationAction

            if(Utils.existAllPermission(arrayOf(Constants.PERM_ACCESS_FINE_LOCATION,
                    Constants.PERM_ACCESS_COARSE_LOCATION))) {
                basicActivity.location!!.getCurrentLatAndLot()
            } else {
                basicActivity.location!!.requestPermissionResult.launch()
            }
        }
    }

    /**================================= SEND SMS Action =========================================*/
    val sendSms = FlexLambda.action { sendSmsAction, array ->
        withContext(Main) {
            basicActivity.sendSmsAction = sendSmsAction
            basicActivity.phoneNumber = array[0].reified()
            basicActivity.smsMessage = array[1].reified()

            if(Utils.existAllPermission(arrayOf(Constants.PERM_SEND_SMS))) {
                basicActivity.sms!!.sendMessage()
            } else {
                basicActivity.sms!!.requestPermissionResult.launch()
            }
        }
    }

    /**=============================== Authentication Action =====================================*/
    @RequiresApi(Build.VERSION_CODES.P)
    val authentication: suspend CoroutineScope.(FlexAction, Array<FlexData>) -> Unit = { authAction, _->
        CoroutineScope(Dispatchers.Main).launch {
            Utils.LOGD("============== Authentication Action ==============")

            val fragmentActivity = App.activity as FragmentActivity
            val basicActivity = App.activity as BasicActivity
            basicActivity.authAction = authAction

            if(Authentication.canAuthenticate()) {
                Authentication.showPrompt(fragmentActivity)
            } else {
                Utils.LOGE("You can't call biometric prompt.")
                val returnObj = Utils.createJSONObject(
                    authValue = true,
                    dataValue = false, msgValue = "인증을 진행할 수 없습니다"
                )
                basicActivity.authAction?.promiseReturn(returnObj)
            }
        }
    }

    val webPopUp: suspend CoroutineScope.(FlexAction, Array<FlexData>) -> Unit = { popUpAction, array ->
        Utils.LOGD("============== Web PopUp Action ==============")

        val url = array[0].asString()!!
        val ratio = array[1].asDouble()

        val basicActivity = App.activity as BasicActivity
        basicActivity.popUpAction = popUpAction

        // 인터넷이 연결되어 있지 않을 경우
        if(Network.getStatus(App.activity) == 0) {
            val returnObj = JSONObject()
            returnObj.put(App.INSTANCE.getString(R.string.obj_key_data), false)
            returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), "연결된 네트워크가 없습니다")

            basicActivity.popUpAction?.promiseReturn(returnObj)
        }

        CoroutineScope(Dispatchers.Main).launch {
            // 뒷배경 뷰 생성
            val mInflater = Utils.getLayoutInflater(App.activity)
            basicActivity.backgroundView = mInflater.inflate(R.layout.background_popup, null)
            App.activity.constraintLayout.addView(basicActivity.backgroundView)

            val screenSize = Utils.getScreenSize()
            val popupWidth = (ratio?.times(
                    screenSize.getValue(App.INSTANCE.getString(R.string.screen_width))))?.toInt()
            val popupHeight = (ratio?.times(
                    screenSize.getValue(App.INSTANCE.getString(R.string.screen_height))))?.toInt()

            App.activity.flex_pop_up_web_view.loadUrl(url)
            App.activity.flex_pop_up_web_view.webViewClient = object: FlexWebViewClient() {
                override fun onReceivedError(view: WebView?, request: WebResourceRequest?,
                                             error: WebResourceError?) {
                    super.onReceivedError(view, request, error)
                    Utils.LOGD("onReceivedError")

                    val returnObj = JSONObject()
                    returnObj.put(App.INSTANCE.getString(R.string.obj_key_data), false)
                    returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), "해당 URL을 불러올 수 없습니다")
                    basicActivity.popUpAction?.promiseReturn(returnObj)
                    basicActivity.popUpAction = null
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    val returnObj = JSONObject()
                    returnObj.put(App.INSTANCE.getString(R.string.obj_key_data), true)
                    basicActivity.popUpAction?.promiseReturn(returnObj)
                }
            }

            App.activity.flex_pop_up_web_view.visibility = View.VISIBLE
            App.activity.flex_pop_up_web_view.layoutParams = Utils.getParamsAlignCenterInConstraintLayout(
                popupWidth!!, popupHeight!!, R.id.constraintLayout)

            val bottomUp = AnimationUtils.loadAnimation(App.activity,
                R.anim.open)
            App.activity.flex_pop_up_web_view.startAnimation(bottomUp)
            App.activity.flex_pop_up_web_view.bringToFront()

            // 닫기 버튼 생성
            basicActivity.popUpCloseButton = Utils.createCloseButton(App.activity,
                R.id.constraintLayout)
            App.activity.constraintLayout.addView(basicActivity.popUpCloseButton)

            basicActivity.popUpCloseButton.setOnClickListener {
                Utils.closePopup(App.activity,  App.activity.constraintLayout, basicActivity.backgroundView,
                    basicActivity.popUpCloseButton,  App.activity.flex_pop_up_web_view)
            }
        }
    }

    /**================================ FileDownload Action ======================================*/
    val fileDownload = FlexLambda.action { fileAction, array ->
        basicActivity.fileAction = fileAction
        basicActivity.fileUrl = array[0].reified()

        if(Utils.existAllPermission(arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE))) {
            Utils.downloadFileFromUrl()
        }  else {
            requestPermissionResultForFileDownload.launch()
        }
    }

    private val requestPermissionResultForFileDownload = basicActivity.registerForActivityResult(
    ActivityResultContracts.RequestPermission(), Manifest.permission.WRITE_EXTERNAL_STORAGE) { isGranted ->
        if(isGranted) {
            Utils.downloadFileFromUrl()
        } else {
            // fileAction?.promiseReturn(deniedObj)
        }
    }

    /**============================ Local Repository Action ======================================*/
    val localRepository: suspend CoroutineScope.(FlexAction, Array<FlexData>) -> Unit = { localRepoAction, array ->
        Utils.LOGD("============== Local Repository Action ==============")

        when (array[0].asInt()) {
            Constants.PUT_DATA_CODE -> {
                val key = array[1].asString()!!
                val value = array[2].asString()

                SharedPreferences.putData(App.INSTANCE.getString(R.string.shared_file_name), key, value)

                localRepoAction?.promiseReturn(true)
            }
            Constants.GET_DATA_CODE -> {
                val key = array[1].asString()!!
                var value = SharedPreferences.get(App.INSTANCE.getString(R.string.shared_file_name),
                    key, App.INSTANCE.getString(R.string.shared_default_string))

                localRepoAction?.promiseReturn(value)
            }
            Constants.DEL_DATA_CODE -> {
                val key = array[1].asString()!!
                SharedPreferences.removeData(App.INSTANCE.getString(R.string.shared_file_name), key)
                localRepoAction?.promiseReturn(true)
            }
        }
    }

//    val a: suspend CoroutineScope.(Array<FlexData>) -> Int = {
//        async {
//            {
//
//            }.let {
//                return@async 1
//            }
//        }.await()
//    }
}