package com.example.hybridapp.util

import android.app.AlertDialog
import android.content.DialogInterface
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.hybridapp.App
import com.google.android.material.snackbar.Snackbar

class Utils {
    /** #################### 2. 팝업 모듈 개발 #################### */

    fun showShortToast(message: String) {
        Log.e(TAG_TOAST, "call showShortToast() in $TAG")

        val context = App.INSTANCE
        val duration = Toast.LENGTH_SHORT
        Toast.makeText(context, message, duration).show()
    }

    fun showLongToast(message: String) {
        Log.e(TAG_TOAST, "call showLongToast() in $TAG")

        val context = App.INSTANCE
        val duration = Toast.LENGTH_LONG
        Toast.makeText(context, message, duration).show()
    }

    fun showShortSnackbar(view: View, message: String) {
        Log.e(TAG_SNACKBAR, "call showShortSnackbar() in $TAG")

        val duration = Snackbar.LENGTH_SHORT
        Snackbar.make(view, message, duration).show()
    }

    fun showLongSnackbar(view: View, message: String) {
        Log.e(TAG_SNACKBAR, "call showLongSnackbar() in $TAG")

        val duration = Snackbar.LENGTH_LONG
        Snackbar.make(view, message, duration).show()
    }

    /** #################### 3. 다이얼로그 모듈 개발 #################### */

    fun showDialog(title: String, message: String) {
        Log.e(TAG_DIALOG, "call showDialog($title, $message) in $TAG")

        AlertDialog.Builder(App.activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인", null)
            .setNegativeButton("취소", null)
            .create()
            .show()
    }

    fun showDialog(title: String, message: String,
                   positiveButtonText: String,
                   negativeButtonText: String) {
        Log.e(TAG_DIALOG, "call showDialog($title, $message, " +
                "$positiveButtonText, $negativeButtonText) in $TAG")

        AlertDialog.Builder(App.activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText, null)
            .setNegativeButton(negativeButtonText, null)
            .create()
            .show()
    }

    private fun showDialog(title: String, message: String,
                           positiveButtonText: String,
                           negativeButtonText: String,
                           pListener: DialogInterface.OnClickListener?,
                           nListener: DialogInterface.OnClickListener?) {
        Log.e(TAG_DIALOG, "call showDialog($title, $message, " +
                "$positiveButtonText, $negativeButtonText) with Listener in $TAG")

        AlertDialog.Builder(App.activity
        )
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText, pListener)
            .setNegativeButton(negativeButtonText, nListener)
            .create()
            .show()
    }

    companion object {
        private const val TAG = "Utils"
        private const val TAG_TOAST = "TOAST"
        private const val TAG_SNACKBAR = "SNACKBAR"
        private const val TAG_DIALOG = "DIALOG"
    }

}