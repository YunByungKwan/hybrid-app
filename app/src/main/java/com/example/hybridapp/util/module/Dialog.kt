package com.example.hybridapp.util.module

import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface
import com.example.hybridapp.App
import com.example.hybridapp.util.Constants

object Dialog {

    fun show(_title: String?,
             _message: String?,
             _positiveButtonText: String?,
             _negativeButtonText: String?,
             _positiveListener: DialogInterface.OnClickListener?,
             _negativeListener: DialogInterface.OnClickListener?,
             _cancelListener: () -> Unit?)
    {
        Constants.LOGE("show", Constants.TAG_DIALOG)

        var title = "알림"
        var message = "다이얼로그입니다."
        var positiveButtonText = "확인"
        var negativeButtonText = "취소"
        if(_title != null) {
            title = _title
        }
        if(_message != null) {
            message = _message
        }
        if(_positiveButtonText != null) {
            positiveButtonText = _positiveButtonText
        }
        if(_negativeButtonText != null) {
            negativeButtonText = _negativeButtonText
        }

        AlertDialog.Builder(App.activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText, _positiveListener)
            .setNegativeButton("중립", null)
            .setNegativeButton(negativeButtonText, _negativeListener)
            .setOnCancelListener { _cancelListener() }
            .setOnDismissListener { _cancelListener() }
            .create()
            .show()
    }

    /** 권한 거부 시 다이얼로그 */
    fun showDenialPermissionText() {
        show(Constants.DENIED_DIAL_TITLE, Constants.DENIED_DIAL_MSG, Constants.DENIED_DIAL_POS,
            null, null, null, {})
    }

}