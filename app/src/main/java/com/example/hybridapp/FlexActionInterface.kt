package com.example.hybridapp

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import app.dvkyun.flexhybridand.FlexData
import app.dvkyun.flexhybridand.FlexFuncInterface
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.data.LogUrlRepository
import com.example.hybridapp.data.LogUrlRoomDatabase
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.example.hybridapp.module.*
import kotlinx.coroutines.*

class FlexActionInterface {

    val basicActivity = App.activity as BasicActivity

    @FlexFuncInterface
    suspend fun ReceiveSMS2(array: Array<FlexData>): Map<String, *> = withContext(Dispatchers.Main) {
        HashMap<String, Any>()
    }

    /**================================= Toast Interface =========================================*/
    @FlexFuncInterface
    fun Toast(array: Array<FlexData>) {
        CoroutineScope(Dispatchers.Main).launch {
            val message = array[0].asString()!!
            val isShortToast = array[1].asBoolean()!!

            if(isShortToast) {
                (App.activity as BasicActivity).toast!!.showShortText(message)
            } else {
                (App.activity as BasicActivity).toast!!.showLongText(message)
            }
        }
    }

    @FlexFuncInterface
    fun ReceiveSMS(array: Array<FlexData>) {
        basicActivity.sms!!.receiveMessage()
    }

    /**============================== Notification Interface =====================================*/
    @FlexFuncInterface
    fun Notification(array: Array<FlexData>): Boolean {
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
        val obj = array[0].asMap()!!
        val title = obj["title"]!!.asString()!!
        val message = obj["message"]!!.asString()!!
        Notification.create(channelId, id, title, message, Constants.NOTI_HIGH, pendingIntent)

        return true
    }

    /**============================= RootingCheck Interface ======================================*/
    @FlexFuncInterface
    fun RootingCheck(array: Array<FlexData>): String {
        return App.INSTANCE.getString(R.string.msg_no_rooting)
    }

    /**=============================== UniqueAppID Interface =====================================*/
    @FlexFuncInterface
    fun UniqueAppID(array: Array<FlexData>): String {
        return Utils.getAppId()
    }

    /**============================== UniqueDeviceID Interface ===================================*/
    @FlexFuncInterface
    fun UniqueDeviceID(array: Array<FlexData>): String {
        return Utils.getDeviceId(App.INSTANCE)
    }

    @FlexFuncInterface
    fun LogUrl(array: Array<FlexData>) {
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