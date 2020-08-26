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
import com.example.hybridapp.util.module.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class FlexActionInterface {

    /**================================= Toast Interface =========================================*/
    @FlexFuncInterface
    fun Toast(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            val message = array.getString(0)
            val isShortToast = array.getBoolean(1)

            if(isShortToast) {
                Toast.showShortText(message)
            } else {
                Toast.showLongText(message)
            }
        }
    }

    @FlexFuncInterface
    fun ReceiveSMS(array: JSONArray) {
        SMS.receiveMessage()
    }

    /**============================== Notification Interface =====================================*/
    @FlexFuncInterface
    fun Notification(array: JSONArray): Boolean {
        // 알림 채널 생성
        val channelId = App.INSTANCE.getString(R.string.noti_channel_id)
        val channelName = App.INSTANCE.getString(R.string.noti_channel_name)
        val description = App.INSTANCE.getString(R.string.noti_desc)
        val importance = Constants.NOTI_HIGH

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.createChannel(channelId, channelName, description, importance, true)
        } else {
            Utils.LOGE(App.INSTANCE.getString(R.string.log_msg_not_channel))
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

        return true
    }

    /**============================= RootingCheck Interface ======================================*/
    @FlexFuncInterface
    fun RootingCheck(array: JSONArray): String {
        return App.INSTANCE.getString(R.string.msg_no_rooting)
    }

    /**=============================== UniqueAppID Interface =====================================*/
    @FlexFuncInterface
    fun UniqueAppID(array: JSONArray): String {
        return Utils.getAppId()
    }

    /**============================== UniqueDeviceID Interface ===================================*/
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
                Log.e(App.INSTANCE.getString(R.string.tag_main), "${i.id}, ${i.visitingTime}, ${i.visitingUrl}")
            }
        }
    }
}