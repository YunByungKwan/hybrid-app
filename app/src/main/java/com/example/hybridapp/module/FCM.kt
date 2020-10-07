package com.example.hybridapp.module

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import com.example.hybridapp.MainActivity
import com.example.hybridapp.R
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * FCM: https://console.firebase.google.com/project/hybridfcm-161a7/notification
 * Reference: https://blog.naver.com/ndb796/221553341369
 */

class FCM: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Utils.LOGD("Call onNewToken() in FCM class.")
        Utils.LOGD("Token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Utils.LOGD("Call onMessageReceived() in FCM class.")

        if(remoteMessage.notification != null) {
            val channelId = getString(R.string.noti_channel_id)
            val channelName = getString(R.string.noti_channel_name)
            val description = getString(R.string.noti_desc)
            val importance = Constants.NOTI_DEFAULT

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.createChannel(channelId, channelName, description,
                    importance, true)
            } else {
                Utils.LOGE(getString(R.string.log_msg_not_channel))
            }

            val title = remoteMessage.notification!!.title
            val message = remoteMessage.notification!!.body
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            Notification.create(channelId, Constants.NOTIFICATION_ID, title!!, message!!,
                importance, pendingIntent)

            Utils.LOGD("Notification Title: $title, message: $message")
        }
    }
}