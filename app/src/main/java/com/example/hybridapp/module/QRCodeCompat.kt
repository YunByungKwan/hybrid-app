package com.example.hybridapp.module

import androidx.activity.result.contract.ActivityResultContracts
import app.dvkyun.flexhybridand.FlexAction
import app.dvkyun.flexhybridand.FlexLambda
import com.example.hybridapp.App
import com.example.hybridapp.MainActivity
import com.example.hybridapp.R
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QRCodeCompat(private val basicAct: MainActivity) {

    private var flexAction: FlexAction? = null
    
    /** QRCode 스캔 */
    private fun scanQrCode() {
        val intentIntegrator = IntentIntegrator(basicAct)
        intentIntegrator.setBeepEnabled(false)
        intentIntegrator.initiateScan()
        activityResult.launch(intentIntegrator.createScanIntent())
    }

    /** onActivityResult */
    private val activityResult = basicAct.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        val resultCode = result.resultCode
        val data = result.data

        val intentResult: IntentResult? =
            IntentIntegrator.parseActivityResult(10001, resultCode, data)

        // QR Code 성공
        if(intentResult != null) {
            // QR Code 값이 있는 경우
            if(intentResult.contents != null) {
                val returnObj = Utils.returnJson(true, intentResult.contents, null)
                flexAction?.promiseReturn(returnObj)
            }
            // QR Code 값이 없는 경우
            else {
                val returnObj = Utils.returnJson(true,
                    null, App.INSTANCE.getString(R.string.msg_no_qr))
                flexAction?.promiseReturn(returnObj)
            }
        }
        // QR Code 실패
        else {
            val returnObj = Utils.returnJson(
                true, null, App.INSTANCE.getString(R.string.msg_not_load_qr))
            flexAction?.promiseReturn(returnObj)
        }
    }
    
    /**======================================= Action ============================================*/

    val scanQrCodeAction
            = FlexLambda.action { action, _->
        withContext(Dispatchers.Main) {
            flexAction = action
            val permission = Constants.PERM_CAMERA
            if(Utils.existsPermission(permission)) {
                scanQrCode()
            } else {
                permissionResult.launch(permission)
            }
        }
    }

    /** onRequestPermissionResult */
    private val permissionResult = basicAct.registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            scanQrCode()
        } else {
            val deniedObj = Utils.returnJson(false,
                null, basicAct.getString(R.string.msg_denied_perm))
            flexAction?.promiseReturn(deniedObj)
        }
    }
}
