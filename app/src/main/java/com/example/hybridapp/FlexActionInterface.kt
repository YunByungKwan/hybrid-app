package com.example.hybridapp

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import app.dvkyun.flexhybridand.FlexFuncInterface
import com.example.hybridapp.data.LogUrlRepository
import com.example.hybridapp.data.LogUrlRoomDatabase
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.example.hybridapp.util.module.SMS
import com.example.hybridapp.util.module.Toast
import com.example.hybridapp.util.module.Snackbar
import com.example.hybridapp.util.module.Notification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class FlexActionInterface {
    @FlexFuncInterface
    fun Toast(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            val isShortToast = array.getBoolean(1)
            val message = array.getString(0)

            if(isShortToast)
                Toast.showShortText(message)
            else
                Toast.showLongText(message)
        }
    }

    @FlexFuncInterface
    fun Snackbar(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            val isShortSnackbar = array.getBoolean(1)
            val message = array.getString(0)

            if(isShortSnackbar)
                Snackbar.showShortText(App.activity.findViewById(R.id.constraintLayout), message)
            else
                Snackbar.showLongText(App.activity.findViewById(R.id.constraintLayout), message)
        }
    }

    @FlexFuncInterface
    fun SendSMS(array: JSONArray): String {
        // val phoneNumber = array.getString(0)
        val phoneNumber = "01065720153"
        val message = array.getString(1)

        return SMS.sendMessage(phoneNumber, message)
    }

    @FlexFuncInterface
    fun ReceiveSMS(array: JSONArray) {
        SMS.receiveMessage()
    }

    @FlexFuncInterface
    fun Notification(array: JSONArray) {
        // 알림 채널 생성
        val channelId = Constants.NOTI_CHANNEL_ID
        val channelName = Constants.NOTI_CHANNEL_NAME
        val description = Constants.NOTI_DESC
        val importance = Constants.NOTI_HIGH

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.createChannel(channelId, channelName, description, importance, true)
        } else {
            Constants.LOGE(Constants.LOG_MSG_NOT_CHANNEL)
        }

        val intent = Intent(App.INSTANCE, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(App.INSTANCE, 0,
            intent, 0)

        // 알림 생성
        val id = Constants.NOTIFICATION_ID
        val obj = array.get(0) as JSONObject
        val title = obj.get("title").toString()
        val message = obj.get("message").toString()
        Notification.create(channelId, id, title, message, Constants.NOTI_HIGH, pendingIntent)
    }

    @FlexFuncInterface
    fun RootingCheck(array: JSONArray): String {
        return "루팅이 확인되지 않았습니다."
    }

    @FlexFuncInterface
    fun UniqueAppID(array: JSONArray): String {
        return Utils.getAppId()
    }

    @FlexFuncInterface
    fun UniqueDeviceID(array: JSONArray): String {
        return Utils.getDeviceId(App.INSTANCE)
    }

    @FlexFuncInterface
    fun LogUrl(array: JSONArray) {
        CoroutineScope(Dispatchers.Default).launch {
            val logUrlDao = LogUrlRoomDatabase.getDatabase(App.INSTANCE).logUrlDao()
            val repository = LogUrlRepository(logUrlDao)
            val logUrls = repository.allLogUrls
            for(i in logUrls) {
                Log.e(Constants.TAG_MAIN, "${i.id}, ${i.visitingTime}, ${i.visitingUrl}")
            }
        }
    }
}