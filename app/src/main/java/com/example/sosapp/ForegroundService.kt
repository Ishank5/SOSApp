package com.example.sosapp

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class SosService : FirebaseMessagingService() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var deviceId: String
    private lateinit var sosDocRef: DocumentReference

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            initializeService()
        }
    }

    @SuppressLint("HardwareIds")
    @RequiresApi(Build.VERSION_CODES.P)
    private fun initializeService() {
        firestore = FirebaseFirestore.getInstance()
        deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        sosDocRef = firestore.collection("sos").document("latest")

        val notification = createNotification()
        startForeground(1, notification)

        val remoteMessage = RemoteMessage.Builder("example_topic")
            .setMessageId("12345")
            .addData("key1", "value1")
            .addData("key2", "value2")
            .build()

        onMessageReceived(remoteMessage)
    }

    private var ringtone: Ringtone? = null

    @SuppressLint("NewApi", "HardwareIds")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data.let { data ->
            val isSosActive = data["triggered"]?.toBoolean() ?: false
            val sosAndroid = data["android"] ?: ""
            val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

            if (isSosActive && sosAndroid != deviceId) {
                startRingtone()
            } else {
                stopRingtone()
            }
            if (!isSosActive) {
                stopRingtone()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun startRingtone() {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(applicationContext, uri)
        ringtone?.isLooping = true
        ringtone?.play()
    }

    private fun stopRingtone() {
        ringtone?.stop()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(): Notification {
        val notificationChannelId = "SOS_SERVICE_CHANNEL"
        val channel = NotificationChannel(notificationChannelId, "SOS Service", NotificationManager.IMPORTANCE_LOW)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("SOS Service")
            .setContentText("Listening for SOS signals")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()
    }

}