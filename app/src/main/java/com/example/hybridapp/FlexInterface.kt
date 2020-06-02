package com.example.hybridapp

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.app.NotificationManagerCompat
import app.dvkyun.flexhybridand.FlexFuncInterface
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.example.hybridapp.util.module.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

class FlexInterface {

    /**
     * Interface 명: Toast
     * UI Thread에서 실행해야 함
     * @param array 크기 2인 배열
     *        arrray[0]: 토스트 텍스트
     *        array[1]: true(showShortText) or false(showLongText)
     */
    @FlexFuncInterface
    fun Toast(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            if(array.getBoolean(1)) {
                Toast.showShortText(array.get(0).toString())
            } else {
                Toast.showLongText(array.get(0).toString())
            }
        }
    }

    @FlexFuncInterface
    fun ShortSnackbar(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {

            Snackbar.showShortText(App.activity.findViewById(R.id.linearLayout),
                array.get(0).toString())
        }
    }

    @FlexFuncInterface
    fun LongSnackbar(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {

            Snackbar.showLongText(App.activity.findViewById(R.id.linearLayout),
                array.get(0).toString())
        }
    }

    @FlexFuncInterface
    fun SendSMS(array: JSONArray) {
        val phoneNumber = array.getString(0)
        val msg = array.getString(1)

        if(Utils.existAllPermission(arrayOf(Constants.PERM_SEND_SMS))) {
            SMS.sendMessage(phoneNumber, msg)
        } else {
            Utils.requestDangerousPermissions(arrayOf(Constants.PERM_SEND_SMS),
                Constants.REQ_PERM_CODE_RECORD_AUDIO)
        }
    }

    @FlexFuncInterface
    fun ReceiveSMS(array: JSONArray) {
        SMS.receiveMessage()
    }

    @FlexFuncInterface
    fun Notification(array: JSONArray) {
        val importance = NotificationManagerCompat.IMPORTANCE_DEFAULT
        val showBadge = false
        val channelName = "채널1"
        val description = "App notification channel"
        Notification.createChannel(importance, showBadge, channelName, description)

        val channelId = "01040501485"
        val title = "알림 제목"
        val content = "알림 본문입니다."
        val intent = Intent(App.INSTANCE, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent
                = PendingIntent.getActivity(App.INSTANCE, 0, intent, 0)

        Notification.create(channelId,
            R.drawable.ic_launcher_background, title, content, pendingIntent)
    }

    @FlexFuncInterface
    fun InstanceId(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            val instanceId = Utils.getInstanceId()
            Log.e(Constants.TAG_INTERFACE, "Instance id: $instanceId")
        }
    }

    @FlexFuncInterface
    fun GUID(array: JSONArray) {
        val guid = Utils.getGUID()
        Log.e(Constants.TAG_INTERFACE, "GUID: $guid")
    }

    @FlexFuncInterface
    fun SaveSharedPreferences(array: JSONArray) {
        val fileName = array.getString(0)
        val key = array.getString(1)
        val value = array.get(2)

        SharedPreferences.putData(fileName, key, value)
    }

    @FlexFuncInterface
    fun removeSharedPreferences(array: JSONArray) {

        val fileName = array.getString(0)
        val key = array.getString(1)

        SharedPreferences.removeData(fileName, key)
    }

    @FlexFuncInterface
    fun LogUrl(array: JSONArray) {
//        CoroutineScope(Dispatchers.Default).launch {
//            val repository =
//            val logUrls = repository.allLogUrls
//            for(i in logUrls) {
//                Log.e(Constants.TAG_MAIN, "${i.id}, ${i.visitingTime}, ${i.visitingUrl}")
//            }
//        }
    }
}