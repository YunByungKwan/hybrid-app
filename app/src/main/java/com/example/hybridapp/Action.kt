package com.example.hybridapp

import android.content.DialogInterface
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import app.dvkyun.flexhybridand.FlexAction
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.example.hybridapp.util.module.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

object Action {

    /**================================= Dialog Action ===========================================*/
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    val dialog: (FlexAction?, JSONArray?) -> Unit = { dialogAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            val title = array?.getString(0)
            val contents = array?.getString(1)
            val jsonObject: JSONObject? = array?.get(2) as JSONObject
            val isDialog = array.getBoolean(3)

            jsonObject?.let {
                val dialogKeys = arrayOf("basic", "destructive", "cancel")
                val basic: String? = Utils.getJsonObjectValue(dialogKeys[0], it)
                val destructive: String? = Utils.getJsonObjectValue(dialogKeys[1], it)
                val cancel: String? = Utils.getJsonObjectValue(dialogKeys[2], it)

                if(isDialog) {
                    val posListener = DialogInterface.OnClickListener { _, _ ->
                        basic?.let { dialogAction?.promiseReturn(dialogKeys[0]) }
                    }
                    val neutralListener = DialogInterface.OnClickListener { _, _ ->
                        destructive?.let { dialogAction?.promiseReturn(dialogKeys[1]) }
                    }
                    val negListener = DialogInterface.OnClickListener { _, _ ->
                        cancel?.let { dialogAction?.promiseReturn(dialogKeys[2]) }
                    }
                    val exitListener = {
                        dialogAction?.promiseReturn(Constants.RESULT_CANCELED)
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
                        dialogAction?.promiseReturn(dialogKeys[0])
                        dialog.dismiss()
                        }
                    }

                    var neutralBtn = Dialog.getBtnView(destructive)
                    neutralBtn?.let { btn ->
                        btn.setOnClickListener {
                            dialogAction?.promiseReturn(dialogKeys[1])
                            dialog.dismiss()
                        }
                    }

                    var negBtn = Dialog.getBtnView(cancel)
                    negBtn?.let { btn ->
                        btn.setOnClickListener {
                        dialogAction?.promiseReturn(dialogKeys[2])
                        dialog.dismiss()
                        }
                    }

                    val exitListener = {
                        dialogAction?.promiseReturn(Constants.RESULT_CANCELED)
                    }

                    val btnList = arrayListOf(posBtn, neutralBtn, negBtn)
                    val dialogLayout = Dialog.getBottomSheetDialogView(title, contents, btnList)
                    Dialog.bottomSheetshow(dialog, dialogLayout, exitListener)
                }
            }
        }
    }

    /**================================== Network Action =========================================*/
    val network: (FlexAction?, JSONArray?) -> Unit = { networkAction, _ ->
        CoroutineScope(Dispatchers.Main).launch {
            val returnObj = JSONObject()
            when(Network.getStatus(App.activity)) {
                Constants.NET_STAT_CELLULAR -> {
                    returnObj.put(Constants.OBJ_KEY_DATA, Constants.NET_STAT_CELLULAR)
                    returnObj.put(Constants.OBJ_KEY_MSG, Constants.MSG_CELLULAR)
                    networkAction?.promiseReturn(returnObj)
                }
                Constants.NET_STAT_WIFI -> {
                    returnObj.put(Constants.OBJ_KEY_DATA, Constants.NET_STAT_WIFI)
                    returnObj.put(Constants.OBJ_KEY_MSG, Constants.MSG_WIFI)
                    networkAction?.promiseReturn(returnObj)
                }
                else -> {
                    returnObj.put(Constants.OBJ_KEY_DATA, Constants.NET_STAT_DISCONNECTED)
                    returnObj.put(Constants.OBJ_KEY_MSG, Constants.MSG_DISCONNECTED)
                    networkAction?.promiseReturn(returnObj)
                }
            }
        }
    }

    /**================================= QR Code Action ==========================================*/
    val qrCode: (FlexAction?, JSONArray?) -> Unit = { qrCodeAction, _->
        Constants.LOGD("============== QR Code Action ==============")

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
    val photoByDeviceRatio: (FlexAction?, JSONArray?) -> Unit = { photoDeviceAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            Constants.LOGD("============== Photo By Device Ratio Action ==============")

            val basicActivity = App.activity as BasicActivity
            basicActivity.photoDeviceAction = photoDeviceAction
            basicActivity.ratio = array?.getDouble(0)
            basicActivity.isWidthRatio = array?.getBoolean(1)

            Constants.LOGD("basicActivity.ratio : ${basicActivity.ratio}, " +
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
    val photoByRatio: (FlexAction?, JSONArray?) -> Unit = { photoAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            Constants.LOGD("============== Photo By Ratio Action ==============")

            val basicActivity = App.activity as BasicActivity
            basicActivity.photoAction = photoAction
            basicActivity.ratio = array?.getDouble(0)

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
    val multiPhotoByDeviceRatio: (FlexAction?, JSONArray?) -> Unit = { multiplePhotoDeviceAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            Constants.LOGD("============== Multi Photo By Device Ratio Action ==============")

            val basicActivity = App.activity as BasicActivity
            basicActivity.multiplePhotoDeviceAction = multiplePhotoDeviceAction
            basicActivity.ratio = array?.getDouble(0)
            basicActivity.isWidthRatio = array?.getBoolean(1)

            Constants.LOGD("basicActivity.ratio : ${basicActivity.ratio}, " +
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
    val multiPhotoByRatio: (FlexAction?, JSONArray?) -> Unit = { multiplePhotoAction, array ->
        CoroutineScope(Dispatchers.Main).launch {

            val basicActivity = App.activity as BasicActivity
            basicActivity.multiplePhotoAction = multiplePhotoAction
            basicActivity.ratio = array?.getDouble(0)

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
    val cameraByDeviceRatio: (FlexAction?, JSONArray?) -> Unit = { cameraDeviceAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            Constants.LOGD("============== Camera By Device Ratio Action ==============")

            val basicActivity = App.activity as BasicActivity
            basicActivity.cameraDeviceAction = cameraDeviceAction
            basicActivity.ratio = array?.getDouble(0)
            basicActivity.isWidthRatio = array?.getBoolean(1)

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

    val cameraByRatio: (FlexAction?, JSONArray?) -> Unit = { cameraAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            Constants.LOGD("============== Camera By Ratio Action ==============")

            val basicActivity = App.activity as BasicActivity
            basicActivity.cameraAction = cameraAction
            basicActivity.ratio = array?.getDouble(0)

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
    val location: (FlexAction?, JSONArray?) -> Unit = { locationAction, _->
        CoroutineScope(Dispatchers.Main).launch {
            Constants.LOGD("============== Location Action ==============")

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
    val sendSms: (FlexAction?, JSONArray?) -> Unit = { sendSmsAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            Constants.LOGD("============== Send SMS Action ==============")

            // inject action
            val basicActivity = App.activity as BasicActivity
            basicActivity.sendSmsAction = sendSmsAction
            basicActivity.phoneNumber = array?.getString(0)
            basicActivity.smsMessage = array?.getString(1)

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
    val authentication: (FlexAction?, JSONArray?) -> Unit = { authAction, _->
        CoroutineScope(Dispatchers.Main).launch {
            Constants.LOGD("============== Authentication Action ==============")

            val fragmentActivity = App.activity as FragmentActivity
            val basicActivity = App.activity as BasicActivity
            basicActivity.authAction = authAction

            if(Authentication.canAuthenticate()) {
                Authentication.showPrompt(fragmentActivity)
            } else {
                Constants.LOGE("You can't call biometric prompt.")
                val returnObj = Utils.createJSONObject(true,
                    false, "인증을 진행할 수 없습니다")
                basicActivity.authAction?.promiseReturn(returnObj)
            }
        }
    }

    /**============================ Local Repository Action ======================================*/
    val localRepository: (FlexAction?, JSONArray?) -> Unit = { localRepoAction, array ->
        Constants.LOGD("============== Local Repository Action ==============")

        when (array!!.getInt(0)) {
            Constants.PUT_DATA_CODE -> {
                val key = array.getString(1)
                val value = array.getString(2)

                SharedPreferences.putData(Constants.SHARED_FILE_NAME, key, value)

                val returnObj = Utils.createJSONObject(null,
                    null, "데이터를 저장하였습니다")
                localRepoAction?.promiseReturn(returnObj)
            }
            Constants.GET_DATA_CODE -> {
                val key = array.getString(1)
                var value = SharedPreferences.getString(Constants.SHARED_FILE_NAME, key)

                val returnObj = Utils.createJSONObject(null,
                    value, null)
                localRepoAction?.promiseReturn(returnObj)
            }
            Constants.DEL_DATA_CODE -> {
                val key = array.getString(1)
                SharedPreferences.removeData(Constants.SHARED_FILE_NAME, key)
                localRepoAction?.promiseReturn("데이터를 제거하였습니다")
            }
        }
    }
}