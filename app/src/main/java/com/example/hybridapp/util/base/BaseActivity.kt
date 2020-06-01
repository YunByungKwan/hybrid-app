package com.example.hybridapp.util.base

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import app.dvkyun.flexhybridand.FlexAction

@SuppressLint("Registered")
open class BaseActivity: AppCompatActivity() {
    var cameraAction: FlexAction? = null
    var singlePhotoAction: FlexAction? = null
    var multiplePhotosAction: FlexAction? = null
    var qrCodeScanAction: FlexAction? = null
    var bioAuthenticationAction: FlexAction? = null
}