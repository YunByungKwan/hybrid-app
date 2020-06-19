package com.example.hybridapp.util.module

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsManager
import com.example.hybridapp.App
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
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
        Constants.LOGD("Call registerReceiver() in SMS object.")
        val filter = IntentFilter()
        filter.addAction(receiver!!.smsRetrievedAction)
        App.activity.registerReceiver(receiver, filter)
    }

    /** SMS Receiver 해제 */
    fun unregisterReceiver(receiver: SMSReceiver?) {
        Constants.LOGD("Call unregisterReceiver() in SMS object.")

        if(receiver != null) {
            App.activity.unregisterReceiver(receiver)
        } else {
            Constants.LOGD("SMS Receiver is null.")
        }
    }

    /** 문자 메시지를 보냄  */
    fun sendMessage() {
        Constants.LOGD("Call sendMessage() in SMS object.")

        val basicActivity = App.activity as BasicActivity

        val sentIntent = PendingIntent.getBroadcast(basicActivity,
            Constants.SEND_SMS_REQ_CODE, Intent("SMS_SENT"), 0)
        val deliveryIntent = PendingIntent.getBroadcast(basicActivity,
            Constants.SEND_SMS_REQ_CODE, Intent(), 0)

        val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(basicActivity.phoneNumber,
                null,
                basicActivity.smsMessage,
                sentIntent,
                null)

        basicActivity.sendSmsAction?.promiseReturn("성공")
    }


    /** 문자 메시지를 받음 */
    fun receiveMessage() {
        Constants.LOGD("Call receiveMessage() in SMS object.")

        val client = SmsRetriever.getClient(App.activity)
        val task = client.startSmsRetriever()
        task.addOnCompleteListener {
            Constants.LOGD("Call addOnCompleteListener in SMS object.")
        }
        task.addOnSuccessListener {
            Constants.LOGD("Call addOnSuccessListener in SMS object.")
        }
        task.addOnFailureListener {
            Constants.LOGD("Call addOnFailureListener in SMS object.")
        }
        task.addOnCanceledListener {
            Constants.LOGD("Call addOnCanceledListener in SMS object.")
        }
    }
}