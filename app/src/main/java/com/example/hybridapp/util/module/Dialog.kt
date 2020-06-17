package com.example.hybridapp.util.module

import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface
import com.example.hybridapp.App
import com.example.hybridapp.util.Constants

object Dialog {

    fun show(title: String?,
             message: String?,
             positiveButtonText: String?,
             neutralButtonText: String?,
             negativeButtonText: String?,
             positiveListener: DialogInterface.OnClickListener?,
             neutralListener: DialogInterface.OnClickListener?,
             negativeListener: DialogInterface.OnClickListener?,
             cancelListener: () -> Unit?)
    {
        Constants.LOGD("Call show() in Dialog object.")

        val dialog = AlertDialog.Builder(App.activity)

        if(title != null) dialog.setTitle(title) else dialog.setTitle("")
        if(message != null) dialog.setMessage(message) else dialog.setMessage("")
        if(positiveButtonText != null)
            dialog.setPositiveButton(positiveButtonText, positiveListener)
        if(neutralButtonText != null)
            dialog.setNeutralButton(neutralButtonText, neutralListener)
        if(negativeButtonText != null)
            dialog.setNegativeButton(negativeButtonText, negativeListener)
        dialog.setOnCancelListener { cancelListener() }
        dialog.setOnDismissListener { cancelListener() }
        dialog.create()
        dialog.show()
    }
}