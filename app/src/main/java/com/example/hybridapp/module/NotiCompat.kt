package com.example.hybridapp.module

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import app.dvkyun.flexhybridand.FlexData
import app.dvkyun.flexhybridand.FlexFuncInterface
import app.dvkyun.flexhybridand.FlexInterfaces
import com.example.hybridapp.App
import com.example.hybridapp.MainActivity
import com.example.hybridapp.R
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils

object NotiCompat {

    /** 알림 채널 생성(Android 8.0(API 레벨 26, O) 이상부터 필수 생성해야 함 */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(channelId: String, name: String, description: String, importance: Int, showBadge: Boolean) {
        val mChannel = NotificationChannel(channelId, name, importance)
        mChannel.description = description
        mChannel.setShowBadge(showBadge)

        val nm = getNotificationManager(App.activity)
        nm.createNotificationChannel(mChannel)
    }

    /** NotificationManager */
    private fun getNotificationManager(context: Context): NotificationManager
            = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    /** 알림 생성 */
    fun createNotification(channelId: String, notificationId: Int, title: String, message: String,
                           importance: Int, pendingIntent: PendingIntent) {
        val builder = NotificationCompat.Builder(App.activity, channelId)
        builder.setContentTitle(title)
        builder.setContentText(message)
        builder.priority = importance
        builder.setAutoCancel(true)
        builder.setContentIntent(pendingIntent)
        builder.setSmallIcon(R.drawable.lotte_noti_logo)

        val notificationManager = NotificationManagerCompat.from(App.activity)
        notificationManager.notify(notificationId, builder.build())
    }

    /**============================== Notification Interface =====================================*/

    @FlexFuncInterface
    fun Notification(array: Array<FlexData>): Boolean {
        val channelId = App.INSTANCE.getString(R.string.noti_channel_id)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = App.INSTANCE.getString(R.string.noti_channel_name)
            val description = App.INSTANCE.getString(R.string.noti_desc)
            val importance = Constants.NOTI_HIGH
            createChannel(channelId, channelName, description, importance, true)
        }

        val intent = Intent(App.INSTANCE, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(App.INSTANCE, 0, intent, 0)
        // 알림 생성
        val id = Constants.NOTIFICATION_ID
        val obj = array[0].asMap()!!
        val title = obj["title"]!!.asString()!!
        val message = obj["message"]!!.asString()!!
        createNotification(channelId, id, title, message, Constants.NOTI_HIGH, pendingIntent)

        return true
    }

}