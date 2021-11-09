package com.example.seabuckcafe.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.seabuckcafe.MainActivity
import com.example.seabuckcafe.R
import com.example.seabuckcafe.utils.Constants

class NotificationService: Service() {

    private lateinit var ringtone: MediaPlayer

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initRingtone()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification()
        return START_STICKY
    }

    override fun onDestroy() {
        ringtone.stop()
    }

    private fun showNotification() {

        // This code available works in notification's tap action for using in fragment class
        val pendingIntent = NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.homeAdminFragment)
            .createPendingIntent()

        // Notification setting
        val builder = NotificationCompat.Builder(this, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.coffee_logo)
            .setContentTitle("You has a new order")
            .setContentText("Please accept as fast as possible")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(123, builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name: CharSequence = "Order Reminder Channel"
            val descriptionText = "Channel For Order Manager"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initRingtone() {
        ringtone = MediaPlayer.create(this, R.raw.ringbell)
        if (!ringtone.isLooping) {
            ringtone.isLooping = true
            ringtone.start()
        }
    }
}