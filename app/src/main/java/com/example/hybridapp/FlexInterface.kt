package com.example.hybridapp

import android.util.Log
import app.dvkyun.flexhybridand.FlexFuncInterface
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.module.Toast
import com.example.hybridapp.util.module.Snackbar
import com.example.hybridapp.util.Utils
import com.example.hybridapp.util.module.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

class FlexInterface {

    private val utils = Utils()

    /** Toast interface */

    @FlexFuncInterface
    fun ShortToast(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            funLOGE(App.INSTANCE.getString(R.string.type_short_toast))

            Toast.showShortText(array.get(0).toString())
        }
    }

    @FlexFuncInterface
    fun LongToast(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            funLOGE(App.INSTANCE.getString(R.string.type_long_toast))

            Toast.showLongText(array.get(0).toString())
        }
    }

    /** Snackbar interface */

    @FlexFuncInterface
    fun ShortSnackbar(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            funLOGE(App.INSTANCE.getString(R.string.type_short_snackbar))

            Snackbar.showShortText(App.activity.findViewById(R.id.linearLayout),
                array.get(0).toString())
        }
    }

    @FlexFuncInterface
    fun LongSnackbar(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            funLOGE(App.INSTANCE.getString(R.string.type_long_snackbar))

            Snackbar.showLongText(App.activity.findViewById(R.id.linearLayout),
                array.get(0).toString())
        }
    }

    @FlexFuncInterface
    fun InstanceId(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            funLOGE("InstanceId")

            val instanceId = utils.getInstanceId()
            Log.e(Constants.TAG_INTERFACE, "Instance id: $instanceId")
        }
    }

    @FlexFuncInterface
    fun GUID(array: JSONArray) {
        funLOGE("GUID")

        val guid = utils.getGUID()
        Log.e(Constants.TAG_INTERFACE, "GUID: $guid")
    }

    @FlexFuncInterface
    fun SaveSharedPreferences(array: JSONArray) {
        funLOGE("SaveSharedPreferences")

        val fileName = array.getString(0)
        val key = array.getString(1)
        val value = array.get(2)

        SharedPreferences.putData(fileName, key, value)
    }

    @FlexFuncInterface
    fun removeSharedPreferences(array: JSONArray) {
        funLOGE("removeSharedPreferences")

        val fileName = array.getString(0)
        val key = array.getString(1)

        SharedPreferences.removeData(fileName, key)
    }

    /** log.e wrapper function */
    private fun funLOGE(functionName: String) {
        Log.e(Constants.TAG_INTERFACE, "call $functionName() in ${Constants.TAG_INTERFACE}")
    }
}