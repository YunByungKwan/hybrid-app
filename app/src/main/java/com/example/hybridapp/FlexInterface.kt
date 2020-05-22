package com.example.hybridapp

import android.util.Log
import app.dvkyun.flexhybridand.FlexFuncInterface
import com.example.hybridapp.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.util.*

class FlexInterface {

    private val utils = Utils()

    @FlexFuncInterface
    fun ShortToast(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            Log.e(TAG, "call ShortToast() in $TAG")

            utils.showShortToast(array.toString())
        }
    }

    @FlexFuncInterface
    fun LongToast(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            Log.e(TAG, "call LongToast() in $TAG")

            utils.showLongToast(array.toString())
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

    companion object {
        private const val TAG = "FlexInterface"
    }
}