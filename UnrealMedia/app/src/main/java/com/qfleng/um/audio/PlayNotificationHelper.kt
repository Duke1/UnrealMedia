package com.qfleng.um.audio

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import com.qfleng.um.R
import android.app.NotificationManager
import android.os.Build
import androidx.media.session.MediaButtonReceiver


object PlayNotificationHelper {
    private const val CHANNEL_ID = "com.qfleng.um.audio.MUSIC_PLAY_CHANNEL"

    @SuppressLint("NewApi")
    fun generateNotification(context: Context, mediaSession: MediaSessionCompat): NotificationCompat.Builder {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val controller = mediaSession.controller
        val mediaMetadata = controller.metadata
        val description = mediaMetadata.description

        var builder: NotificationCompat.Builder? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "MUSIC_PLAY_CHANNEL", NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableLights(true) //开启指示灯
            channel.lightColor = context.resources.getColor(R.color.colorPrimary) //指示灯颜色
            channel.setShowBadge(true) //显示角标
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE//锁定屏幕上显示此频道的通知
            channel.description = context.getString(R.string.app_name)//设置渠道描述
            channel.enableVibration(false)
            //channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 600)//震动频率
            channel.setBypassDnd(true)//绕过免打扰模式
            notificationManager.createNotificationChannel(channel)
//            createNotificationChannelGroups()
//            setNotificationChannelGroups(channel)
            builder = NotificationCompat.Builder(context, CHANNEL_ID)
            //builder.setBadgeIconType(BADGE_ICON_SMALL)//角标的样式
            builder.setNumber(3)//角标的数量
        } else {
            builder = NotificationCompat.Builder(context)
        }


        builder.setContentTitle(description.title)
                .setContentText(description.subtitle)
                .setSubText(description.description)
                .setLargeIcon(description.iconBitmap)
                .setContentIntent(controller.sessionActivity)
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setShowWhen(false)
                .setOngoing(true)
                .setAutoCancel(false)

        builder.addAction(R.mipmap.ic_skip_previous_white_36dp,
                "Previous",
                MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))

        builder.addAction(R.mipmap.ic_pause_white_36dp,
                "Pause",
                MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY_PAUSE))
        builder.addAction(R.mipmap.ic_skip_next_white_36dp,
                "Next",
                MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT))

        builder.setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2)
                .setMediaSession(mediaSession.sessionToken))
        builder.setSmallIcon(R.mipmap.ic_launcher)

        return builder
    }


    @SuppressLint("NewApi")
    fun deleteNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.deleteNotificationChannel(CHANNEL_ID)
        }
    }
}