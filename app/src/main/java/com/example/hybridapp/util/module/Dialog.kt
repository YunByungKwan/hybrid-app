package com.example.hybridapp.util.module

import android.app.AlertDialog
import android.content.DialogInterface
import com.example.hybridapp.App
import com.example.hybridapp.util.Constants

object Dialog {

    fun show(title: String, message: String) {
        Constants.LOGE("show", Constants.TAG_DIALOG)

        AlertDialog.Builder(App.activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인", null)
            .setNegativeButton("취소", null)
            .setOnCancelListener { it.cancel() }
            .setOnDismissListener { it.dismiss() }
            .create()
            .show()
    }

    fun show(title: String, message: String, posTxt: String) {
        Constants.LOGE("show", Constants.TAG_DIALOG)

        AlertDialog.Builder(App.activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(posTxt, null)
            .setOnCancelListener { it.cancel() }
            .setOnDismissListener { it.dismiss() }
            .create()
            .show()
    }

    fun show(title: String, message: String, posTxt: String, negTxt: String) {
        Constants.LOGE("show", Constants.TAG_DIALOG)

        AlertDialog.Builder(App.activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(posTxt, null)
            .setNegativeButton(negTxt, null)
            .setOnCancelListener { it.cancel() }
            .setOnDismissListener { it.dismiss() }
            .create()
            .show()
    }

    fun show(title: String, message: String, posTxt: String, negTxt: String,
                   pListener: DialogInterface.OnClickListener?,
                   nListener: DialogInterface.OnClickListener?) {
        Constants.LOGE("show", Constants.TAG_DIALOG)

        AlertDialog.Builder(App.activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(posTxt, pListener)
            .setNegativeButton(negTxt, nListener)
            .setOnCancelListener { it.cancel() }
            .setOnDismissListener { it.dismiss() }
            .create()
            .show()
    }

    fun show(title: String, message: String, posTxt: String, negTxt: String,
             pListener: DialogInterface.OnClickListener?,
             nListener: DialogInterface.OnClickListener?,
             cListener: () -> Unit) {
        Constants.LOGE("show", Constants.TAG_DIALOG)

        AlertDialog.Builder(App.activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(posTxt, pListener)
            .setNegativeButton(negTxt, nListener)
            .setOnCancelListener { cListener() }
            .setOnDismissListener { cListener() }
            .create()
            .show()
    }

    /** 권한 거부 시 다이얼로그 */
    fun showDenialPermissionText() {
        show(Constants.DIAL_TITLE_DENIED, Constants.DIAL_MSG_DENIED, Constants.DIAL_POS_DENIED)
    }


}