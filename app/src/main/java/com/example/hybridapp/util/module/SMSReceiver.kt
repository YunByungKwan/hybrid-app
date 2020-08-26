package com.example.hybridapp.util.module

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.hybridapp.App
import com.example.hybridapp.R
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SMSReceiver: BroadcastReceiver() {
    val smsRetrievedAction = "com.google.android.gms.auth.api.phone.SMS_RETRIEVED"

    override fun onReceive(context: Context, intent: Intent) {
        Utils.LOGD("Call onReceive() in SMSReceiver.")

//        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
//        val basicActivity = App.activity as BasicActivity
//        if(id == basicActivity.downloadId) {
//            Utils.LOGD("Download Completed")
//            val returnObj = JSONObject()
//            returnObj.put(getString(R.string.obj_KEY_DATA, true)
//            basicActivity.fileAction?.promiseReturn()
//        }

        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    Utils.LOGD(App.INSTANCE.getString(R.string.log_msg_sms_success))

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