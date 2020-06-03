package com.example.hybridapp.util.module

import android.content.DialogInterface
import androidx.fragment.app.FragmentActivity
import app.dvkyun.flexhybridand.FlexAction
import com.example.hybridapp.App
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
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

            Dialog.show(title, message, basic, destructive, cancel,
                posListener, null, negListener, cancelListener)
        }
    }

    val network: (FlexAction?, JSONArray?) -> Unit = { networkAction, _ ->
        CoroutineScope(Dispatchers.Main).launch {
            if(Network.isConnected()) {
                Toast.showShortText(Constants.TOAST_MSG_NETWORK_CONNECTED)
                networkAction?.promiseReturn(true)
            } else {
                Toast.showShortText(Constants.TOAST_MSG_NETWORK_DISCONNECTED)
                networkAction?.promiseReturn(false)
            }
        }
    }

    val cameraDeviceRatio: (FlexAction?, JSONArray?) -> Unit = { cameraDeviceAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            val ratio = array?.getDouble(0)
            val isWidthRatio = array?.getBoolean(1)

            if(Utils.existAllPermission(arrayOf(Constants.PERM_CAMERA))) {
                if (cameraDeviceAction != null) {
                    Camera.request(cameraDeviceAction, ratio, isWidthRatio)
                }
            } else {
                Utils.requestDangerousPermissions(arrayOf(Constants.PERM_CAMERA),
                    Constants.REQ_PERM_CODE_CAMERA)
            }
        }
    }

    val cameraRatio: (FlexAction?, JSONArray?) -> Unit = { cameraAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            val ratio = array?.getDouble(0)

            if(Utils.existAllPermission(arrayOf(Constants.PERM_CAMERA))) {
                if (cameraAction != null) {
                    Camera.request(cameraAction, ratio, null)
                }
            } else {
                Utils.requestDangerousPermissions(arrayOf(Constants.PERM_CAMERA),
                    Constants.REQ_PERM_CODE_CAMERA)
            }
        }
    }

    val photoDeviceRatio: (FlexAction?, JSONArray?) -> Unit = { photoDeviceAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            val ratio = array?.getDouble(0)
            val isWidthRatio = array?.getBoolean(1)

            val storagePermissions = arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                Constants.PERM_READ_EXTERNAL_STORAGE)

            if(Utils.existAllPermission(storagePermissions)) {
                if (photoDeviceAction != null) {
                    Photo.requestImage(photoDeviceAction, ratio, isWidthRatio!!)
                }
            } else {
                Utils.requestDangerousPermissions(storagePermissions,
                    Constants.REQ_PERM_CODE_READ_WRITE)
            }
        }
    }

    val photoRatio: (FlexAction?, JSONArray?) -> Unit = { photoAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            val ratio = array?.getDouble(0)

            val storagePermissions = arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                Constants.PERM_READ_EXTERNAL_STORAGE)

            if(Utils.existAllPermission(storagePermissions)) {
                if (photoAction != null) {
                    Photo.requestImage(photoAction, ratio, null)
                }
            } else {
                Utils.requestDangerousPermissions(storagePermissions,
                    Constants.REQ_PERM_CODE_READ_WRITE)
            }
        }
    }

    val multiPhotoDeviceRatio: (FlexAction?, JSONArray?) -> Unit = { multiplePhotoDeviceAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            val ratio = array?.getDouble(0)
            val isWidthRatio = array?.getBoolean(1)

            val storagePermissions =
                arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                    Constants.PERM_READ_EXTERNAL_STORAGE)

            if(Utils.existAllPermission(storagePermissions)) {
                Photo.requestMultipleImages(multiplePhotoDeviceAction, ratio, isWidthRatio!!)
            } else {
                Utils.requestDangerousPermissions(storagePermissions,
                    Constants.REQ_PERM_CODE_READ_WRITE)
            }
        }

    }

    val multiPhotoRatio: (FlexAction?, JSONArray?) -> Unit = { multiplePhotoAction, array ->
        CoroutineScope(Dispatchers.Main).launch {
            val ratio = array?.getDouble(0)

            val storagePermissions =
                arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                    Constants.PERM_READ_EXTERNAL_STORAGE)

            if(Utils.existAllPermission(storagePermissions)) {
                Photo.requestMultipleImages(multiplePhotoAction, ratio, null)
            } else {
                Utils.requestDangerousPermissions(storagePermissions,
                    Constants.REQ_PERM_CODE_READ_WRITE)
            }
        }

    }

    val qrcode: (FlexAction?, JSONArray?) -> Unit = { qrCodeAction, _->
        if(Utils.existAllPermission(arrayOf(Constants.PERM_CAMERA))) {
            QRCode.startScan(qrCodeAction)
        } else {
            Utils.requestDangerousPermissions(arrayOf(Constants.PERM_CAMERA),
                Constants.REQ_PERM_CODE_CAMERA)
        }
    }

    val location: (FlexAction?, JSONArray?) -> Unit = { locationAction, _->
        CoroutineScope(Dispatchers.Main).launch {
            val locationPermissions = arrayOf(Constants.PERM_ACCESS_FINE_LOCATION,
                Constants.PERM_ACCESS_COARSE_LOCATION)

            if(Utils.existAllPermission(locationPermissions)) {
                Location.getCurrent(locationAction)
            } else {
                Utils.requestDangerousPermissions(locationPermissions,
                    Constants.REQ_PERM_CODE_LOCATION)
            }
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

    val record: (FlexAction?, JSONArray?) -> Unit = { recordAction, _->
        if(Utils.existAllPermission(arrayOf(Constants.PERM_RECORD_AUDIO))) {
            Record.getIntent(recordAction)
        } else {
            Utils.requestDangerousPermissions(arrayOf(Constants.PERM_RECORD_AUDIO),
                Constants.REQ_PERM_CODE_RECORD_AUDIO)
        }
    }

    val localRepository: (FlexAction?, JSONArray?) -> Unit = { localRepoAction, array ->
        val state = array!!.getJSONArray(0).getInt(1)

        if(state == 0) {
            val key = array.getJSONArray(1).getString(1)
            val value = array.getJSONArray(1).getString(1)

            SharedPreferences.putData(Constants.SHARED_FILE_NAME, key, value)
        } else if(state == 1) { // get
            val key = array.getJSONArray(1).getString(1)

            SharedPreferences.getString(Constants.SHARED_FILE_NAME, key)
        } else if(state == 2) { // delete
            val key = array.getJSONArray(1).getString(1)

            SharedPreferences.removeData(Constants.SHARED_FILE_NAME, key)
        }
    }
}