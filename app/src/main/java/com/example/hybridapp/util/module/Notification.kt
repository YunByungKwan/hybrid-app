package com.example.hybridapp.util.module

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.hybridapp.App
import com.example.hybridapp.R
import com.example.hybridapp.util.Constants

object Notification {

    /**
     * 알림 채널 생성
     * Android 8.0(API 레벨 26, O) 이상부터 필수 생성해야 함
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(channelId: String, name: String, description: String, importance: Int,
                       showBadge: Boolean)
    {
        Constants.LOGD("Create Notification Channel.")

        val mChannel = NotificationChannel(channelId, name, importance)
        mChannel.description = description
        mChannel.setShowBadge(showBadge)

        val manager = getNotificationManager(App.activity)
        manager.createNotificationChannel(mChannel)
    }

    /** NotificationManager */
    private fun getNotificationManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    /** 알림 생성 */
    fun create(channelId: String, notificationId: Int, title: String, message: String,
               importance: Int, pendingIntent: PendingIntent) {
        Constants.LOGD("Create Notification.")

        val builder = NotificationCompat.Builder(App.activity, channelId)
        builder.setContentTitle(title)
        builder.setContentText(message)
        builder.priority = importance
        builder.setAutoCancel(true)
        builder.setContentIntent(pendingIntent)
        builder.setSmallIcon(R.drawable.lotte_noti_logo)

        val notificationManager
                = NotificationManagerCompat.from(App.activity)

        notificationManager.notify(notificationId, builder.build())
    }
}