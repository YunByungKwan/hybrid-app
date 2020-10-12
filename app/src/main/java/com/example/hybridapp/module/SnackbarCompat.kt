package com.example.hybridapp.module

import android.view.View
import com.google.android.material.snackbar.Snackbar

object SnackbarCompat {

    /** 짧은 Snackbar 메시지 출력 */
    fun showShortSnackbar(view: View, message: String) = Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()

    /** 긴 Snackbar 메시지 출력 */
    fun showLongSnackbar(view: View, message: String) = Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()

}