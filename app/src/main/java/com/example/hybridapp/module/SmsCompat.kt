package com.example.hybridapp.module

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.result.registerForActivityResult
import app.dvkyun.flexhybridand.FlexAction
import app.dvkyun.flexhybridand.FlexData
import app.dvkyun.flexhybridand.FlexFuncInterface
import app.dvkyun.flexhybridand.FlexLambda
import com.example.hybridapp.App
import com.example.hybridapp.R
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * 문자 형식
 * <#> 으로 시작해야 한다.
 * 11자 해시 문자열로 끝나야 한다.
 * 140 byte 이하여야 한다.
 *
 * Example>
 * <#>
 * 테스트입니다.
 * amvosl3kf/u
 */

class SmsCompat(private val basicActivity: BasicActivity) {

    private var phoneNumber: String? = null
    private var message: String? = null
    private var flexAction: FlexAction? = null

    /** SMS Receiver를 등록한다. */
    fun registerReceiver(receiver: SMSReceiver?) {
        val filter = IntentFilter()
        filter.addAction(receiver!!.smsRetrievedAction)
        basicActivity.registerReceiver(receiver, filter)
    }

    /** SMS Receiver를 해제한다. */
    fun unregisterReceiver(receiver: SMSReceiver?) {
        if(receiver != null) {
            basicActivity.unregisterReceiver(receiver)
        } else {
            Utils.LOGD("SMS Receiver is null.")
        }
    }

    /** 문자 메시지를 보냄  */
    private fun sendSMS() {
        val packageManager = App.INSTANCE.packageManager

        val intent = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("smsto:${phoneNumber}")
            putExtra("sms_body", message)
        }

        if(Utils.existsReceiveActivity(intent, packageManager)) {
            activityResult.launch(intent)
        } else {
            var returnObj = Utils.createJSONObject(null, false, "메시지를 보낼 수 없습니다")
            flexAction?.promiseReturn(returnObj)
            phoneNumber = null
            message = null
        }
    }

    /** 문자 메시지를 받음 */
    private fun receiveSMS() {
        val client = SmsRetriever.getClient(basicActivity)
        val task = client.startSmsRetriever()
        task.addOnCompleteListener {
            Utils.LOGD("Call addOnCompleteListener in SMS object.")
        }
        task.addOnSuccessListener {
            Utils.LOGD("Call addOnSuccessListener in SMS object.")
        }
        task.addOnFailureListener {
            Utils.LOGD("Call addOnFailureListener in SMS object.")
        }
        task.addOnCanceledListener {
            Utils.LOGD("Call addOnCanceledListener in SMS object.")
        }
    }

    /** onActivityResult */
    private val activityResult = basicActivity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        val returnObj = JSONObject()
        returnObj.put(App.INSTANCE.getString(R.string.obj_key_data), true)
        flexAction!!.promiseReturn(returnObj)
        phoneNumber = null
        message = null
    }

    /**====================================== Action =============================================*/
    val sendSmsAction
            = FlexLambda.action { action, array ->
        withContext(Dispatchers.Main) {
            flexAction = action
            phoneNumber = array[0].reified()
            message = array[1].reified()

            if(Utils.existAllPermission(arrayOf(Constants.PERM_SEND_SMS))) {
                sendSMS()
            } else {
                permissionResult.launch()
            }
        }
    }

    /** onRequestPermissionResult */
    private val permissionResult = basicActivity.registerForActivityResult(
        ActivityResultContracts.RequestPermission(), Manifest.permission.READ_EXTERNAL_STORAGE) { isGranted ->
        if(isGranted) {
            sendSMS()
        } else {
            val deniedObj = Utils.createJSONObject(false, null, basicActivity.getString(R.string.msg_denied_perm))
            flexAction!!.promiseReturn(deniedObj)
            phoneNumber = null
            message = null
        }
    }

    /**======================================= Interface =========================================*/

    @FlexFuncInterface
    fun ReceiveSMS(array: Array<FlexData>) {
        receiveSMS()
    }

}