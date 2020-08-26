package com.example.hybridapp.util.module

import com.example.hybridapp.App
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Utils
import com.google.zxing.integration.android.IntentIntegrator

object QRCode {

    /** QRCode 스캔 */
    fun startScan() {
        Utils.LOGD("Call startScan() in QRCode object.")
        val basicActivity = App.activity as BasicActivity
        val intentIntegrator = IntentIntegrator(basicActivity)
        intentIntegrator.setBeepEnabled(false)
        intentIntegrator.initiateScan()
    }
}