package com.example.hybridapp.util.module

import android.content.DialogInterface
import androidx.fragment.app.FragmentActivity
import app.dvkyun.flexhybridand.FlexAction
import app.dvkyun.flexhybridand.FlexUtil
import com.example.hybridapp.App
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

object Action {

    /**
     * UI Thread에서 실행해야 함
     * @param array 다이얼로그 텍스트
     *              Ex)
     *              setArgs(0, "Title Text");
     *              setArgs(1, "Body Text");
     *              setArgs(2, [
     *              ["확인", "basic"],
     *              ["취소", "cancel"],
     *              ["삭제", "destructive"],]);
     *              setArgs(3, true);
     *              setArgs(4, true);
     */
    val dialog: (FlexAction?, JSONArray?) -> Unit = { dialogAction, array ->
        CoroutineScope(Dispatchers.Main).launch {

            val a = FlexUtil.convertJSONArray(array)!!
            var t = a[0] as String

            // 맵으로 바꾸는게 좋은듯
            val dic = array!!.get(2) as JSONObject
            val dic2 = dic.get("basic").toString()
            val title = array!!.getString(0)
            val contents = array.getString(1)
            val btns = array.getJSONArray(2)
            for(i in 0..btns.length()) {
                if(btns.getJSONArray(i).getString(1) == "basic") {

                }
            }
            btns.getJSONArray(0).getString(1)
            val posButtonText
                    = dic2
            val negButtonText
                    = array.getJSONArray(2).getJSONArray(1).getString(0)
            val posListener = DialogInterface.OnClickListener { _, _ ->
                dialogAction?.promiseReturn(posButtonText)
            }
            val negListener = DialogInterface.OnClickListener { _, _ ->
                dialogAction?.promiseReturn(negButtonText)
            }
            val cancelListener = {
                dialogAction?.promiseReturn(Constants.RESULT_CANCELED)
            }

            Dialog.show(title, contents, posButtonText, negButtonText,
                posListener, negListener, cancelListener)
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

    val networkStatus: (FlexAction?, JSONArray?) -> Unit = { networkStatusAction, _ ->
        CoroutineScope(Dispatchers.Main).launch {
            when (Network.getStatus()) {
                Constants.STATUS_CELLULAR -> {
                    Toast.showShortText(Constants.TOAST_MSG_CELLULAR)
                    networkStatusAction?.promiseReturn(Constants.STATUS_CELLULAR)
                }
                Constants.STATUS_WIFI -> {
                    Toast.showShortText(Constants.TOAST_MSG_WIFI)
                    networkStatusAction?.promiseReturn(Constants.STATUS_WIFI)
                }
                Constants.STATUS_NO -> {
                    Toast.showShortText(Constants.TOAST_MSG_NO)
                    networkStatusAction?.promiseReturn(Constants.STATUS_NO)
                }
            }
        }
    }

    val camera: (FlexAction?, JSONArray?) -> Unit = { cameraAction, _ ->
        CoroutineScope(Dispatchers.Main).launch {
            if(Utils.existAllPermission(arrayOf(Constants.PERM_CAMERA))) {
                if (cameraAction != null) {
                    Camera.request(cameraAction)
                }
            } else {
                Utils.requestDangerousPermissions(arrayOf(Constants.PERM_CAMERA),
                    Constants.REQ_PERM_CODE_CAMERA)
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
    val singlePhoto: (FlexAction?, JSONArray?) -> Unit = { singlePhotoAction, _ ->
        CoroutineScope(Dispatchers.Main).launch {
            val storagePermissions = arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                Constants.PERM_READ_EXTERNAL_STORAGE)

            if(Utils.existAllPermission(storagePermissions)) {
                if (singlePhotoAction != null) {
                    Photo.requestImage(singlePhotoAction)
                }
            } else {
                Utils.requestDangerousPermissions(storagePermissions,
                    Constants.REQ_PERM_CODE_READ_WRITE)
            }
        }
    }

    val multiplePhoto: (FlexAction?, JSONArray?) -> Unit = { multiplePhotoAction, _ ->
        CoroutineScope(Dispatchers.Main).launch {
            val storagePermissions =
                arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                Constants.PERM_READ_EXTERNAL_STORAGE)

            if(Utils.existAllPermission(storagePermissions)) {
                Photo.requestMultipleImages(multiplePhotoAction)
            } else {
                Utils.requestDangerousPermissions(storagePermissions,
                    Constants.REQ_PERM_CODE_READ_WRITE)
            }
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

    /**
     * @param array
     */
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