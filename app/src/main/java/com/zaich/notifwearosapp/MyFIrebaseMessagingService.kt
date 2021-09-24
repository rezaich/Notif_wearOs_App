package com.zaich.notifwearosapp

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService:FirebaseMessagingService() {
    private val channelId = "channel_id_example_01"
    private val notificationId = 101

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i(TAG, " From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()){
            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]
            showNotification(applicationContext,title,body)
        }
        else{
            val title = remoteMessage.notification!!.title
            val body = remoteMessage.notification!!.body
            showNotification(applicationContext,title,body)
        }
        remoteMessage.notification.let {
            Log.d(TAG,"Message Notification Body: ${it?.body}")
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(TAG, "Refreshed token: $p0")
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(context: Context, title:String?, message: String?){
        var ii  = Intent()
        ii = Intent(context,MainActivity::class.java)
        ii.data = Uri.parse("custom://"+System.currentTimeMillis())
        ii.action = "actionstring" + System.currentTimeMillis()
        ii.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pi = PendingIntent.getActivity(context,0, ii,
            PendingIntent.FLAG_UPDATE_CURRENT)
        val notification : Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notification = NotificationCompat.Builder(context,channelId)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title).build()
            val notificationManager  = context.getSystemService(
                NOTIFICATION_SERVICE
            )as NotificationManager
            val notificationChannel = NotificationChannel(
                channelId,title,NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
            notificationManager.notify(notificationId,notification)
        }else{
            notification = Notification.Builder(context)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pi)
                .setContentText(message)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title).build()

            val notificationManager = context.getSystemService(
                NOTIFICATION_SERVICE
            )as NotificationManager
            notificationManager.notify(notificationId,notification)
        }
    }
}