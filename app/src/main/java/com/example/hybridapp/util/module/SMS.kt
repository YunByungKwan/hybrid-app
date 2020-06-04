package com.example.hybridapp.util.module

import android.content.IntentFilter
import android.telephony.SmsManager
import android.util.Log
import com.example.hybridapp.App
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

    fun registerReceiver(receiver: SMSReceiver?) {
        val filter = IntentFilter()
        filter.addAction(receiver!!.SMSRetrievedAction)

        App.activity.registerReceiver(receiver, filter)
    }

    fun unregisterReceiver(receiver: SMSReceiver?) {
        if(receiver != null) {
            App.activity.unregisterReceiver(receiver)
        } else {
            Log.e(Constants.TAG_MAIN, "smsReceiver is null in onPause().")
        }
    }

    /** 문자 메시지를 보냄  */
    fun sendMessage(phoneNumber: String, message: String) {
        Constants.LOGE("sendMessage", Constants.TAG_SMS)

        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber,
            null, message, null, null)
    }

    /** Receive message */
    fun receiveMessage() {
        Constants.LOGE("receiveMessage", Constants.TAG_SMS)

        val client = SmsRetriever.getClient(App.activity)
        val task = client.startSmsRetriever()
        task.addOnCompleteListener {
            Log.e(Constants.TAG_SMS, "SMS task is completed.")
        }
        task.addOnSuccessListener {
            Log.e(Constants.TAG_SMS, "SMS task success.")
        }
        task.addOnFailureListener {
            Log.e(Constants.TAG_SMS, "SMS task fail.")
        }
        task.addOnCanceledListener {
            Log.e(Constants.TAG_SMS, "SMS task is canceled.")
        }
    }
}