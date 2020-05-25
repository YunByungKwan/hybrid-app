package com.example.hybridapp

import android.util.Log
import android.view.View
import app.dvkyun.flexhybridand.FlexFuncInterface
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

class FlexInterface {

    private val utils = Utils()

    @FlexFuncInterface
    fun ShortToast(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            funLOGE("ShortToast")

            utils.showShortToast(array.toString())
        }
    }

    @FlexFuncInterface
    fun LongToast(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            funLOGE("LongToast")

            utils.showLongToast(array.toString())
        }
    }

    @FlexFuncInterface
    fun ShortSnackbar(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            funLOGE("ShortToast")

            utils.showShortSnackbar(App.activity.findViewById(R.id.linearLayout), array.toString())
        }
    }

    @FlexFuncInterface
    fun LongSnackbar(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            funLOGE("LongToast")

            utils.showLongSnackbar(App.activity.findViewById(R.id.linearLayout), array.toString())
        }
    }

    @FlexFuncInterface
    fun Dialog(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            Log.e(TAG, "call Dialog() in $TAG")

            val title = array.getString(0)
            val contents = array.getString(1)

            utils.showDialog(title, contents)
        }
    }

    @FlexFuncInterface
    fun extendedDialog(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            Log.e(TAG, "call extendedDialog() in $TAG")

            utils.showDialog("제목", "다이얼로그 메시지입니다.", "긍정", "부정")
        }
    }

    /** log.e wrapper function */
    private fun funLOGE(functionName: String) {
        Log.e(Constants.TAG_INTERFACE, "call $functionName() in ${Constants.TAG_INTERFACE}")
    }

    companion object {
        private const val TAG = "FlexInterface"
    }
}