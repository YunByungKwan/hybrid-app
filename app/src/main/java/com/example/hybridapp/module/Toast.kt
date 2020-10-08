package com.example.hybridapp.module

import android.widget.Toast
import app.dvkyun.flexhybridand.FlexData
import app.dvkyun.flexhybridand.FlexFuncInterface
import com.example.hybridapp.App
import com.example.hybridapp.basic.BasicActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Toast {

    /**======================================= Interface =========================================*/

    @FlexFuncInterface
    fun Toast(array: Array<FlexData>) {
        CoroutineScope(Dispatchers.Main).launch {
            val message = array[0].asString()!!
            val isShortToast = array[1].asBoolean()!!

            if(isShortToast) {
                showShortToast(message)
            } else {
                showLongToast(message)
            }
        }
    }

    /**======================================== Function =========================================*/

    /** 짧은 Toast 메시지 출력 */
    fun showShortToast(message: String)
            = Toast.makeText(App.INSTANCE, message, Toast.LENGTH_SHORT).show()

    /** 긴 Toast 메시지 출력 */
    fun showLongToast(message: String)
            = Toast.makeText(App.INSTANCE, message, Toast.LENGTH_LONG).show()
}
