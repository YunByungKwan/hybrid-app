package com.example.hybridapp.util.module

import com.example.hybridapp.App
import com.google.zxing.integration.android.IntentIntegrator

object QRCode {

    /** QRCode 스캔 */
    fun startScan() {
        val intentIntegrator = IntentIntegrator(App.activity)
        intentIntegrator.setBeepEnabled(false)
        intentIntegrator.initiateScan()
    }
}