package com.example.hybridapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        flex_web_view.setBaseUrl("file:///android_asset")
        flex_web_view.loadUrl("file:///android_asset/html/test.html")
        flex_web_view.addFlexInterface(FlexInterface())
        WebView.setWebContentsDebuggingEnabled(true)
    }
}
