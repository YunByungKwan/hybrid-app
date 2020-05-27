package com.example.hybridapp

import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
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

    /** Toast interface */

    @FlexFuncInterface
    fun ShortToast(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            funLOGE(App.INSTANCE.getString(R.string.type_short_toast))

            utils.showShortToast(array.get(0).toString())
        }
    }

    @FlexFuncInterface
    fun LongToast(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            funLOGE(App.INSTANCE.getString(R.string.type_long_toast))

            utils.showLongToast(array.get(0).toString())
        }
    }

    /** Snackbar interface */

    @FlexFuncInterface
    fun ShortSnackbar(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            funLOGE(App.INSTANCE.getString(R.string.type_short_snackbar))

            utils.showShortSnackbar(App.activity.findViewById(R.id.linearLayout),
                array.get(0).toString())
        }
    }

    @FlexFuncInterface
    fun LongSnackbar(array: JSONArray) {
        CoroutineScope(Dispatchers.Main).launch {
            funLOGE(App.INSTANCE.getString(R.string.type_long_snackbar))

            utils.showLongSnackbar(App.activity.findViewById(R.id.linearLayout),
                array.get(0).toString())
        }
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

    @FlexFuncInterface
    fun SaveSharedPreferences(array: JSONArray) {
        funLOGE("SaveSharedPreferences")

        val fileName = array.getString(0)
        val key = array.getString(1)
        val value = array.get(2)

        utils.putDataToPreferences(fileName, key, value)
    }

    @FlexFuncInterface
    fun removeSharedPreferences(array: JSONArray) {
        funLOGE("removeSharedPreferences")

        val fileName = array.getString(0)
        val key = array.getString(1)

        utils.removeDataFromPreferences(fileName, key)
    }

    /** log.e wrapper function */
    private fun funLOGE(functionName: String) {
        Log.e(Constants.TAG_INTERFACE, "call $functionName() in ${Constants.TAG_INTERFACE}")
    }
}