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
            val basic: String? =
                if(jsonObject?.has("basic")!!)
                    jsonObject.get("basic").toString()
                else null
            val destructive: String? =
                if(jsonObject.has("destructive"))
                    jsonObject.get("destructive").toString()
                else null
            val cancel: String? =
                if(jsonObject.has("cancel"))
                    jsonObject.get("cancel").toString()
                else null

            val posListener = DialogInterface.OnClickListener { _, _ ->
                dialogAction?.promiseReturn(basic)
            }
            val negListener = DialogInterface.OnClickListener { _, _ ->
                dialogAction?.promiseReturn(cancel)
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

    val network: (FlexAction?, JSONArray?) -> Unit = { networkAction, _ ->
        CoroutineScope(Dispatchers.Main).launch {
            when(Network.getStatus(App.activity)) {
                Constants.STATUS_CELLULAR -> {
                    networkAction?.promiseReturn(Constants.MSG_CELLULAR)
                }
                Constants.STATUS_WIFI -> {
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
            val ratio = array?.getDouble(0)
            val isWidthRatio = array?.getBoolean(1)

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
            val ratio = array?.getDouble(0)
            val isWidthRatio = array?.getBoolean(1)

            Photo.requestMultipleImages(multiplePhotoDeviceAction, ratio!!, isWidthRatio!!)
        }
    }

    val multiPhotoByRatio: (FlexAction?, JSONArray?) -> Unit = { multiplePhotoAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            val ratio = array?.getDouble(0)

            Photo.requestMultipleImages(multiplePhotoAction, ratio!!, null)
        }
    }

    val qrcode: (FlexAction?, JSONArray?) -> Unit = { qrCodeAction, _->
        if(Utils.existAllPermission(arrayOf(Constants.PERM_CAMERA))) {
            QRCode.startScan(qrCodeAction)
        } else {
            Utils.checkDangerousPermissions(arrayOf(Constants.PERM_CAMERA),
                Constants.REQ_PERM_CODE_CAMERA)
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