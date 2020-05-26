package com.example.hybridapp

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.app.NotificationManagerCompat
import app.dvkyun.flexhybridand.FlexFuncInterface
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

class FlexInterface {

    private val utils = Utils()

    @FlexFuncInterface
    fun ShortToast(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            funLOGE("ShortToast")

            utils.showShortToast(array.toString())
        }
    }

    @FlexFuncInterface
    fun LongToast(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            funLOGE("LongToast")

            utils.showLongToast(array.toString())
        }
    }

    @FlexFuncInterface
    fun ShortSnackbar(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            funLOGE("ShortToast")

            utils.showShortSnackbar(App.activity.findViewById(R.id.linearLayout), array.toString())
        }
    }

    @FlexFuncInterface
    fun LongSnackbar(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            funLOGE("LongToast")

            utils.showLongSnackbar(App.activity.findViewById(R.id.linearLayout), array.toString())
        }
    }

    @FlexFuncInterface
    fun Dialog(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            Log.e(TAG, "call Dialog() in $TAG")

            val title = array.getString(0)
            val contents = array.getString(1)

            utils.showDialog(title, contents)
        }
    }

    @FlexFuncInterface
    fun extendedDialog(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            Log.e(TAG, "call extendedDialog() in $TAG")

            utils.showDialog("제목", "다이얼로그 메시지입니다.", "긍정", "부정")
        }
    }

    @FlexFuncInterface
    fun Network(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            funLOGE("Network")

            if(utils.isNetworkConnected()) {
                utils.showShortToast("네트워크가 연결되어 있습니다.")
            } else {
                utils.showShortToast("네트워크가 연결되어 있지 않습니다.")
            }
        }
    }

    @FlexFuncInterface
    fun NetworkStatus(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            funLOGE("NetworkStatus")

            when (utils.getNetworkStatus()) {
                0 -> {
                    utils.showShortToast("네트워크가 연결되어 있지 않습니다.")
                }
                1 -> {
                    utils.showShortToast("데이터에 연결되어 있습니다.")
                }
                2 -> {
                    utils.showShortToast("와이파이에 연결되어 있습니다.")
                }
            }
        }
    }

    @FlexFuncInterface
    fun QRCodeScan(array: JSONArray) {
        funLOGE("QRCodeScan")

        utils.takeQRCodeReader()
    }

    @FlexFuncInterface
    fun Notification(array: JSONArray) {
        funLOGE("Notification")

        val importance = NotificationManagerCompat.IMPORTANCE_DEFAULT
        val showBadge = false
        val channelName = "알림"
        val description = "App notification channel"
        utils.createNotificationChannel(importance, showBadge, channelName, description)

        val channelId = "01040501485"
        val title = "알림 제목"
        val content = "알림 본문입니다."
        val intent = Intent(App.INSTANCE, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent
                = PendingIntent.getActivity(App.INSTANCE, 0, intent, 0)

        utils.createNotification(channelId,
            R.drawable.ic_launcher_background, title, content, pendingIntent)
    }

    @FlexFuncInterface
    fun InstanceId(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            funLOGE("InstanceId")

            val instanceId = utils.getInstanceId()
            Log.e(Constants.TAG_INTERFACE, "Instance id: $instanceId")
        }
    }

    @FlexFuncInterface
    fun GUID(array: JSONArray) {
        funLOGE("GUID")

        val guid = utils.getGUID()
        Log.e(Constants.TAG_INTERFACE, "GUID: $guid")
    }

    /** log.e wrapper function */
    private fun funLOGE(functionName: String) {
        Log.e(Constants.TAG_INTERFACE, "call $functionName() in ${Constants.TAG_INTERFACE}")
    }

    companion object {
        private const val TAG = "FlexInterface"
    }
}