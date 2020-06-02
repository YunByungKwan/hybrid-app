package com.example.hybridapp.basic

import android.content.Intent
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import app.dvkyun.flexhybridand.FlexWebChromeClient
import com.example.hybridapp.App
import com.example.hybridapp.util.Constants

object BasicWebChromeClient: FlexWebChromeClient(App.activity) {

    private var mFilePatCallback: ValueCallback<Array<Uri>>? = null

    override fun onShowFileChooser(
        webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?): Boolean
    {
        mFilePatCallback = filePathCallback

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"

        App.activity.
        startActivityForResult(intent, Constants.REQ_CODE_FILE_UPLOAD)

        return true
    }
}