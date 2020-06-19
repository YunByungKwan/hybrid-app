package com.example.hybridapp.util.module

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import com.example.hybridapp.MainActivity
import com.example.hybridapp.util.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * FCM: https://console.firebase.google.com/project/hybridfcm-161a7/notification
 * Reference: https://blog.naver.com/ndb796/221553341369
 */

class FCM: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Constants.LOGD("Call onNewToken() in FCM class.")
        Constants.LOGD("Token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Constants.LOGD("Call onMessageReceived() in FCM class.")

        if(remoteMessage.notification != null) {
            val channelId = Constants.NOTI_CHANNEL_ID
            val channelName = Constants.NOTI_CHANNEL_NAME
            val description = Constants.NOTI_DESC
            val importance = Constants.NOTI_DEFAULT

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.createChannel(channelId, channelName, description,
                    importance, true)
            } else {
                Constants.LOGE(Constants.LOG_MSG_NOT_CHANNEL)
            }

            val title = remoteMessage.notification!!.title
            val message = remoteMessage.notification!!.body
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            Notification.create(channelId, Constants.NOTIFICATION_ID, title!!, message!!,
                importance, pendingIntent)

            Constants.LOGD("Notification Title: $title, message: $message")
        }
    }
}