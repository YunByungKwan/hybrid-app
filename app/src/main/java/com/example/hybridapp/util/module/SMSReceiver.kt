package com.example.hybridapp.util.module

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.hybridapp.App
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import org.json.JSONObject

class SMSReceiver: BroadcastReceiver() {
    val smsRetrievedAction = "com.google.android.gms.auth.api.phone.SMS_RETRIEVED"

    override fun onReceive(context: Context, intent: Intent) {
        Constants.LOGD("Call onReceive() in SMSReceiver.")

//        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
//        val basicActivity = App.activity as BasicActivity
//        if(id == basicActivity.downloadId) {
//            Constants.LOGD("Download Completed")
//            val returnObj = JSONObject()
//            returnObj.put(Constants.OBJ_KEY_DATA, true)
//            basicActivity.fileAction?.promiseReturn()
//        }

        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    Constants.LOGD(Constants.LOG_LOG_MSG_SMS_SUCCESS)

                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    Constants.LOGD("Message: $message")
                }
                CommonStatusCodes.CANCELED -> {
                    Constants.LOGD(Constants.LOG_MSG_SMS_CANCELED)
                }
                CommonStatusCodes.ERROR -> {
                    Constants.LOGD(Constants.LOG_MSG_SMS_ERROR)
                }
                CommonStatusCodes.TIMEOUT-> {
                    Constants.LOGD(Constants.LOG_MSG_SMS_TIMEOUT)
                }
            }
        }
    }
}