//package com.example.sosapp
//
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.util.Log
//import androidx.annotation.RequiresApi
//import androidx.work.CoroutineWorker
//import androidx.work.WorkerParameters
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.tasks.await
//
//class SOSWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
//
//    private val deviceId: String = android.provider.Settings.Secure.getString(
//        appContext.contentResolver,
//        android.provider.Settings.Secure.ANDROID_ID
//    )
//
//    @RequiresApi(Build.VERSION_CODES.P)
//    override suspend fun doWork(): Result {
//        val firestore = FirebaseFirestore.getInstance()
//        val context = applicationContext
//
//        try {
//            val snapshot = firestore.collection("sos").document("latest").get().await()
//            val triggered = snapshot.getBoolean("triggered") ?: false
//            val eventDeviceId = snapshot.getString("deviceId") ?: ""
//
//            if (triggered && deviceId != eventDeviceId) {  // Only play ringtone if the deviceId doesn't match
//                Log.d("SOSWorker", "SOS Triggered on another device!")
//
//                // Start the foreground service to play the ringtone
//                val serviceIntent = Intent(context, RingtoneForegroundService::class.java)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    context.startForegroundService(serviceIntent)
//                } else {
//                    context.startService(serviceIntent)
//                }
//            } else {
//                // Stop the foreground service if the SOS event is stopped
//                val serviceIntent = Intent(context, RingtoneForegroundService::class.java)
//                context.stopService(serviceIntent)
//            }
//
//            return Result.success()
//
//        } catch (e: Exception) {
//            Log.e("SOSWorker", "Error checking SOS status", e)
//            return Result.retry()
//        }
//    }
//}
