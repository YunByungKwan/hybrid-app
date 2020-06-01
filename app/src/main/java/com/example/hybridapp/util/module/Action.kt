package com.example.hybridapp.util.module

import app.dvkyun.flexhybridand.FlexAction
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

object Action {

    val network: (FlexAction?, JSONArray?) -> Unit = { action, _ ->
        CoroutineScope(Dispatchers.Main).launch {
            if(Network.isConnected()) {
                Toast.showShortText(Constants.TOAST_MSG_NETWORK_CONNECTED)
                action?.promiseReturn(true)
            } else {
                Toast.showShortText(Constants.TOAST_MSG_NETWORK_DISCONNECTED)
                action?.promiseReturn(false)
            }
        }
    }

    val camera: (FlexAction?, JSONArray?) -> Unit = { action, _ ->
        CoroutineScope(Dispatchers.Main).launch {
            if(Utils().existAllPermission(arrayOf(Constants.PERM_CAMERA))) {
                if (action != null) {
                    Camera.request(action)
                }
            } else {
                Utils().requestDangerousPermissions(arrayOf(Constants.PERM_CAMERA),
                    Constants.REQ_PERM_CODE_CAMERA)
            }
        }
    }
}