package com.example.hybridapp.util.module

import app.dvkyun.flexhybridand.FlexAction
import com.example.hybridapp.App
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.google.zxing.integration.android.IntentIntegrator

object QRCode {

    /** QRCode 스캔 */
    fun startScan(action: FlexAction?) {
        if(Utils.existAllPermission(arrayOf(Constants.PERM_CAMERA))) {
            val basicActivity = App.activity as BasicActivity
            basicActivity.qrCodeScanAction = action

            val intentIntegrator = IntentIntegrator(basicActivity)
            intentIntegrator.setBeepEnabled(false)
            intentIntegrator.initiateScan()
        } else {
            Utils.checkDangerousPermissions(arrayOf(Constants.PERM_CAMERA), Constants.PERM_CAMERA_REQ_CODE)
            action?.resolveVoid()
        }
    }
}