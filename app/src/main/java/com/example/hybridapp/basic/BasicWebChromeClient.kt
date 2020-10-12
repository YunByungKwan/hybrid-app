package com.example.hybridapp.basic

import android.content.Intent
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import app.dvkyun.flexhybridand.FlexWebChromeClient
import com.example.hybridapp.App

class BasicWebChromeClient(activity: BasicActivity): FlexWebChromeClient(activity) {

    private var mFilePatCallback: ValueCallback<Array<Uri>>? = null

    /** onActivityResult */
    private val activityResult = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

    }

    /** 웹뷰 파일 다운로드시 호출 */
    override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?): Boolean {
        mFilePatCallback = filePathCallback

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"

        activityResult.launch(intent)
        return true
    }
}