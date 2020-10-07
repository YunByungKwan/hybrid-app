package com.example.hybridapp.module

import androidx.activity.result.contract.ActivityResultContracts
import com.example.hybridapp.App
import com.example.hybridapp.R
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Utils
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class QRCode {

    private val basicActivity = App.activity as BasicActivity
    private val deniedObj = Utils.createJSONObject(false,
        null, basicActivity.getString(R.string.msg_denied_perm))

    /** onRequestPermissionResult */
    val requestPermissionResult = basicActivity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            startScan()
        } else {
            basicActivity.qrCodeScanAction?.promiseReturn(deniedObj)
        }
    }

    /** onActivityResult */
    val activityResult = basicActivity.registerForActivityResult(
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
               basicActivity.qrCodeScanAction?.promiseReturn(returnObj)
            }
            // QR Code 값이 없는 경우
            else {
                Utils.LOGE("QR Code result is null")
                val returnObj = Utils.createJSONObject(
                    true, null, App.INSTANCE.getString(R.string.msg_no_qr))
                basicActivity.qrCodeScanAction?.promiseReturn(returnObj)
            }
        }
        // QR Code 실패
        else {
            Utils.LOGE("QR CODE RESULT_CANCELED")
            val returnObj = Utils.createJSONObject(
                true, null, App.INSTANCE.getString(R.string.msg_not_load_qr))
            basicActivity.qrCodeScanAction?.promiseReturn(returnObj)
        }
    }

    /** QRCode 스캔 */
    fun startScan() {
        val intentIntegrator = IntentIntegrator(basicActivity)
        intentIntegrator.setBeepEnabled(false)
        intentIntegrator.initiateScan()
        activityResult.launch(intentIntegrator.createScanIntent())
    }
}
