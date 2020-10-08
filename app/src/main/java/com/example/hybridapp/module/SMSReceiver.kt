package com.example.hybridapp.module

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.hybridapp.App
import com.example.hybridapp.R
import com.example.hybridapp.util.Utils
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SMSReceiver: BroadcastReceiver() {

    val smsRetrievedAction = "com.google.android.gms.auth.api.phone.SMS_RETRIEVED"

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    Utils.LOGD("Message: $message")
                }
                CommonStatusCodes.CANCELED -> {
                    Utils.LOGD(App.INSTANCE.getString(R.string.log_msg_sms_canceled))
                }
                CommonStatusCodes.ERROR -> {
                    Utils.LOGD(App.INSTANCE.getString(R.string.log_msg_sms_error))
                }
                CommonStatusCodes.TIMEOUT-> {
                    Utils.LOGD(App.INSTANCE.getString(R.string.log_msg_sms_timeout))
                }
            }
        }
    }
}