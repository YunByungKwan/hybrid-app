package com.example.hybridapp.util.module

import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface
import com.example.hybridapp.App
import com.example.hybridapp.util.Constants

object Dialog {

    fun show(_title: String?,
             _message: String?,
             _positiveButtonText: String?,
             _neutralButtonText: String?,
             _negativeButtonText: String?,
             _positiveListener: DialogInterface.OnClickListener?,
             _neutralListener: DialogInterface.OnClickListener?,
             _negativeListener: DialogInterface.OnClickListener?,
             _cancelListener: () -> Unit?)
    {
        Constants.LOGE("show", Constants.TAG_DIALOG)

        val title = _title ?: "알림"
        val message = _message ?: "다이얼로그입니다."
        val positiveButtonText = _positiveButtonText ?: "확인"
        val neutralButtonText = _neutralButtonText ?: "삭제"
        val negativeButtonText = _negativeButtonText ?: "취소"

        AlertDialog.Builder(App.activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText, _positiveListener)
            .setNeutralButton(neutralButtonText, _neutralListener)
            .setNegativeButton(negativeButtonText, _negativeListener)
            .setOnCancelListener { _cancelListener() }
            .setOnDismissListener { _cancelListener() }
            .create()
            .show()
    }

    /** 권한 거부 시 다이얼로그 */
    fun showDenialPermissionText() {
        show(Constants.DENIED_DIAL_TITLE, Constants.DENIED_DIAL_MSG, Constants.DENIED_DIAL_POS,
            null, null, null, null,
            null, {})
    }
}