package com.example.sosapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys


class SosService : Service() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var deviceId: String
    private lateinit var sosDocRef: DocumentReference
    private var ringtone: Ringtone? = null

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            initializeService()
        }
    }

    private var userName: String = "temp"

    @SuppressLint("HardwareIds")
    @RequiresApi(Build.VERSION_CODES.P)
    private fun initializeService() {
        firestore = FirebaseFirestore.getInstance()
        deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        sosDocRef = firestore.collection("sos").document("latest")

        val notification = createNotification()
        startForeground(1, notification)

        observeSosStatus()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun observeSosStatus() {
//        fetchUsernameFromPreferences()

        // Reference to the "latest" document in the "sos" collection
        val sosDocRef = FirebaseFirestore.getInstance().collection("sos").document("latest")

        sosDocRef.addSnapshotListener { snapshot, e ->
            if (e != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener

            val isSosActive = snapshot.getBoolean("triggered") == true
            val sosAndroid = snapshot.getString("android") ?: ""

            if (isSosActive && sosAndroid != deviceId) {
                startRingtone()
            } else {
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
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("SOS Service")
            .setContentText("Listening for SOS signals")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()
    }

    private fun fetchUsernameFromPreferences() {
        val sharedPreferences = getEncryptedSharedPreferences()
        userName = sharedPreferences.getString("username", "temp") ?: "temp"
    }

    private fun getEncryptedSharedPreferences(): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            "user_prefs",
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}
