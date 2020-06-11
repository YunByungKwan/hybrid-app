package com.example.hybridapp.util.module

import android.content.IntentFilter
import android.telephony.SmsManager
import android.util.Log
import com.example.hybridapp.App
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
        Constants.logD("Call registerReceiver() in SMS object.")
        val filter = IntentFilter()
        filter.addAction(receiver!!.smsRetrievedAction)
        App.activity.registerReceiver(receiver, filter)
    }

    /** SMS Receiver 해제 */
    fun unregisterReceiver(receiver: SMSReceiver?) {
        Constants.logD("Call unregisterReceiver() in SMS object.")

        if(receiver != null) {
            App.activity.unregisterReceiver(receiver)
        } else {
            Constants.logD("SMS Receiver is null.")
        }
    }

    /** 문자 메시지를 보냄  */
    fun sendMessage(phoneNumber: String, message: String): String {
        Constants.logD("Call sendMessage() in SMS object.")

        return if(Utils.existAllPermission(arrayOf(Constants.PERM_SEND_SMS))) {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message,
                null, null)

            Constants.LOG_MSG_SMS_SUCCESS
        } else {
            Utils.checkDangerousPermissions(arrayOf(Constants.PERM_SEND_SMS),
                Constants.PERM_SEND_SMS_REQ_CODE)

            Log.d("dlgodnjs", "asggg")
            ""
        }
    }

    /** 문자 메시지를 받음 */
    fun receiveMessage() {
        Constants.logD("Call receiveMessage() in SMS object.")

        val client = SmsRetriever.getClient(App.activity)
        val task = client.startSmsRetriever()
        task.addOnCompleteListener {
            Constants.logD("Call addOnCompleteListener in SMS object.")
        }
        task.addOnSuccessListener {
            Constants.logD("Call addOnSuccessListener in SMS object.")
        }
        task.addOnFailureListener {
            Constants.logD("Call addOnFailureListener in SMS object.")
        }
        task.addOnCanceledListener {
            Constants.logD("Call addOnCanceledListener in SMS object.")
        }
    }
}