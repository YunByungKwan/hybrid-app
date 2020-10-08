package com.example.hybridapp.module

import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.Gravity.CENTER
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import app.dvkyun.flexhybridand.FlexActionInterface
import app.dvkyun.flexhybridand.FlexFuncInterface
import app.dvkyun.flexhybridand.FlexLambda
import com.example.hybridapp.App
import com.example.hybridapp.R
import com.example.hybridapp.util.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

object Dialog {

    /**====================================== Action =============================================*/

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    val showAction = FlexLambda.action { dialogAction, array ->
        withContext(Main) {
            val title = array[0].asString()
            val contents = array[1].asString()
            val mapData = array[2].asMap()
            val isDialog = array[3].asBoolean()

            mapData?.let {
                val dialogKeys = arrayOf("basic", "destructive", "cancel")
                val basic: String? = it[dialogKeys[0]]?.asString()
                val destructive: String? = it[dialogKeys[1]]?.asString()
                val cancel: String? = it[dialogKeys[2]]?.asString()
                val returnObj = JSONObject()

                if (isDialog!!) {
                    val posListener = DialogInterface.OnClickListener { _, _ ->
                        basic?.let {
                            returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), dialogKeys[0])
                            dialogAction.promiseReturn(returnObj)
                        }
                    }
                    val neutralListener = DialogInterface.OnClickListener { _, _ ->
                        destructive?.let {
                            returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), dialogKeys[1])
                            dialogAction.promiseReturn(returnObj)
                        }
                    }
                    val negListener = DialogInterface.OnClickListener { _, _ ->
                        cancel?.let {
                            returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), dialogKeys[2])
                            dialogAction.promiseReturn(returnObj)
                        }
                    }
                    val exitListener = {
                        returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), dialogKeys[2])
                        dialogAction.promiseReturn(returnObj)
                    }
                    show(title, contents, basic, destructive, cancel, posListener, neutralListener, negListener, exitListener)
                } else {  // Bottom Dialog (Bottom Sheet Dialog)
                    val dialog = BottomSheetDialog(App.activity)

                    var posBtn = getBtnView(basic)
                    posBtn?.let { btn ->
                        btn.setOnClickListener {
                            returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), dialogKeys[0])
                            dialogAction?.promiseReturn(returnObj)
                            dialog.dismiss()
                        }
                    }

                    var neutralBtn = getBtnView(destructive)
                    neutralBtn?.let { btn ->
                        btn.setOnClickListener {
                            returnObj.put(
                                App.INSTANCE.getString(R.string.obj_key_msg),
                                dialogKeys[1]
                            )
                            dialogAction.promiseReturn(returnObj)
                            dialog.dismiss()
                        }
                    }

                    var negBtn = getBtnView(cancel)
                    negBtn?.let { btn ->
                        btn.setOnClickListener {
                            returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), dialogKeys[2])
                            dialogAction.promiseReturn(returnObj)
                            dialog.dismiss()
                        }
                    }

                    val exitListener = {
                        returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), dialogKeys[2])
                        dialogAction.promiseReturn(returnObj)
                    }

                    val btnList = arrayListOf(posBtn, neutralBtn, negBtn)
                    val dialogLayout = getBottomSheetDialogView(title, contents, btnList)
                    bottomSheetshow(dialog, dialogLayout, exitListener)
                }
            }
        }
    }

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

    /** BottomSheetDialog 객체 만들어서 화면에 출력 */
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

    /** BottomSheetDialog 의 Layout 생 */
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

    /** 커스텀 Button View 객체 생성하는 함 */
    fun getBtnView(text: String?) : Button? {
        var btn = Button(App.context(), null, R.style.Widget_AppCompat_Button_Borderless)
        btn.layoutParams = getMatchHorizonLinearLayoutParams()
        text?.let { btn.text = text } ?: run { return null }
        btn.textSize = 15F
        btn.gravity = CENTER
        return btn
    }

    /** 수평 Match_parent 인 LinearLayoutParams 생성 */
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