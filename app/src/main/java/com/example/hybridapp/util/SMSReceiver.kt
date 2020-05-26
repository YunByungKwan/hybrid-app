package com.example.hybridapp.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SMSReceiver: BroadcastReceiver() {
    val SMSRetrievedAction = "com.google.android.gms.auth.api.phone.SMS_RETRIEVED"

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            Log.d(Constants.TAG_SMS_RECEIVER, "SmsReceiver : onReceiver")

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    Log.d(Constants.TAG_SMS_RECEIVER, "SmsReceiver : onReceiver(CommonStatusCodes.SUCCESS)")

                    Log.e(Constants.TAG_SMS_RECEIVER, "Message: $message")
                }
                CommonStatusCodes.TIMEOUT-> {
                    Log.d(Constants.TAG_SMS_RECEIVER, "SmsReceiver : onReceiver(CommonStatusCodes.TIMEOUT)")
                }
            }
        }
    }
}