package com.example.hybridapp

import android.content.DialogInterface
import android.os.Build
import android.view.View
import android.view.animation.AnimationUtils
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import app.dvkyun.flexhybridand.FlexAction
import app.dvkyun.flexhybridand.FlexData
import app.dvkyun.flexhybridand.FlexWebViewClient
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.example.hybridapp.util.module.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

object Action {

    /**================================= Dialog Action ===========================================*/
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    val dialog: (FlexAction?, Array<FlexData>) -> Unit = { dialogAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
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

                if(isDialog!!) {
                    val posListener = DialogInterface.OnClickListener { _, _ ->
                        basic?.let {
                            returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), dialogKeys[0])
                            Utils.LOGD("returnObj['msg'] = " +
                                    returnObj.getString(App.INSTANCE.getString(R.string.obj_key_msg))
                            )
                            dialogAction?.promiseReturn(returnObj)
                        }
                    }
                    val neutralListener = DialogInterface.OnClickListener { _, _ ->
                        destructive?.let {
                            returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), dialogKeys[1])
                            Utils.LOGD("returnObj['msg'] = " +
                                    returnObj.getString(App.INSTANCE.getString(R.string.obj_key_msg))
                            )
                            dialogAction?.promiseReturn(returnObj) }
                    }
                    val negListener = DialogInterface.OnClickListener { _, _ ->
                        cancel?.let {
                            returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), dialogKeys[2])
                            Utils.LOGD("returnObj['msg'] = " +
                                    returnObj.getString(App.INSTANCE.getString(R.string.obj_key_msg))
                            )
                            dialogAction?.promiseReturn(returnObj)
                        }
                    }
                    val exitListener = {
                        returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), dialogKeys[2])
                        Utils.LOGD("returnObj['msg'] = " +
                                returnObj.getString(App.INSTANCE.getString(R.string.obj_key_msg))
                        )
                        dialogAction?.promiseReturn(returnObj)
                    }

                    Dialog.show(
                        title, contents, basic, destructive, cancel,
                        posListener, neutralListener, negListener, exitListener
                    )
                }
                else {  // Bottom Dialog (Bottom Sheet Dialog)
                    val dialog = BottomSheetDialog(App.activity)

                    var posBtn = Dialog.getBtnView(basic)
                    posBtn?.let { btn ->
                        btn.setOnClickListener {
                            returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), dialogKeys[0])
                            Utils.LOGD("returnObj['msg'] = " +
                                    "${returnObj.getString(App.INSTANCE.getString(R.string.obj_key_msg))}")
                            dialogAction?.promiseReturn(returnObj)
                            dialog.dismiss()
                        }
                    }

                    var neutralBtn = Dialog.getBtnView(destructive)
                    neutralBtn?.let { btn ->
                        btn.setOnClickListener {
                            returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), dialogKeys[1])
                            Utils.LOGD("returnObj['msg'] = " +
                                    "${returnObj.getString(App.INSTANCE.getString(R.string.obj_key_msg))}")
                            dialogAction?.promiseReturn(returnObj)
                            dialog.dismiss()
                        }
                    }

                    var negBtn = Dialog.getBtnView(cancel)
                    negBtn?.let { btn ->
                        btn.setOnClickListener {
                            returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), dialogKeys[2])
                            Utils.LOGD("returnObj['msg'] = " +
                                    "${returnObj.getString(App.INSTANCE.getString(R.string.obj_key_msg))}")
                            dialogAction?.promiseReturn(returnObj)
                            dialog.dismiss()
                        }
                    }

                    val exitListener = {
                        returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), dialogKeys[2])
                        Utils.LOGD("returnObj['msg'] = " +
                                "${returnObj.getString(App.INSTANCE.getString(R.string.obj_key_msg))}")
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
    val network: (FlexAction?, Array<FlexData>) -> Unit = { networkAction, _ ->
        CoroutineScope(Dispatchers.Main).launch {
            val returnObj = JSONObject()
            when(Network.getStatus(App.activity)) {
                Constants.NET_STAT_CELLULAR -> {
                    returnObj.put(App.INSTANCE.getString(R.string.obj_key_data), Constants.NET_STAT_CELLULAR)
                    returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), App.INSTANCE.getString(R.string.msg_cellular))
                    networkAction?.promiseReturn(returnObj)
                }
                Constants.NET_STAT_WIFI -> {
                    returnObj.put(App.INSTANCE.getString(R.string.obj_key_data), Constants.NET_STAT_WIFI)
                    returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), App.INSTANCE.getString(R.string.msg_wifi))
                    networkAction?.promiseReturn(returnObj)
                }
                else -> {
                    returnObj.put(App.INSTANCE.getString(R.string.obj_key_data), Constants.NET_STAT_DISCONNECTED)
                    returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), App.INSTANCE.getString(R.string.msg_disconnected))
                    networkAction?.promiseReturn(returnObj)
                }
            }
        }
    }

    /**================================= QR Code Action ==========================================*/
    val qrCode: (FlexAction?,  Array<FlexData>) -> Unit = { qrCodeAction, _->
        Utils.LOGD("============== QR Code Action ==============")

        val basicActivity = App.activity as BasicActivity
        basicActivity.qrCodeScanAction = qrCodeAction

        val perms = arrayOf(Constants.PERM_CAMERA)

        // 권한이 다 있을 경우
        if(Utils.existAllPermission(perms)) {
            QRCode.startScan()
        }
        // 권한이 다 있지 않을 경우
        else {
            Utils.checkAbsentPerms(perms, Constants.PERM_QR_REQ_CODE,
                basicActivity.qrCodeScanAction)
        }
    }

    /**================================ Photo Device Action ======================================*/
    val photoByDeviceRatio: (FlexAction?, Array<FlexData>) -> Unit = { photoDeviceAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            Utils.LOGD("============== Photo By Device Ratio Action ==============")

            val basicActivity = App.activity as BasicActivity
            basicActivity.photoDeviceAction = photoDeviceAction
            basicActivity.ratio = array[0].reified()
            basicActivity.isWidthRatio = array[1].reified()

            Utils.LOGD("basicActivity.ratio : ${basicActivity.ratio}, " +
                    "basicActivity.isWidthRatio : ${basicActivity.isWidthRatio}")

            val perms = arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                Constants.PERM_READ_EXTERNAL_STORAGE)

            // 권한이 다 있을 경우
            if(Utils.existAllPermission(perms)) {
                Photo.requestImage()
            }
            // 권한이 다 있지 않을 경우
            else {
                Utils.checkAbsentPerms(perms, Constants.PERM_PHOTO_DEVICE_REQ_CODE,
                    basicActivity.photoDeviceAction)
            }
        }
    }

    /**==================================== Photo Action =========================================*/
    val photoByRatio: (FlexAction?, Array<FlexData>) -> Unit = { photoAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            Utils.LOGD("============== Photo By Ratio Action ==============")

            val basicActivity = App.activity as BasicActivity
            basicActivity.photoAction = photoAction
            basicActivity.ratio = array[0].reified()

            val perms = arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                Constants.PERM_READ_EXTERNAL_STORAGE)

            // 권한이 다 있을 경우
            if(Utils.existAllPermission(perms)) {
                Photo.requestImage()
            }
            // 권한이 다 있지 않을 경우
            else {
                Utils.checkAbsentPerms(perms, Constants.PERM_PHOTO_REQ_CODE,
                    basicActivity.photoAction)
            }
        }
    }

    /**=============================== Multi Photo Device Action =================================*/
    val multiPhotoByDeviceRatio: (FlexAction?, Array<FlexData>) -> Unit = { multiplePhotoDeviceAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            Utils.LOGD("============== Multi Photo By Device Ratio Action ==============")

            val basicActivity = App.activity as BasicActivity
            basicActivity.multiplePhotoDeviceAction = multiplePhotoDeviceAction
            basicActivity.ratio = array[0].reified()
            basicActivity.isWidthRatio = array[1].reified()

            Utils.LOGD("basicActivity.ratio : ${basicActivity.ratio}, " +
                    "basicActivity.isWidthRatio : ${basicActivity.isWidthRatio}")

            val perms = arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                Constants.PERM_READ_EXTERNAL_STORAGE)

            // 권한이 다 있을 경우
            if(Utils.existAllPermission(perms)) {
                Photo.requestMultipleImages()
            }
            // 권한이 다 있지 않을 경우
            else {
                Utils.checkAbsentPerms(perms, Constants.PERM_MUL_PHOTO_DEVICE_REQ_CODE,
                    basicActivity.multiplePhotoDeviceAction)
            }
        }
    }

    /**================================== Multi Photo Action =====================================*/
    val multiPhotoByRatio: (FlexAction?,  Array<FlexData>) -> Unit = { multiplePhotoAction, array ->
        CoroutineScope(Dispatchers.Main).launch {

            val basicActivity = App.activity as BasicActivity
            basicActivity.multiplePhotoAction = multiplePhotoAction
            basicActivity.ratio = array[0].reified()

            val perms =
                arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE, Constants.PERM_READ_EXTERNAL_STORAGE)

            // 권한이 다 있을 경우
            if(Utils.existAllPermission(perms)) {
                Photo.requestMultipleImages()
            }
            // 권한이 다 있지 않을 경우
            else {
                Utils.checkAbsentPerms(perms, Constants.PERM_MUL_PHOTO_REQ_CODE,
                    basicActivity.multiplePhotoAction)
            }
        }
    }

    /**=================================== Camera Action =========================================*/
    val cameraByDeviceRatio: (FlexAction?, Array<FlexData>) -> Unit = { cameraDeviceAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            Utils.LOGD("============== Camera By Device Ratio Action ==============")

            val basicActivity = App.activity as BasicActivity
            basicActivity.cameraDeviceAction = cameraDeviceAction
            basicActivity.ratio = array[0].reified()
            basicActivity.isWidthRatio = array[1].reified()

            val perms = arrayOf(Constants.PERM_CAMERA)

            // 권한이 다 있을 경우
            if(Utils.existAllPermission(perms)) {
                Camera.request()
            }
            // 권한이 다 있지 않을 경우
            else {
                Utils.checkAbsentPerms(perms, Constants.PERM_CAMERA_DEVICE_REQ_CODE,
                    basicActivity.cameraDeviceAction)
            }
        }
    }

    val cameraByRatio: (FlexAction?, Array<FlexData>) -> Unit = { cameraAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            Utils.LOGD("============== Camera By Ratio Action ==============")

            val basicActivity = App.activity as BasicActivity
            basicActivity.cameraAction = cameraAction
            basicActivity.ratio = array[0].reified()

            val perms = arrayOf(Constants.PERM_CAMERA)

            // 권한이 다 있을 경우
            if(Utils.existAllPermission(perms)) {
                Camera.request()
            }
            // 권한이 다 있지 않을 경우
            else {
                Utils.checkAbsentPerms(perms, Constants.PERM_CAMERA_REQ_CODE,
                    basicActivity.cameraAction)
            }
        }
    }

    /**=================================== Location Action =======================================*/
    val location: (FlexAction?, Array<FlexData>) -> Unit = { locationAction, _->
        CoroutineScope(Dispatchers.Main).launch {
            Utils.LOGD("============== Location Action ==============")

            // inject action
            val basicActivity = App.activity as BasicActivity
            basicActivity.locationAction = locationAction

            val perms = arrayOf(Constants.PERM_ACCESS_FINE_LOCATION,
                Constants.PERM_ACCESS_COARSE_LOCATION)

            // 권한이 다 있을 경우
            if(Utils.existAllPermission(perms)) {
                Location.getCurrentLatAndLot()
            }
            // 권한이 다 있지 않을 경우
            else {
                Utils.checkAbsentPerms(perms, Constants.PERM_LOCATION_REQ_CODE,
                    basicActivity.locationAction)
            }
        }
    }

    /**================================= SEND SMS Action =========================================*/
    val sendSms: (FlexAction?, Array<FlexData>) -> Unit = { sendSmsAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            Utils.LOGD("============== Send SMS Action ==============")

            // inject action
            val basicActivity = App.activity as BasicActivity
            basicActivity.sendSmsAction = sendSmsAction
            basicActivity.phoneNumber = array[0].reified()
            basicActivity.smsMessage = array[1].reified()

            val perms = arrayOf(Constants.PERM_SEND_SMS)

            // 권한이 다 있을 경우
            if(Utils.existAllPermission(perms)) {
                SMS.sendMessage()
            }
            // 권한이 다 있지 않을 경우
            else {
                Utils.checkAbsentPerms(perms, Constants.PERM_SEND_SMS_REQ_CODE,
                    basicActivity.sendSmsAction)
            }
        }
    }

    /**=============================== Authentication Action =====================================*/
    @RequiresApi(Build.VERSION_CODES.P)
    val authentication: (FlexAction?, Array<FlexData>) -> Unit = { authAction, _->
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

    val webPopUp: (FlexAction?, Array<FlexData>) -> Unit = { popUpAction, array ->
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
    val fileDownload: (FlexAction?, Array<FlexData>) -> Unit = { fileAction, array ->
        Utils.LOGD("============== File Download Action ==============")

        val basicActivity = App.activity as BasicActivity
        basicActivity.fileAction = fileAction
        basicActivity.fileUrl = array[0].reified()

        val perms = arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE)

        // 권한이 다 있을 경우
        if(Utils.existAllPermission(perms)) {
            Utils.downloadFileFromUrl()
        }
        // 권한이 다 있지 않을 경우
        else {
            Utils.checkAbsentPerms(perms, Constants.PERM_FILE_REQ_CODE,
                basicActivity.fileAction)
        }
    }

    /**============================ Local Repository Action ======================================*/
    val localRepository: (FlexAction?, Array<FlexData>) -> Unit = { localRepoAction, array ->
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
}