package com.example.hybridapp

import android.content.DialogInterface
import androidx.fragment.app.FragmentActivity
import app.dvkyun.flexhybridand.FlexAction
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.example.hybridapp.util.module.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

object Action {

    /**================================= Dialog Action ===========================================*/
    val dialog: (FlexAction?, JSONArray?) -> Unit = { dialogAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            val title = array?.getString(0)
            val contents = array?.getString(1)
            val jsonObject: JSONObject? = array?.get(2) as JSONObject
            val isDialog = array.getBoolean(3)

            if(isDialog) { // Dialog 호출
                jsonObject?.let {
                    val basic: String? = Utils.getJsonObjectValue("basic", it)
                    val destructive: String? = Utils.getJsonObjectValue("destructive", it)
                    val cancel: String? = Utils.getJsonObjectValue("cancel", it)

                    val posListener = DialogInterface.OnClickListener { _, _ ->
                        if (basic != null) {
                            dialogAction?.promiseReturn(basic)
                        } else {
                            dialogAction?.resolveVoid()
                        }
                    }
                    val negListener = DialogInterface.OnClickListener { _, _ ->
                        if (cancel != null) {
                            dialogAction?.promiseReturn(cancel)
                        } else {
                            dialogAction?.resolveVoid()
                        }
                    }
                    val cancelListener = {
                        dialogAction?.promiseReturn(Constants.RESULT_CANCELED)
                    }

                    Dialog.show(
                        title, contents, basic, destructive, cancel,
                        posListener, null, negListener, cancelListener
                    )
                }
            } else { // Bottom Dialog 호출
                Snackbar.showShortText(App.activity.findViewById(R.id.constraintLayout), contents!!)
            }
        }
    }

    /**================================== Network Action =========================================*/
    val network: (FlexAction?, JSONArray?) -> Unit = { networkAction, _ ->
        CoroutineScope(Dispatchers.Main).launch {
            val returnObj = JSONObject()
            when(Network.getStatus(App.activity)) {
                Constants.NET_STAT_CELLULAR -> {
                    returnObj.put(Constants.OBJ_KEY_MSG, Constants.MSG_CELLULAR)
                    networkAction?.promiseReturn(returnObj)
                }
                Constants.NET_STAT_WIFI -> {
                    returnObj.put(Constants.OBJ_KEY_MSG, Constants.MSG_WIFI)
                    networkAction?.promiseReturn(returnObj)
                }
                else -> {
                    returnObj.put(Constants.OBJ_KEY_MSG, Constants.MSG_DISCONNECTED)
                    networkAction?.promiseReturn(returnObj)
                }
            }
        }
    }

    /**================================= QR Code Action ==========================================*/
    val qrCode: (FlexAction?, JSONArray?) -> Unit = { qrCodeAction, _->
        Constants.LOGD("============== QR Code Action ==============")

        // inject action
        val basicActivity = App.activity as BasicActivity
        basicActivity.qrCodeScanAction = qrCodeAction

        // 권한이 다 있을 경우
        if(Utils.existAllPermission(arrayOf(Constants.PERM_CAMERA))) {
            QRCode.startScan()
        }
        // 권한이 다 있지 않을 경우
        else {
            Utils.checkAbsentPerms(arrayOf(Constants.PERM_CAMERA), Constants.PERM_QR_REQ_CODE,
                basicActivity.qrCodeScanAction)
        }
    }

    /**================================ Photo Device Action ======================================*/
    val photoByDeviceRatio: (FlexAction?, JSONArray?) -> Unit = { photoDeviceAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            Constants.LOGD("============== Photo By Device Ratio Action ==============")

            val ratio = array?.getDouble(0)
            val isWidthRatio = array?.getBoolean(1)

            // inject action and ratio
            val basicActivity = App.activity as BasicActivity
            basicActivity.photoDeviceAction = photoDeviceAction
            basicActivity.ratio = ratio

            val perms = arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                Constants.PERM_READ_EXTERNAL_STORAGE)

            // 권한이 다 있을 경우
            if(Utils.existAllPermission(perms)) {
                Photo.requestImage(isWidthRatio)
            }
            // 권한이 다 있지 않을 경우
            else {
                Utils.checkAbsentPerms(perms, Constants.PERM_MUL_PHOTO_DEVICE_REQ_CODE,
                    basicActivity.photoDeviceAction)
            }
        }
    }

    val photoByRatio: (FlexAction?, JSONArray?) -> Unit = { photoAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            Constants.LOGD("============== Photo By Ratio Action ==============")

            val ratio = array?.getDouble(0)

            // inject action and ratio
            val basicActivity = App.activity as BasicActivity
            basicActivity.photoAction = photoAction
            basicActivity.ratio = ratio

            val perms = arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                Constants.PERM_READ_EXTERNAL_STORAGE)

            // 권한이 다 있을 경우
            if(Utils.existAllPermission(perms)) {
                Photo.requestImage(null)
            }
            // 권한이 다 있지 않을 경우
            else {
                Utils.checkAbsentPerms(perms, Constants.PERM_PHOTO_REQ_CODE,
                    basicActivity.photoAction)
            }
        }
    }

    /**================================== Multi Photo Action =====================================*/
    val multiPhotoByDeviceRatio: (FlexAction?, JSONArray?) -> Unit = { multiplePhotoDeviceAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            val ratio = array?.getDouble(0)
            val isWidthRatio = array?.getBoolean(1)

            // inject action and ratio
            val basicActivity = App.activity as BasicActivity
            basicActivity.multiplePhotoDeviceAction = multiplePhotoDeviceAction
            basicActivity.ratio = ratio

            val perms =
                arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE, Constants.PERM_READ_EXTERNAL_STORAGE)

            // 권한이 다 있을 경우
            if(Utils.existAllPermission(perms)) {
                Photo.requestMultipleImages(isWidthRatio)
            }
            // 권한이 다 있지 않을 경우
            else {
                Utils.checkAbsentPerms(perms, Constants.PERM_MUL_PHOTO_DEVICE_REQ_CODE,
                    basicActivity.multiplePhotoDeviceAction)
            }
        }
    }

    val multiPhotoByRatio: (FlexAction?, JSONArray?) -> Unit = { multiplePhotoAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            val ratio = array?.getDouble(0)

            // inject action and ratio
            val basicActivity = App.activity as BasicActivity
            basicActivity.multiplePhotoAction = multiplePhotoAction
            basicActivity.ratio = ratio

            val perms =
                arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE, Constants.PERM_READ_EXTERNAL_STORAGE)

            // 권한이 다 있을 경우
            if(Utils.existAllPermission(perms)) {
                Photo.requestMultipleImages(null)
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

            val ratio = array?.getDouble(0)
            val isWidthRatio = array?.getBoolean(1)

            // inject action and ratio
            val basicActivity = App.activity as BasicActivity
            basicActivity.cameraDeviceAction = cameraDeviceAction
            basicActivity.ratio = ratio

            val perms = arrayOf(Constants.PERM_CAMERA)

            // 권한이 다 있을 경우
            if(Utils.existAllPermission(perms)) {
                Camera.request(isWidthRatio)
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

            val ratio = array?.getDouble(0)

            // inject action and ratio
            val basicActivity = App.activity as BasicActivity
            basicActivity.cameraAction = cameraAction
            basicActivity.ratio = ratio

            val perms = arrayOf(Constants.PERM_CAMERA)

            // 권한이 다 있을 경우
            if(Utils.existAllPermission(perms)) {
                Camera.request(null)
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

    val bioAuth: (FlexAction?, JSONArray?) -> Unit = { bioAuthAction, _->
        CoroutineScope(Dispatchers.Main).launch {
            val fragmentActivity = App.activity as FragmentActivity

            if(BioAuth.canAuthenticate()) {
                BioAuth.showPrompt(fragmentActivity, bioAuthAction)
            } else {
                Constants.LOGE("You can't call biometric prompt.")
            }
        }
    }

    val localRepository: (FlexAction?, JSONArray?) -> Unit = { localRepoAction, array ->
        when (array!!.getInt(0)) {
            Constants.SET_DATA_SHARED -> {
                val key = array.getString(1)
                val value = array.getString(2)

                val originValue = SharedPreferences.getString(Constants.SHARED_FILE_NAME, key)

                // 해당 키값에 대한 데이터가 존재하지 않을 경우
                if(originValue == "") {
                    // 입력받은 value값이 비어있는 경우
                    if(value == "") {
                        localRepoAction?.promiseReturn("데이터를 입력해 주세요.")
                    } else {
                        SharedPreferences.putData(Constants.SHARED_FILE_NAME, key, value)
                        localRepoAction?.promiseReturn("데이터를 저장하였습니다.")
                    }
                }
                // 해당 키값에 대한 데이터가 이미 존재하는 경우
                else {
                    localRepoAction?.promiseReturn("데이터가 이미 존재합니다.")
                }

            }
            Constants.GET_DATA_SHARED -> {
                val key = array.getString(1)
                var value = SharedPreferences.getString(Constants.SHARED_FILE_NAME, key)

                // 해당 키값에 대한 데이터가 존재하지 않을 경우
                if(value == "") {
                    localRepoAction?.promiseReturn(
                        "해당 키값에 대한 데이터가 존재하지 않습니다.")
                } else {
                    localRepoAction?.promiseReturn(value)
                }
            }
            Constants.DELETE_DATA_SHARED -> {
                val key = array.getString(1)
                val value = SharedPreferences.getString(Constants.SHARED_FILE_NAME, key)

                // 해당 키값에 대한 데이터가 존재하지 않을 경우
                if(value == "") {
                    localRepoAction?.promiseReturn(
                        "해당 키값에 대한 데이터가 존재하지 않습니다.")
                } else {
                    SharedPreferences.removeData(Constants.SHARED_FILE_NAME, key)
                    localRepoAction?.promiseReturn("데이터를 제거하였습니다.")
                }
            }
        }
    }
}