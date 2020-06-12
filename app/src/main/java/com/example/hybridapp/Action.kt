package com.example.hybridapp

import android.content.DialogInterface
import androidx.fragment.app.FragmentActivity
import app.dvkyun.flexhybridand.FlexAction
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.example.hybridapp.util.module.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

object Action {

    val dialog: (FlexAction?, JSONArray?) -> Unit = { dialogAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            val title = array?.getString(0)
            val message = array?.getString(1)
            val jsonObject: JSONObject? = array?.get(2) as JSONObject

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
                    title, message, basic, destructive, cancel,
                    posListener, null, negListener, cancelListener
                )
            }
        }
    }

    val network: (FlexAction?, JSONArray?) -> Unit = { networkAction, _ ->
        CoroutineScope(Dispatchers.Main).launch {
            when(Network.getStatus(App.activity)) {
                Constants.NET_STAT_CELLULAR -> {
                    networkAction?.promiseReturn(Constants.MSG_CELLULAR)
                }
                Constants.NET_STAT_WIFI -> {
                    networkAction?.promiseReturn(Constants.MSG_WIFI)
                }
                else -> {
                    networkAction?.promiseReturn(Constants.MSG_DISCONNECTED)
                }
            }
        }
    }

    val cameraByDeviceRatio: (FlexAction?, JSONArray?) -> Unit = { cameraDeviceAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            val ratio = array?.getDouble(0)
            val isWidthRatio = array?.getBoolean(1)
            Camera.request(cameraDeviceAction, ratio, isWidthRatio)
        }
    }

    val cameraByRatio: (FlexAction?, JSONArray?) -> Unit = { cameraAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            val ratio = array?.getDouble(0)

            Camera.request(cameraAction, ratio, null)
        }
    }

    val photoByDeviceRatio: (FlexAction?, JSONArray?) -> Unit = { photoDeviceAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
//            val ratio = array?.getDouble(0)
//            val isWidthRatio = array?.getBoolean(1)
            val ratio = 1.0
            val isWidthRatio = false

            Photo.requestImage(photoDeviceAction, ratio, isWidthRatio)
        }
    }

    val photoByRatio: (FlexAction?, JSONArray?) -> Unit = { photoAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            val ratio = array?.getDouble(0)

            Photo.requestImage(photoAction, ratio!!, null)
        }
    }

    val multiPhotoByDeviceRatio: (FlexAction?, JSONArray?) -> Unit = { multiplePhotoDeviceAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
//            val ratio = array?.getDouble(0)
//            val isWidthRatio = array?.getBoolean(1)
            val ratio = 1.0
            val isWidthRatio = true

            Photo.requestMultipleImages(multiplePhotoDeviceAction, ratio!!, isWidthRatio!!)
        }
    }

    val multiPhotoByRatio: (FlexAction?, JSONArray?) -> Unit = { multiplePhotoAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            val ratio = array?.getDouble(0)

            Photo.requestMultipleImages(multiplePhotoAction, ratio!!, null)
        }
    }

    val qrCode: (FlexAction?, JSONArray?) -> Unit = { qrCodeAction, _->
        if(Utils.existAllPermission(arrayOf(Constants.PERM_CAMERA))) {
            QRCode.startScan(qrCodeAction)
        } else {
            Utils.checkDangerousPermissions(arrayOf(Constants.PERM_CAMERA),
                Constants.PERM_CAMERA_REQ_CODE)
        }
    }

    val location: (FlexAction?, JSONArray?) -> Unit = { locationAction, _->
        CoroutineScope(Dispatchers.Main).launch {
            Constants.LOGD("Call location action.")
            Location.getCurrent(locationAction)
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
                SharedPreferences.putData(Constants.SHARED_FILE_NAME, key, value)
                localRepoAction?.promiseReturn("데이터를 저장하였습니다.")
            }
            Constants.GET_DATA_SHARED -> {
                val key = array.getString(1)
                val value = SharedPreferences.getString(Constants.SHARED_FILE_NAME, key)
                localRepoAction?.promiseReturn(value)
            }
            Constants.DELETE_DATA_SHARED -> {
                val key = array.getString(1)
                SharedPreferences.removeData(Constants.SHARED_FILE_NAME, key)
                localRepoAction?.promiseReturn("데이터를 제거하였습니다.")
            }
        }
    }
}