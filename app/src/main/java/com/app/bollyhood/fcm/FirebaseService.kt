package com.app.bollyhood.fcm

import android.Manifest
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.bollyhood.R
import com.app.bollyhood.activity.ChatActivity
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import java.util.Random

class FirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("fcmToken", "=$token")
        PrefManager(this).setvalue(StaticData.fcmToken, token)
    }


    override fun onMessageReceived(message: RemoteMessage) {

        Log.e("RemoteMessage", "${message.data}")
        message.data.isNotEmpty().let {
            try {
                val json = JSONObject(message.data as Map<*, *>)
                if (PrefManager(this).getvalue(StaticData.isLogin, false)) {
                    if (isAppIsInBackground(this)) {
                        prepareNotification(json, StaticData.killed)
                        //Loger.LogError("Constant.killed", Constant.killed)
                    } else {
                        prepareNotification(json, StaticData.foregrounded)
                        //Loger.LogError("Constant.killed", Constant.foregrounded)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        super.onMessageReceived(message)

    }


    private fun prepareNotification(jsonobject: JSONObject, appStatus: String) {
        val notificationId = randInt(0, 999)
        val type = jsonobject.optString("type")
        var intent: Intent? = null
        var pendingIntent: PendingIntent? = null

        when (type) {


            "chat" -> {
                try {
                    if (appStatus == StaticData.killed) {
                        val broadCastIntent = Intent("sendData")
                        broadCastIntent.putExtra("id", jsonobject.optString("uid"))
                        broadCastIntent.putExtra("other_uid", jsonobject.optString("other_uid"))


                        LocalBroadcastManager.getInstance(this).sendBroadcast(
                            broadCastIntent
                        )

                        intent = Intent(this, ChatActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.putExtra("isNotifications",true)
                        intent.putExtra("id", jsonobject.optString("uid"))
                        intent.putExtra("other_uid", jsonobject.optString("other_uid"))

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            pendingIntent = PendingIntent.getActivity(
                                this,
                                notificationId,
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                            )
                        } else {
                            pendingIntent = PendingIntent.getActivity(
                                this,
                                notificationId,
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                            )
                        }
                        showNotificationAndClick(pendingIntent, type, jsonobject, notificationId)
                    } else {
                        val intent = Intent("sendData")
                        intent.putExtra("id", jsonobject.optString("uid"))
                        intent.putExtra("other_uid", jsonobject.optString("other_uid"))
                        showNotification(type, jsonobject, notificationId)
                        LocalBroadcastManager.getInstance(this).sendBroadcast(
                            intent
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }


            else -> {
                showNotification(type, jsonobject, notificationId)
            }
        }
    }


    private fun showNotification(
        channelId: String,
        messageBody: JSONObject,
        notificationId: Int
    ) {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.applogo)
            .setColor(ContextCompat.getColor(this, R.color.colorred))
            .setContentTitle(messageBody.optString("title"))
            .setContentText(messageBody.optString("body"))
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(messageBody.optString("body"))
            )
            .setSound(defaultSoundUri)


        val notificationManager = NotificationManagerCompat.from(applicationContext)

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.lightColor = ContextCompat.getColor(this, R.color.colorred)
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(
                100,
                200,
                300,
                400,
                500,
                400,
                300,
                200,
                400
            )
            notificationManager.createNotificationChannel(channel)
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }else {
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }


    private fun randInt(min: Int, max: Int): Int {
        val rand = Random()
        return rand.nextInt(max - min + 1) + min
    }

    private fun showNotificationAndClick(
        pendingIntent: PendingIntent?,
        channelId: String,
        messageBody: JSONObject,
        notificationId: Int
    ) {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.applogo)
            .setColor(ContextCompat.getColor(this, R.color.colorred))
            .setContentTitle(messageBody.optString("title"))
            .setContentText(messageBody.optString("body"))
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(messageBody.optString("body"))
            )
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(applicationContext)

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.lightColor = ContextCompat.getColor(this, R.color.colorred)
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(
                100,
                200,
                300,
                400,
                500,
                400,
                300,
                200,
                400
            )
            notificationManager.createNotificationChannel(channel)
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true
        val am: ActivityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            val runningProcesses: List<ActivityManager.RunningAppProcessInfo> =
                am.runningAppProcesses
            for (processInfo in runningProcesses) {
                if (processInfo.importance === ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (activeProcess in processInfo.pkgList) {
                        if (activeProcess == context.packageName) {
                            isInBackground = false
                        }
                    }
                }
            }
        } else {
            try {
                val taskInfo: List<ActivityManager.RunningTaskInfo> = am.getRunningTasks(1)
                val componentInfo: ComponentName? = taskInfo[0].topActivity
                if (componentInfo?.packageName == context.packageName) {
                    isInBackground = false
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
        return isInBackground
    }


}