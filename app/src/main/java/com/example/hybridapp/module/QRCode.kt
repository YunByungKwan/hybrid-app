package com.example.hybridapp.module

import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import app.dvkyun.flexhybridand.FlexAction
import app.dvkyun.flexhybridand.FlexLambda
import com.example.hybridapp.App
import com.example.hybridapp.R
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QRCode(private val basicActivity: BasicActivity) {

    var qrCodeScanAction: FlexAction? = null

    /**======================================= Action ============================================*/

    val scanAction
            = FlexLambda.action { qrCodeAction, _->
        withContext(Dispatchers.Main) {
            qrCodeScanAction = qrCodeAction

            if(Utils.existAllPermission(arrayOf(Constants.PERM_CAMERA))) {
                basicActivity.qrInstance!!.startScan()
            } else {
                basicActivity.qrInstance!!.requestPermissionResult.launch(Manifest.permission.CAMERA)
            }
        }
    }

    /** onRequestPermissionResult */
    private val requestPermissionResult = basicActivity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            startScan()
        } else {
            val deniedObj = Utils.createJSONObject(false,
                null, basicActivity.getString(R.string.msg_denied_perm))
            qrCodeScanAction?.promiseReturn(deniedObj)
        }
    }

    /** onActivityResult */
    private val activityResult = basicActivity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { _result ->
        val resultCode = _result.resultCode
        val data = _result.data

        val result: IntentResult? =
            IntentIntegrator.parseActivityResult(10001, resultCode, data)

        // QR Code 성공
        if(result != null) {
            // QR Code 값이 있는 경우
            if(result.contents != null) {
                Utils.LOGD("IntentIntegrator Result: ${result.contents}")
                val returnObj = Utils.createJSONObject(
                    true, result.contents, null)
               qrCodeScanAction?.promiseReturn(returnObj)
            }
            // QR Code 값이 없는 경우
            else {
                Utils.LOGE("QR Code result is null")
                val returnObj = Utils.createJSONObject(
                    true, null, App.INSTANCE.getString(R.string.msg_no_qr))
                qrCodeScanAction?.promiseReturn(returnObj)
            }
        }
        // QR Code 실패
        else {
            Utils.LOGE("QR CODE RESULT_CANCELED")
            val returnObj = Utils.createJSONObject(
                true, null, App.INSTANCE.getString(R.string.msg_not_load_qr))
            qrCodeScanAction?.promiseReturn(returnObj)
        }
    }

    /** QRCode 스캔 */
    private fun startScan() {
        val intentIntegrator = IntentIntegrator(basicActivity)
        intentIntegrator.setBeepEnabled(false)
        intentIntegrator.initiateScan()
        activityResult.launch(intentIntegrator.createScanIntent())
    }
}
