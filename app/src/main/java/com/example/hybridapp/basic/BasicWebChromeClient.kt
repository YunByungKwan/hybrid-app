package com.example.hybridapp.basic

import android.content.Intent
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebView
import app.dvkyun.flexhybridand.FlexWebChromeClient
import com.example.hybridapp.util.Constants

class BasicWebChromeClient(activity: BasicActivity): FlexWebChromeClient(activity) {

    private val mActivity = activity

    private var mFilePatCallback: ValueCallback<Array<Uri>>? = null

    /** 웹뷰 파일 다운로드시 호출 */
    override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?): Boolean {
        Constants.LOGD("Call function onShowFileChooser() in BasicWebChromeClient class.")

        mFilePatCallback = filePathCallback

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"

        mActivity.startActivityForResult(intent, Constants.FILE_UPLOAD_REQ_CODE)

        return true
    }

}