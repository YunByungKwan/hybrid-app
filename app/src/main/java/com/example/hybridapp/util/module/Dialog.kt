package com.example.hybridapp.util.module

import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.Gravity.CENTER
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginTop
import com.example.hybridapp.App
import com.example.hybridapp.R
import com.example.hybridapp.util.Constants
import com.google.android.material.bottomsheet.BottomSheetDialog

object Dialog {

    fun show(title: String?,
             message: String?,
             positiveButtonText: String?,
             neutralButtonText: String?,
             negativeButtonText: String?,
             positiveListener: DialogInterface.OnClickListener?,
             neutralListener: DialogInterface.OnClickListener?,
             negativeListener: DialogInterface.OnClickListener?,
             exitListener: () -> Unit?)
    {
        Constants.LOGD("Call show() in Dialog object.")

        val dialog = AlertDialog.Builder(App.activity)

        title?.let { dialog.setTitle(title) } ?: dialog.setTitle("")
        message?.let { dialog.setMessage(message) } ?: dialog.setMessage("")

        positiveButtonText?.let { dialog.setPositiveButton(positiveButtonText, positiveListener) }
        neutralButtonText?.let { dialog.setNeutralButton(neutralButtonText, neutralListener) }
        negativeButtonText?.let { dialog.setNegativeButton(negativeButtonText, negativeListener) }

        dialog.setOnCancelListener { exitListener() }

        dialog.create()
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun bottomSheetshow(dialog: BottomSheetDialog,
                        layout: LinearLayout,
                        exitListener: () -> Unit?)
    {
            dialog.setContentView(layout)
            dialog.setOnCancelListener { exitListener() }
            dialog.setCanceledOnTouchOutside(false)

            dialog.create()
            dialog.show()
    }

    fun getBottomSheetDialogView(title: String?,
                                 message: String?,
                                 btnList : List<Button?>?) : LinearLayout
    {
        var layout = LinearLayout(App.context())
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER_VERTICAL

        // TITLE TEXTVIEW 생성
        var titleTextView = TextView(App.context())
        title?.let {
            titleTextView.text = title
            titleTextView.textSize = 25F
            titleTextView.gravity = CENTER
            titleTextView.layoutParams = getMatchHorizonLinearLayoutParams()
            layout.addView(titleTextView)
        }

        // TITLE 과 CONTEXT의 경계 생성
        var border = View(App.context())
        var params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 5)
        params.setMargins(100, 15, 100, 15)
        border.layoutParams = params
        border.setBackgroundColor(Color.RED)
        layout.addView(border)

        // CONTEXT TEXTVIEW 생성
        var msgTextView = TextView(App.context())
        message?.let {
            msgTextView.text = message
            msgTextView.textSize = 20F
            msgTextView.gravity = CENTER
            msgTextView.layoutParams = getMatchHorizonLinearLayoutParams()
            layout.addView(msgTextView)
        }

        // CONTEXT 와 BUTTON의 경계 생성
        border = View(App.context())
        params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3)
        params.setMargins(15, 25, 15, 25)
        border.layoutParams = params
        layout.addView(border)

        // BUTTON 생성
        btnList?.let {
            for(btn in btnList) {
                btn?.let { layout.addView(btn) }
            }
        }

        return layout
    }

    fun getBtnView(text: String?) : Button? {
        var btn = Button(App.context(), null, R.style.Widget_AppCompat_Button_Borderless)
        btn.layoutParams = getMatchHorizonLinearLayoutParams()
        text?.let { btn.text = text } ?: run { return null }
        btn.textSize = 15F
        btn.gravity = CENTER
        return btn
    }

    private fun getMatchHorizonLinearLayoutParams() : LinearLayout.LayoutParams {
        var params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.gravity = CENTER
        params.setMargins(5, 20,5,20)
        return params
    }
}