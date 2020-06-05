package com.example.hybridapp.basic

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import app.dvkyun.flexhybridand.FlexAction

@SuppressLint("Registered")
open class BasicActivity: AppCompatActivity() {

    var cameraDeviceAction: FlexAction? = null
    var cameraAction: FlexAction? = null
    var photoDeviceAction: FlexAction? = null
    var photoAction: FlexAction? = null
    var multiplePhotoDeviceAction: FlexAction? = null
    var multiplePhotosAction: FlexAction? = null
    var qrCodeScanAction: FlexAction? = null
    var bioAuthAction: FlexAction? = null
    var recordAction: FlexAction? = null

    var ratio: Double? = null
    var isWidthRatio: Boolean? = null
}