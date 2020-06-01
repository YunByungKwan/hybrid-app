package com.example.hybridapp.util.module

import android.widget.Toast
import com.example.hybridapp.App

object Toast {

    /** 짧은 Toast 메시지 출력 */
    fun showShortText(message: String) {
        Toast.makeText(App.INSTANCE, message, Toast.LENGTH_SHORT).show()
    }

    /** 긴 Toast 메시지 출력 */
    fun showLongText(message: String) {
        Toast.makeText(App.INSTANCE, message, Toast.LENGTH_LONG).show()
    }
}