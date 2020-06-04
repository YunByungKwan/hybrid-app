package com.example.hybridapp.util.module

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.hybridapp.App
import com.example.hybridapp.R
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

    /**
     *  알림 채널 생성
     *  Android 8.0(API 레벨 26, O) 이상부터 필수 생성해야 함
     */
//    fun createChannel(channelId: String, name: String, description: String, importance: Int,
//                      showBadge: Boolean)
//    {
//        Constants.LOGE("createChannel", Constants.TAG_NOTI)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val mChannel = NotificationChannel(channelId, name, importance)
//            mChannel.description = description
//            mChannel.setShowBadge(showBadge)
//
//            val manager = getNotificationManager(App.activity)
//            manager.createNotificationChannel(mChannel)
//        } else {
//            Log.e(Constants.TAG_NOTI, Constants.LOG_MSG_NOT_CHANNEL)
//        }
//    }

    /**
     * 알림 채널 생성
     * Android 8.0(API 레벨 26, O) 이상부터 필수 생성해야 함
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(channelId: String, name: String, description: String, importance: Int,
                       showBadge: Boolean)
    {
        Constants.LOGE("createChannel", Constants.TAG_NOTI)

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
        Constants.LOGE("create", Constants.TAG_NOTI)

        val builder = NotificationCompat.Builder(App.activity, channelId)
        builder.setContentTitle(title)
        builder.setContentText(message)
        builder.priority = importance
        builder.setAutoCancel(true)
        builder.setContentIntent(pendingIntent)
        builder.setSmallIcon(R.drawable.ic_launcher_background)

        val notificationManager
                = NotificationManagerCompat.from(App.activity)

        notificationManager.notify(notificationId, builder.build())
    }
}