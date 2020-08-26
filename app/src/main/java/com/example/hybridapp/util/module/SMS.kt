package com.example.hybridapp.util.module

import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import com.example.hybridapp.App
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.google.android.gms.auth.api.phone.SmsRetriever

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

object SMS {

    /** SMS Receiver 등록 */
    fun registerReceiver(receiver: SMSReceiver?) {
        Utils.LOGD("Call registerReceiver() in SMS object.")
        val filter = IntentFilter()
        filter.addAction(receiver!!.smsRetrievedAction)
        App.activity.registerReceiver(receiver, filter)
    }

    /** SMS Receiver 해제 */
    fun unregisterReceiver(receiver: SMSReceiver?) {
        Utils.LOGD("Call unregisterReceiver() in SMS object.")

        if(receiver != null) {
            App.activity.unregisterReceiver(receiver)
        } else {
            Utils.LOGD("SMS Receiver is null.")
        }
    }

    /** 문자 메시지를 보냄  */
    fun sendMessage() {
        Utils.LOGD("Call sendMessage() in SMS object.")

        val basicActivity = App.activity as BasicActivity
        val packageManager = App.INSTANCE.packageManager

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("smsto:${basicActivity.phoneNumber}")
            putExtra("sms_body", basicActivity.smsMessage)
        }

        if(Utils.existsReceiveActivity(sendIntent, packageManager)) {
            basicActivity.startActivityForResult(sendIntent, Constants.SEND_SMS_REQ_CODE)
        } else {
            var returnObj = Utils.createJSONObject(null, false, "메시지를 보낼 수 없습니다")
            basicActivity.sendSmsAction?.promiseReturn(returnObj)
            basicActivity.phoneNumber = null
            basicActivity.smsMessage = null
        }
    }


    /** 문자 메시지를 받음 */
    fun receiveMessage() {
        Utils.LOGD("Call receiveMessage() in SMS object.")

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