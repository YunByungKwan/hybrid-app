package com.example.hybridapp.util.module

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.hybridapp.MainActivity
import com.example.hybridapp.R
import com.example.hybridapp.util.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * FCM: https://console.firebase.google.com/project/hybridfcm-161a7/notification
 * Reference: https://blog.naver.com/ndb796/221553341369
 */

class FCM: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Constants.LOGE("onNewToken", Constants.TAG_FCM_SERVICE)

        Log.e(Constants.TAG_FCM_SERVICE, "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Constants.LOGE("onMessageReceived", Constants.TAG_FCM_SERVICE)

        if(remoteMessage.notification != null) {
            Log.e(Constants.TAG_FCM_SERVICE, "알림 메시지: ${remoteMessage.notification!!.body}")

            val channelId = Constants.NOTI_CHANNEL_ID
            val channelName = Constants.NOTI_CHANNEL_NAME
            val description = Constants.NOTI_DESC
            val importance = Constants.NOTI_DEFAULT

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.createChannel(channelId, channelName, description,
                    importance, true)
            } else {
                Log.e(Constants.TAG_FCM_SERVICE, Constants.LOG_MSG_NOT_CHANNEL)
            }

            val title = remoteMessage.notification!!.title
            val message = remoteMessage.notification!!.body
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            Notification.create(channelId, Constants.NOTIFICATION_ID, title!!, message!!,
                importance, pendingIntent)
        }
    }
}