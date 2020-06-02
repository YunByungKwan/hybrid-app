package com.example.hybridapp.util.module

import app.dvkyun.flexhybridand.FlexAction
import com.example.hybridapp.App
import com.example.hybridapp.basic.BasicActivity
import com.google.zxing.integration.android.IntentIntegrator

object QRCode {

    private val basicActivity = App.activity as BasicActivity

    /** QRCode 스캔 */
    fun startScan(action: FlexAction?) {

        basicActivity.qrCodeScanAction = action

        val intentIntegrator = IntentIntegrator(App.activity)
        intentIntegrator.setBeepEnabled(false)
        intentIntegrator.initiateScan()
    }
}