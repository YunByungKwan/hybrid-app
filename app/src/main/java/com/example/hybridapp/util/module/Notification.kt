package com.example.hybridapp.util.module

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.hybridapp.App
import com.example.hybridapp.util.Constants

/**
 * This function is available on API level 26 and higher
 * Badge: >= API level 26
 * API level 26부터 모든 알림은 채널에 할당되어야 함. 그렇지 않으면 알림이 나타나지 않음
 * API level 25 이하는 앱 단위로만 알림을 관리할 수 있음. 각 앱이 채널을 하나만 가짐
 * API level 26 이상에서 알림의 중요도는 알림이 게시된 채널의 importance에 따라 결정됨
 * API level 25 이하에서는 각 알림의 중요도는 알림의 priority에 따라 결정됨
 */

object Notification {

    fun createChannel(importance: Int, showBadge: Boolean,
                                  name: String, descriptionText: String) {
        Constants.LOGE("createChannel", Constants.TAG_NOTIFICATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "${App.activity.packageName}-$name"
            val mChannel = NotificationChannel(channelId, name, importance)
            mChannel.description = descriptionText
            mChannel.setShowBadge(showBadge)

            val notificationManager
                    = App.activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    fun create(channelId: String, icon: Int, title: String,
                           content: String, pendingIntent: PendingIntent) {
        Constants.LOGE("create", Constants.TAG_NOTIFICATION)

        val builder = NotificationCompat.Builder(App.activity, channelId)
        builder.setSmallIcon(icon)
        builder.setContentTitle(title)
        builder.setContentText(content)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        builder.setAutoCancel(true)
        builder.setContentIntent(pendingIntent)

        val notificationManager
                = NotificationManagerCompat.from(App.activity)

        notificationManager.notify(Constants.NOTIFICATION_ID, builder.build())
    }
}