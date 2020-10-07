package com.example.hybridapp.basic

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.webkit.ValueCallback
import android.webkit.WebView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import app.dvkyun.flexhybridand.FlexWebChromeClient
import com.example.hybridapp.App
import com.example.hybridapp.R
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import kotlinx.android.synthetic.main.activity_main.*

class BasicWebChromeClient(activity: BasicActivity): FlexWebChromeClient(activity) {

    private val basicActivity = App.activity as BasicActivity
    private var mFilePatCallback: ValueCallback<Array<Uri>>? = null

    /** onActivityResult */
    private val activityResultForFileDownload = basicActivity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

    }

    /** 웹뷰 파일 다운로드시 호출 */
    override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?): Boolean {
        mFilePatCallback = filePathCallback

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"

        activityResultForFileDownload.launch(intent)
        return true
    }
}