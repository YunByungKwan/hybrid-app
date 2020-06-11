package com.example.hybridapp.util.module

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.hybridapp.util.Constants
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SMSReceiver: BroadcastReceiver() {
    val smsRetrievedAction = "com.google.android.gms.auth.api.phone.SMS_RETRIEVED"

    override fun onReceive(context: Context, intent: Intent) {
        Constants.logD("Call onReceive() in SMSReceiver.")

        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    Constants.LOGD(Constants.LOG_LOG_MSG_SMS_SUCCESS)

                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    Constants.logD("Message: $message")
                }
                CommonStatusCodes.CANCELED -> {
                    Constants.logD(Constants.LOG_MSG_SMS_CANCELED)
                }
                CommonStatusCodes.ERROR -> {
                    Constants.logD(Constants.LOG_MSG_SMS_ERROR)
                }
                CommonStatusCodes.TIMEOUT-> {
                    Constants.logD(Constants.LOG_MSG_SMS_TIMEOUT)
                }
            }
        }
    }
}