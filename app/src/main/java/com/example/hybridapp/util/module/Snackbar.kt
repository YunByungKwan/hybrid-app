package com.example.hybridapp.util.module

import android.view.View
import com.example.hybridapp.util.Constants
import com.google.android.material.snackbar.Snackbar

object Snackbar {

    /** 짧은 Snackbar 메시지 출력 */
    fun showShortText(view: View, message: String)
            = Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()

    /** 긴 Snackbar 메시지 출력 */
    fun showLongText(view: View, message: String)
            = Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
}