package com.example.hybridapp.basic

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import app.dvkyun.flexhybridand.FlexAction

@SuppressLint("Registered")
open class BasicActivity: AppCompatActivity() {
    var cameraAction: FlexAction? = null
    var qrCodeScanAction: FlexAction? = null
    var singlePhotoAction: FlexAction? = null
    var multiplePhotosAction: FlexAction? = null
    var locationAction: FlexAction? = null
    var bioAuthAction: FlexAction? = null
    var recordAction: FlexAction? = null
}