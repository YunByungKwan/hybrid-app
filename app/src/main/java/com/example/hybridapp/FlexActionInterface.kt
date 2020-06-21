package com.example.hybridapp

import android.app.DownloadManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import app.dvkyun.flexhybridand.FlexFuncInterface
import com.example.hybridapp.basic.BasicActivity
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

    /**============================= RootingCheck Interface ======================================*/
    @FlexFuncInterface
    fun RootingCheck(array: JSONArray): String {
        return Constants.MSG_NO_ROOTING
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

    /**============================== FileDownload Interface =====================================*/
    @FlexFuncInterface
    fun FileDownload(array: JSONArray) {
        Constants.LOGD("Call FileDownload Interface")

        val basicActivity = App.activity as BasicActivity
        basicActivity.fileUrl = array?.getString(0)

        val perms = arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE)

        // 권한이 다 있을 경우
        if(Utils.existAllPermission(perms)) {
            Utils.downloadFileFromUrl()
        }
        // 권한이 다 있지 않을 경우
        else {
            Utils.checkAbsentPerms(perms, Constants.PERM_FILE_REQ_CODE,
                null)
        }
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