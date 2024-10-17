package com.example.sosapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore

class RingtoneForegroundService : Service() {

    private lateinit var ringtone: Ringtone
    private val firestore = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val context = this
        val deviceId = android.provider.Settings.Secure.getString(
            contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        )

        val ringtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtone = RingtoneManager.getRingtone(this, ringtoneUri)
        ringtone.isLooping = true

        // Listen for changes in the Firebase Firestore
        firestore.collection("sos").document("latest")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null || !snapshot.exists()) {
                    return@addSnapshotListener
                }
                val triggered = snapshot.getBoolean("triggered") ?: false
                val eventDeviceId = snapshot.getString("deviceId") ?: ""

                if (triggered && deviceId != eventDeviceId) {
                    if (!ringtone.isPlaying) {
                        ringtone.stop()
                    }
                } else {
                    if (ringtone.isPlaying) {
                        ringtone.stop()
                    }
                }
            }

        createNotificationChannel()

        val notification: Notification = NotificationCompat.Builder(this, "SOS_CHANNEL")
            .setContentTitle("SOS Alert")
            .setContentText("Listening for SOS alerts.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (ringtone.isPlaying) {
            ringtone.stop()
        }
        // Restart service if it gets killed
        val restartServiceIntent = Intent(applicationContext, this::class.java).also {
            it.setPackage(packageName)
        }
        startService(restartServiceIntent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "SOS_CHANNEL",
                "SOS Alert Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
