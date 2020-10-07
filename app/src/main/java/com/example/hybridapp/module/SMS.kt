package com.example.hybridapp.module

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import com.example.hybridapp.App
import com.example.hybridapp.R
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Utils
import com.google.android.gms.auth.api.phone.SmsRetriever
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

class SMS {

    private val basicActivity = App.activity as BasicActivity
    private val deniedObj = Utils.createJSONObject(false, null,
        basicActivity.getString(R.string.msg_denied_perm))

    /** onRequestPermissionResult */
    val requestPermissionResult = basicActivity.registerForActivityResult(
        ActivityResultContracts.RequestPermission(), Manifest.permission.READ_EXTERNAL_STORAGE) { isGranted ->
        if(isGranted) {
            sendMessage()
        } else {
            basicActivity.sendSmsAction!!.promiseReturn(deniedObj)
            basicActivity.phoneNumber = null
            basicActivity.smsMessage = null
        }
    }

    /** onActivityResult */
    private val activityResult = basicActivity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        // Intent.ACTION_SEND는 return값이 없음
        val returnObj = JSONObject()
        returnObj.put(App.INSTANCE.getString(R.string.obj_key_data), true)
        basicActivity.sendSmsAction!!.promiseReturn(returnObj)
        basicActivity.phoneNumber = null
        basicActivity.smsMessage = null
    }

    /** SMS Receiver 등록 */
    fun registerReceiver(receiver: SMSReceiver?) {
        val filter = IntentFilter()
        filter.addAction(receiver!!.smsRetrievedAction)
        App.activity.registerReceiver(receiver, filter)
    }

    /** SMS Receiver 해제 */
    fun unregisterReceiver(receiver: SMSReceiver?) {
        if(receiver != null) {
            App.activity.unregisterReceiver(receiver)
        } else {
            Utils.LOGD("SMS Receiver is null.")
        }
    }

    /** 문자 메시지를 보냄  */
    fun sendMessage() {
        val packageManager = App.INSTANCE.packageManager

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("smsto:${basicActivity.phoneNumber}")
            putExtra("sms_body", basicActivity.smsMessage)
        }

        if(Utils.existsReceiveActivity(sendIntent, packageManager)) {
            activityResult.launch(sendIntent)
        } else {
            var returnObj = Utils.createJSONObject(null, false, "메시지를 보낼 수 없습니다")
            basicActivity.sendSmsAction?.promiseReturn(returnObj)
            basicActivity.phoneNumber = null
            basicActivity.smsMessage = null
        }
    }


    /** 문자 메시지를 받음 */
    fun receiveMessage() {
        val client = SmsRetriever.getClient(App.activity)
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
}